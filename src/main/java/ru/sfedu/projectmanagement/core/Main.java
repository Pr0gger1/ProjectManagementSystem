package ru.sfedu.projectmanagement.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        logger.debug("Launching the application...");
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