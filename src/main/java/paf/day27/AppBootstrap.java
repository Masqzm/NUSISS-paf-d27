package paf.day27;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonReader;

import paf.day27.repo.CommentsRepo;

@Component
public class AppBootstrap implements CommandLineRunner {
    @Autowired
	private CommentsRepo commentsRepo;
    
    // mvn clean spring-boot:run -D spring-boot.run.arguments="--load=mongoDB_data/comments.json"
    @Override
    public void run(String... args) throws Exception {
		if(args.length > 0 && args[0].startsWith(Constants.CLI_PREFIX_LOAD))
            loadFile(args[0].split("=")[1]);
    }

	private void loadFile(String path) throws Exception {
		File commentsFile = new File(path);
        
		// If file/path is invalid
		if(!commentsFile.exists() && !commentsFile.isFile())
			return;

		StringBuilder sbFilename = new StringBuilder(commentsFile.getName());
		String collectionName = sbFilename.delete(sbFilename.indexOf("."), sbFilename.length()).toString();
		
        // Drop collection
		System.out.println(">>> Dropping " + collectionName + " collection");
		commentsRepo.dropCollection(collectionName);

        // Insert to collection from .json file
		try (Reader r = new FileReader(commentsFile)) {
            BufferedReader br = new BufferedReader(r);

            System.out.println("### Reading JSON file: %s".formatted(commentsFile.getName()));
            JsonReader jsonReader = Json.createReader(br);
            JsonArray arr = jsonReader.readArray();

            br.close();
            
            System.out.println("### Procesing JSON documents: %d".formatted(arr.size()));
            
            // Insertion over iteration
            // arr.stream()                                        // Converts the JSON array into a stream to process elements one by one
            //     .map(j -> Document.parse(j.toString()))         // Converts each JSON obj (j) into a Document obj (returned), mapped back to the stream element
            //     .map(d ->  {                                    
            //         d.put("_id", d.getString("c_id"));          // Adds _id field from c_id to Document d
            //         d.remove("c_id");                           // Removes "c_id" from the Document d
            //         return d;
            //     })
            //     .forEach(d -> {
            //         // Batch insert is more efficient
            //         commentsRepo.insertComment(d, collectionName);
            //     });

            // Batch insertion
            List<Document> documents = arr.stream()
                                        .map(j -> Document.parse(j.toString()))
                                        .map(d ->  {                                    
                                            d.put("_id", d.getString("c_id"));
                                            d.remove("c_id");
                                            return d;
                                        })
                                        .collect(Collectors.toList());

            commentsRepo.insertCommentMany(documents, collectionName);
            
            commentsRepo.createTextIndex("c_text", collectionName);
        }
	}
}
