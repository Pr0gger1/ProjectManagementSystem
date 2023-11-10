package ru.sfedu.projectmanager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    static Logger logger = LogManager.getLogger(Main.class);

    static {
        logger.debug("Main[0]: starting application");
    }

    public static void main(String[] args) {
        logger.info("Launching the application...");
        logger.info(
                "Operating System: " + System.getProperty("os.name") + " "
                        + System.getProperty("os.version")
        );
        logger.info("JRE: " + System.getProperty("java.version"));
        logger.info("Java Launched From: " + System.getProperty("java.home"));
        logger.info("Class Path: " + System.getProperty("java.class.path"));
        logger.info("Library Path: " + System.getProperty("java.library.path"));
        logger.info("User Home Directory: " + System.getProperty("user.home"));
        logger.info("User Working Directory: " + System.getProperty("user.dir"));
        logger.info("Test INFO logging.");
        logger.error("error");
    }
}