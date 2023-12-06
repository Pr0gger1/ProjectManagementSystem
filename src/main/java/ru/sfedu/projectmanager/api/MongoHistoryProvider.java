package ru.sfedu.projectmanager.api;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import ru.sfedu.projectmanager.Constants;
import ru.sfedu.projectmanager.model.HistoryRecord;
import ru.sfedu.projectmanager.utils.ConfigPropertiesUtil;

public class MongoHistoryProvider {
    private static final Logger logger = LogManager.getLogger(MongoHistoryProvider.class);

    /**
     * Method that saves entity history record in Mongo db
     * @param record history record object
     * @param <T> type of history record
     */
    public static <T> void save(HistoryRecord<T> record) {
        String dbName = Environment.valueOf(ConfigPropertiesUtil.getEnvironmentVariable(Constants.ENVIRONMENT)) == Environment.TEST ?
                Constants.MONGO_DB_NAME_TEST : Constants.MONGO_DB_NAME_PRODUCTION;
        try {
            logger.debug("save[0]: record{\n{}\n}", record.toString());
            MongoCollection<Document> collection = getCollection(dbName, record.getObject().getClass());
            collection.insertOne(record.convertToDocument());
        }
        catch (IllegalArgumentException | NullPointerException exception) {
            logger.error(exception);
        }
    }

    /**
     * Method that gets collection of database with entity class name
     * @param dbName database name
     * @param obj object whose name is used as a collection name
     * @return MongoDB document
     */
    public static MongoCollection<Document> getCollection(String dbName, Class<?> obj) throws IllegalArgumentException, NullPointerException {
        return getCollection(dbName, obj.getSimpleName().toLowerCase());
    }

    /**
     * Method that gets collection of database with entity class name
     * @param dbName database name
     * @param collectionName collection name
     * @return MongoDB document
     */
    public static MongoCollection<Document> getCollection(String dbName, String collectionName) throws IllegalArgumentException, NullPointerException {
        String mongoUrl = ConfigPropertiesUtil.getEnvironmentVariable(Constants.MONGO_URL);
        logger.debug("getCollection[1]: mongo URL: {}", mongoUrl);

        MongoClient mongoClient = MongoClients.create(mongoUrl);
        MongoDatabase db = mongoClient.getDatabase(dbName);
        logger.debug("getCollection[2]: mongo database name is {}", db.getName());

        return db.getCollection(collectionName.toLowerCase());
    }
}
