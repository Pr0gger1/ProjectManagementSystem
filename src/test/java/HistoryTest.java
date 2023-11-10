import org.junit.jupiter.api.Test;
import ru.sfedu.projectmanager.Constants;
import ru.sfedu.projectmanager.api.MongoHistoryProvider;
import ru.sfedu.projectmanager.model.HistoryRecord;
import ru.sfedu.projectmanager.model.Project;
import ru.sfedu.projectmanager.model.enums.ActionStatus;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class HistoryTest {

    @Test
    public void initNewBean() {
        Project project = new Project(
                "tower defense game", "the coolest tower defense game",
                "tower_defense"
        );

        HistoryRecord<Project> record1 = new HistoryRecord<>(
                project,
                "initNewBean",
                ActionStatus.SUCCESS
        );

        MongoHistoryProvider.save(Constants.MONGO_TEST_DB, record1);
        project.setDeadline(new GregorianCalendar(2023, Calendar.DECEMBER, 25));


        HistoryRecord<Project> record2 =   new HistoryRecord<>(
                project,
                "initNewBean",
                ActionStatus.SUCCESS
        );

        MongoHistoryProvider.save(Constants.MONGO_TEST_DB, record2);
    }
}
