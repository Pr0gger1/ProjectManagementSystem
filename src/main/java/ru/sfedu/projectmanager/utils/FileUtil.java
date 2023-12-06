package ru.sfedu.projectmanager.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class FileUtil {
    private static final Logger logger = LogManager.getLogger(FileUtil.class);

    public static void createFolderIfNotExists(String path) throws IOException {
        File folder = new File(path);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            logger.debug("createFolderIfNotExists[1]: directory {} has been created: {}", folder.getAbsolutePath(), created);
        }
        else logger.debug("createFolderIfNotExists[2]: directory {} already exists", folder.getAbsolutePath());
    }

    public static void createFileIfNotExists(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            boolean created = file.createNewFile();
            logger.debug("createFileIfNotExists[1]: file {} has been created: {}", file.getAbsolutePath(), created);
        }
        else logger.debug("createFileIfNotExists[2]: file {} already exists", file.getAbsolutePath());
    }
}
