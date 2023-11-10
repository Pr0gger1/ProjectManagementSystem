package ru.sfedu.projectmanager.api;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import ru.sfedu.projectmanager.Constants;
import ru.sfedu.projectmanager.model.HistoryRecord;

public class MongoHistoryProvider {
    public static <T> void save(String dbName, HistoryRecord<T> record) {
        MongoCollection<Document> collection = getCollection(dbName, record.getObject().getClass());
        collection.insertOne(record.convertToDocument());
    }

    public static MongoCollection<Document> getCollection(String dbName, Class<?> obj) {
        return getCollection(dbName, obj.getSimpleName().toLowerCase());
    }

    public static MongoCollection<Document> getCollection(String dbName, String collectionName) {
        String mongoUrl = Constants.configPropertiesProvider.getEnvironmentVariable("MONGO_URL");
        MongoClient mongoClient = MongoClients.create(mongoUrl);
        MongoDatabase db = mongoClient.getDatabase(dbName);

        return db.getCollection(collectionName.toLowerCase());
    }
}
