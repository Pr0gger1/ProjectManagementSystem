package ru.sfedu.projectmanagement.core.utils.xml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.projectmanagement.core.model.BugReport;
import ru.sfedu.projectmanagement.core.model.Project;

import java.io.File;
import java.util.ArrayList;

public class XmlUtil {
    private static final Logger logger = LogManager.getLogger(XmlUtil.class);
    private static JAXBContext context;
    private static Marshaller marshaller;
    private static Unmarshaller unmarshaller;

    static {
        try {
            context = JAXBContext.newInstance(
                    Wrapper.class,
                    Project.class,
                    BugReport.class
            );

            marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            unmarshaller = context.createUnmarshaller();
        } catch (JAXBException e) {
            logger.error("XmlUtil: {}", e.getMessage());
        }
    }

    public static void truncateFile(String entityFilePath) throws JAXBException {
        File file = new File(entityFilePath);
        JAXBContext context = JAXBContext.newInstance(Wrapper.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(new Wrapper<>(), file);
    }

    public static <T> Wrapper<T> read(String entityFilePath) throws JAXBException{
        File file = new File(entityFilePath);

        JAXBContext context = JAXBContext.newInstance(Wrapper.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Wrapper<T> wrapper = (Wrapper<T>) unmarshaller.unmarshal(file);
        logger.debug("readRecord[1]: read record {}", wrapper.toString());

        return wrapper;
    }

    public static <T> void createOrUpdate(String filePath, T object) throws JAXBException {
        Wrapper<T> wrapper = new Wrapper<>();
        logger.debug("createOrUpdateRecord[0]: {}", wrapper.getList());
        wrapper = read(filePath);
        ArrayList<T> list = wrapper.getList();

        boolean isFound = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(object)) {
                list.set(i, object);
                isFound = true;
                break;
            }
        }

        if (!isFound)
            list.add(object);

        marshaller.marshal(wrapper, new File(filePath));
        marshaller.marshal(wrapper, System.out);
    }
}

