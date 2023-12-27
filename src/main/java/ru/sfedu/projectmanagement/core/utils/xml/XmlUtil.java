package ru.sfedu.projectmanagement.core.utils.xml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.projectmanagement.core.model.*;
import ru.sfedu.projectmanagement.core.model.enums.EntityType;

import java.io.File;
import java.io.StringWriter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class XmlUtil {
    private static final Logger logger = LogManager.getLogger(XmlUtil.class);
    private static Marshaller marshaller;
    private static JAXBContext context;

    static {
        try {
            context = JAXBContext.newInstance(
                    Wrapper.class,
                    Project.class,
                    BugReport.class,
                    Task.class,
                    Employee.class,
                    Event.class,
                    Documentation.class,
                    EmployeeProjectObject.class
            );

            marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        } catch (JAXBException e) {
            logger.error("XmlUtil: {}", e.getMessage());
        }
    }

    /**
     * @param filePath path of the xml file
     * @param id id of the project whose existence need to check
     * @param <T> T type of Entity implemented object
     * @return true if object with such id exists else false
     */
    public static <T extends Entity> boolean isRecordNotExists(String filePath, UUID id) {
        Wrapper<T> wrapper = XmlUtil.readFile(filePath);
        if (!wrapper.getList().isEmpty() && wrapper.getList().stream().allMatch(entity -> entity.getEntityType() == EntityType.EmployeeProject)) {
            return wrapper.getList()
                    .stream()
                    .noneMatch(entity -> ((EmployeeProjectObject) entity).getEmployeeId().equals(id));
        }
        return wrapper.getList()
                .stream()
                .noneMatch(entity -> entity.getId().equals(id));
    }

    public static void truncateFile(String entityFilePath) throws JAXBException {
        File file = new File(entityFilePath);
        JAXBContext context = JAXBContext.newInstance(Wrapper.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(new Wrapper<>(), file);
    }

    /**
     * @param entityFilePath path of the xml file
     * @param <T> T type of entities implemented by Entity
     * @return Wrapper with list of entities
     */
    public static <T extends Entity> Wrapper<T> readFile(String entityFilePath) {
        File file = new File(entityFilePath);
        logger.debug("readFile[1]: file path {}", file.getAbsolutePath());
        try {
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Wrapper<T> wrapper = (Wrapper<T>) unmarshaller.unmarshal(file);
            logger.debug("readRecord[2]: read record {}", wrapper.toString());

            return wrapper;
        }
        catch (JAXBException exception) {
            logger.error("readRecord[3]: {}", exception.getMessage());
            return new Wrapper<>();
        }
    }

    /**
     * @param filePath path of the xml file
     * @param object object implemented by Entity you want to save
     * @param <T> T type of entity implemented by Entity
     * @throws JAXBException throws if something goes wrong when saving an entity to xml
     */
    public static <T extends Entity> void createRecord(String filePath, T object) throws JAXBException {
        Wrapper<T> wrapper = readFile(filePath);
        List<T> list = wrapper.getList();
        logger.debug("createRecord[1]: {}", list);
        String errorMessage = "record with id = %s already exists";

        if (list.stream().anyMatch(el -> el.getId().equals(object.getId()) && el.getEntityType() != EntityType.EmployeeProject))
            throw new JAXBException(String.format(errorMessage, object.getId()));

        wrapper.addNode(object);

        marshaller.marshal(wrapper, new File(filePath));
    }


    /**
     * @param filePath path of the xml file
     * @param object object implemented by Entity you want to save or update
     * @param <T> T type of entity implemented by Entity
     * @throws JAXBException throws if something goes wrong when saving an entity to xml
     */
    public static <T extends Entity> void createOrUpdateRecord(String filePath, T object) throws JAXBException {
        Wrapper<T> wrapper = new Wrapper<>();
        logger.debug("createOrUpdateRecord[0]: {}", wrapper.getList());
        wrapper = readFile(filePath);
        List<T> list = wrapper.getList();

        AtomicBoolean isFound = new AtomicBoolean(false);

        list = list.stream().map(entity -> {
            if (entity.getId().equals(object.getId())) {
                isFound.set(true);
                return object;
            }
            return entity;
        }).toList();

        wrapper.setList(list);

        if (!isFound.get())
            wrapper.addNode(object);

        StringWriter logStringWriter = new StringWriter();

        marshaller.marshal(wrapper, new File(filePath));
        marshaller.marshal(wrapper, logStringWriter);
        logger.debug("createOrUpdateRecord[1]: content {}", logStringWriter);
    }

    /**
     * @param filePath path of the xml file
     * @param wrapper Wrapper with entities implemented by Entity. The specified file is overwritten by this wrapper
     * @param <T> T type of entity implemented by Entity
     * @throws JAXBException throws if something goes wrong when saving an entity to xml
     */
    public static <T> void setContainer(String filePath, Wrapper<T> wrapper) throws JAXBException {
        marshaller.marshal(wrapper, new File(filePath));
    }
}

