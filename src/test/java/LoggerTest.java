import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class LoggerTest {
    private final Logger logger = LogManager.getLogger(LoggerTest.class);

    @Test
    public void testInfoLog() {
        String message = "This is an info log message";
        logger.info(message);
        assertTrue(logger.isInfoEnabled());
    }

    @Test
    public void testDebugLog() {
        String message = "This is a debug log message";
        logger.debug(message);
        assertTrue(logger.isDebugEnabled());
    }

    @Test
    public void testErrorLog() {
        String message = "This is an error log message";
        logger.error(message);
        assertTrue(logger.isErrorEnabled());
    }
}