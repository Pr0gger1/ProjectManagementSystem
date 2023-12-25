package ru.sfedu.projectmanagement.core.utils.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.projectmanagement.core.model.*;
import ru.sfedu.projectmanagement.core.model.enums.EntityType;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.*;

public class CsvUtil {
    private static final Logger logger = LogManager.getLogger(CsvUtil.class);

    public static <T extends Entity> List<T> readFile(String filePath, Class<T> tClass) {
        try (Reader reader = new FileReader(filePath)) {
            CSVReader csvReader = new CSVReaderBuilder(reader).build();

            ColumnPositionMappingStrategy<T> mappingStrategy = new ColumnPositionMappingStrategy<>();
            mappingStrategy.setType(tClass);

            String[] mapping = getObjectFields(tClass);
            mappingStrategy.setColumnMapping(mapping);

            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(csvReader)
                    .withType(tClass)
                    .withMappingStrategy(mappingStrategy)
                    .build();

            return csvToBean.parse();

        } catch (IOException e) {
            logger.error("getAllRecords[3]: error: {}", e.getMessage());
        }
        return null;
    }

    public static <T extends Entity> void truncateFile(String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath, false)) {
            CSVWriter csvWriter = new CSVWriter(fileWriter);
            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(csvWriter)
                    .build();

            beanToCsv.write(new ArrayList<>());
        }
        catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error("truncateFile[1]: {}", e.getMessage());
        }
    }

    public static <T extends Entity> boolean isRecordNotExists(String filePath, UUID id, Class<T> tClass) {
        List<T> data = readFile(filePath, tClass);
        return !Optional.ofNullable(data)
                .map(d-> d.stream().anyMatch(entity -> entity.getId().equals(id)))
                .orElse(false);
    }

    public static <T extends Entity> boolean isRecordExists(String filePath, UUID id, Class<T> tClass) {
        return !isRecordNotExists(filePath, id, tClass);
    }

    public static <T extends Entity> void createRecord(String filePath, T object, Class<T> classT) throws Exception {
        List<T> data = readFile(filePath, classT);
        String errorMessage = "%s with id %s already exists";
        List<EntityType> secondaryEntities = List.of(EntityType.TaskTag, EntityType.DocumentationData);

        boolean alreadyExists = Optional.ofNullable(data)
                .map(d -> d.stream()
                        .anyMatch(entity -> {
                            if (object.getEntityType() == EntityType.EmployeeProject) {
                                return ((EmployeeProjectObject) entity).getEmployeeId().equals(((EmployeeProjectObject) object).getEmployeeId());
                            }
                            else {
                                return !secondaryEntities.contains(object.getEntityType()) && entity.getId().equals(object.getId());
                            }
                        }))
                .orElse(false);

        if (alreadyExists) {
            throw new Exception(String.format(errorMessage, object.getClass().getSimpleName(),
                    (object.getEntityType() == EntityType.EmployeeProject)
                            ? ((EmployeeProjectObject) object).getEmployeeId()
                            : object.getId()));
        }

        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath, true))) {
            ColumnPositionMappingStrategy<T> mappingStrategy = new ColumnPositionMappingStrategy<>();
            mappingStrategy.setType(classT);
            String[] columns = getObjectFields(classT);
            mappingStrategy.setColumnMapping(columns);

            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(csvWriter)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
                    .withMappingStrategy(mappingStrategy)
                    .build();

            beanToCsv.write(object);
        }
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