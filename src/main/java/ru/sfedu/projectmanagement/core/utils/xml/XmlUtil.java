package ru.sfedu.projectmanagement.core.utils.xml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.projectmanagement.core.model.*;
import ru.sfedu.projectmanagement.core.model.Entity;
import ru.sfedu.projectmanagement.core.model.enums.EntityType;

import java.io.File;
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
     * @param filePath
     * @param id
     * @param <T>
     * @return
     */
    public static <T extends Entity> boolean isRecordExists(String filePath, UUID id) {
        Wrapper<T> wrapper = XmlUtil.read(filePath);
        if (wrapper.getList().stream().allMatch(entity -> entity.getEntityType() == EntityType.EmployeeProject)) {
            return wrapper.getList()
                    .stream()
                    .anyMatch(entity -> ((EmployeeProjectObject) entity).getEmployeeId().equals(id));
        }
        return wrapper.getList()
                .stream()
                .anyMatch(entity -> entity.getId().equals(id));
    }

    public static void truncateFile(String entityFilePath) throws JAXBException {
        File file = new File(entityFilePath);
        JAXBContext context = JAXBContext.newInstance(Wrapper.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(new Wrapper<>(), file);
    }

    public static <T extends Entity> Wrapper<T> read(String entityFilePath) {
        File file = new File(entityFilePath);

        try {
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Wrapper<T> wrapper = (Wrapper<T>) unmarshaller.unmarshal(file);
            logger.debug("readRecord[1]: read record {}", wrapper.toString());

            return wrapper;
        }
        catch (JAXBException exception) {
            return new Wrapper<>();
        }
    }

    public static <T extends Entity> void createRecord(String filePath, T object) throws JAXBException {
        Wrapper<T> wrapper = read(filePath);
        List<T> list = wrapper.getList();
        logger.debug("createRecord[0]: {}", list);

        for (T entity : list) {
            if (entity.getId().equals(object.getId()) && entity.getEntityType() != EntityType.EmployeeProject) {
                String errorMessage = String.format("record with id = %s already exists", entity.getId());
                throw new JAXBException(errorMessage);
            }
        }

        wrapper.addNode(object);

        marshaller.marshal(wrapper, new File(filePath));
        marshaller.marshal(wrapper, System.out);
    }

    public static <T extends Entity> void createOrUpdateRecord(String filePath, T object) throws JAXBException {
        Wrapper<T> wrapper = new Wrapper<>();
        logger.debug("createOrUpdateRecord[0]: {}", wrapper.getList());
        wrapper = read(filePath);
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

        marshaller.marshal(wrapper, new File(filePath));
        marshaller.marshal(wrapper, System.out);
    }
}

