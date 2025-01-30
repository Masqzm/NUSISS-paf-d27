package paf.day27.repo;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Indexes;

@Repository
public class CommentsRepo {
    @Autowired
    private MongoTemplate template;

    // db.comments.drop()
    public void dropCollection(String name) {
        template.dropCollection(name);
    }

    /* 
        db.comments.insert({
            <doc>
        })
    */
    // Insertion of single comment
    public <T> T insertComment(T doc, String collectionName) {
        return template.insert(doc, collectionName);            // also returns obj inserted
    }

    /* 
        db.comments.insertMany([
            <doc>,
            <doc>,
            ...
        ])
    */
    // Insertion comments in bulk
    public <T> List<T> insertCommentMany(List<T> docs, String collectionName) {
        return new ArrayList<>(template.insert(docs, collectionName));
    }


    /* 
        db.comments.createIndex({
            fieldName: "text"
        })
    */
    // Create text index of given field
    public void createTextIndex(String fieldName, String collectionName) {
        template.getCollection(collectionName).createIndex(Indexes.text(fieldName));
    }

    // Day27 demo (index terms search)
    // -------------------------------
    /*
        db.comments.createIndex({
            c_text: "text"
        })

        db.comments.find(
            {
                $text: { $search: <terms> }
            },
            {
                score: {
                    $meta: "weight"    
                }
            }
        )
        .sort({ score: { $meta: "weight" } })
        .limit(10)
        .projection({
            c_text: 1,
            weight: 1
        })
    */
    public List<Document> searchComments(String... terms) {
        TextCriteria criteria = TextCriteria.forDefaultLanguage()
                                .matchingAny(terms);
        
        TextQuery query = (TextQuery) TextQuery.queryText(criteria)
                        .sortByScore()
                        .includeScore("weight")
                        .limit(10);
        
        query.fields().include("c_text", "weight");

        return template.find(query, Document.class, "comments");
    }
}
