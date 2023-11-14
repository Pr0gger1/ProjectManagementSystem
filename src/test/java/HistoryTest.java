import org.junit.jupiter.api.Test;
import ru.sfedu.projectmanager.Constants;
import ru.sfedu.projectmanager.model.HistoryRecord;
import ru.sfedu.projectmanager.model.Project;
import ru.sfedu.projectmanager.model.enums.ActionStatus;
import ru.sfedu.projectmanager.model.enums.ChangeType;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class HistoryTest {

    @Test
    public void initNewBean() {
        Project project = new Project(
                "tower defense game", "the coolest tower defense game",
                "tower_defense"
        );

        project.addHistoryRecord(
            Constants.MONGO_TEST_DB,
            new HistoryRecord<>(
                project,
                "initNewBean",
                ActionStatus.SUCCESS,
                ChangeType.CREATE
        ));

        project.setDeadline(new GregorianCalendar(2023, Calendar.DECEMBER, 25));
        project.addHistoryRecord(
            Constants.MONGO_TEST_DB,
            new HistoryRecord<>(
                project,
                "initNewBean",
                ActionStatus.SUCCESS,
                ChangeType.UPDATE
            )
        );
    }
}
