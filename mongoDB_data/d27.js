// Insertion
db.tasks.insert({
    name: "Jogging",
    priority: 1
})

db.tasks.insertMany([
    { 
        name: "CNY shopping",
        priority: 1
     },
    { 
        name: "Visiting",
        priority: 3
     }
])

// Delete all with following conditions
db.tasks.deleteMany(
    { priority: { $gte: 3} }
)

// Delete all
// db.tasks.deleteMany({})

db.tasks.find({})

// Addition of fields to certain records
db.tasks.updateOne(
    // Filter conditions
    { name: { $regex: 'jogging', $options: 'i' } },
    
    // Update properties
    {   
        $set: { venue: 'Labrador Park' },
        $inc: { priority: 1 }                // increment
    }
)

db.tasks.updateMany(
    { _id: ObjectId('6792ed3d589383b557072f8f') },
    {
        // $pop: 1,    // removes from the back. Note: can't do both push & pop in one operation!
        $push: 
        { 
            jog_with: { $each: [ 'fred', 'wilma'] }
        }
    }
)

db.tasks.find({})

// Upsertion - inserts record if not found, updates otherwise
db.tasks.update(
    { name: { $regex: 'jog', $options: 'i' } },
    {
        $set: {
            time: 'AM',
            date: 'Jan 20 2025',
            name: 'Jogging with friends'
        },
        $push: { jog_with: 'fred' }
    },
    { upsert: true }
)

db.tasks.find({
    name: { $regex: 'jogg', $options: 'i' }
})

db.tasks.update(
    { name: { $regex: 'running', $options: 'i' } },
    {
        $set: {
            time: 'AM',
            date: 'Jan 20 2025',
            name: 'Running with friends'
        },
        $push: { run_with: 'fred' }
    },
    { upsert: true }
)

db.tasks.find({
    name: { $regex: 'run', $options: 'i' }
})



// Using D26 shows db
db.series.findOne()

// Indexing demo
db.series.createIndex({
    name: 1
})

db.series.find({
    name: {
        // ^ - start of str, $ - end of str
        $regex: '^under',
        $options: 'i'
    }
})



// Using D26 BGG db
// Text index demo
db.comments.createIndex({
    c_text: "text"
})

db.comments.getIndexes()

// Searching thru "text" index created
db.comments.find({
    $text: {
        // search & return records with c_text containing any of the str given
        // search phrase by encapsulating it with ""
        $search: 'love enjoy fun "love it"'    
    }
})

db.comments.find(
    {
        $text: { $search: 'hate crap avoid' }
    },
    {
        // Shows how close text is to the search terms
        score: {
            $meta: "textScore"    
        }
    }
)
.sort({ score: { $meta: "textScore" } })
.limit(5)


db.comments.drop()