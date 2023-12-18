package ru.sfedu.projectmanagement.core.utils.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.projectmanagement.core.model.Employee;
import ru.sfedu.projectmanagement.core.model.Entity;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class CsvUtil {
    private static final Logger logger = LogManager.getLogger(CsvUtil.class);

    public static <T extends Entity> List<T> readFile(String filePath, Class<T> tClass) {
        try (FileReader reader = new FileReader(filePath)) {
            CSVReader csvReader = new CSVReaderBuilder(reader).build();
            ColumnPositionMappingStrategy<T> mappingStrategy = new ColumnPositionMappingStrategy<>();
            mappingStrategy.setType(tClass);
            String[] mapping = getObjectFields(Employee.class);
            mappingStrategy.setColumnMapping(mapping);

            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(csvReader)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withMappingStrategy(mappingStrategy)
                    .withType(tClass)
                    .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                    .build();

            logger.debug("readFile[1]: read data {}", csvToBean.parse());
            return csvToBean.parse();
        } catch (IOException exception) {
            logger.error("readFile[2]: {}", exception.getMessage());
        }
        return null;
    }

    public static <T extends Entity> boolean isRecordExists(String filePath, UUID id) {
        return true;
//        List<T> data = CsvUtil.readFile(filePath);
//        return Optional.ofNullable(data)
//                .map(d-> d.stream().anyMatch(entity -> entity.getId().equals(id)))
//                .orElse(false);
    }

    public static <T extends Entity> void createRecord(String filePath, T object, Class<T> classT) throws Exception {
        CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath, true));
        ColumnPositionMappingStrategy<T> mappingStrategy = new ColumnPositionMappingStrategy<>();
        mappingStrategy.setType(classT);
        mappingStrategy.setColumnMapping(getObjectFields(classT));

        List<T> data = readFile(filePath, classT);

        for (T entity : data) {
            if (entity.getId().equals(object.getId()))
                throw new Exception(String.format("object with id %s already exists", object.getId()));
        }


        StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(csvWriter)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                .withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
                .withMappingStrategy(mappingStrategy)
                .build();

        beanToCsv.write(object);
        csvWriter.close();
    }

    public static <T extends Entity> void createRecords(String filePath, List<T> objects, Class<T> classT) throws Exception {
        CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath, false));
        ColumnPositionMappingStrategy<T> mappingStrategy = new ColumnPositionMappingStrategy<>();
        mappingStrategy.setType(classT);
        mappingStrategy.setColumnMapping(getObjectFields(classT));

        StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(csvWriter)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
                .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
                .withMappingStrategy(mappingStrategy)
                .build();
        beanToCsv.write(objects);
        csvWriter.close();
    }


    public static <T> String[] getObjectFields(Class<T> object) {
        List<Field> childFields = Arrays.stream(object.getDeclaredFields()).toList();
        List<Field> parentFields = Arrays.stream(object.getSuperclass().getDeclaredFields()).toList();

        List<Field> allFields = new ArrayList<>(){{ addAll(parentFields); addAll(childFields); }};
        allFields = allFields.stream().filter(field -> !field.getName().equals("entityType")).toList();
        String[] columns = new String[allFields.size()];

        for (int i = 0; i < columns.length; i++) {
            columns[i] = allFields.get(i).getName();
        }

        return columns;
    }
}
