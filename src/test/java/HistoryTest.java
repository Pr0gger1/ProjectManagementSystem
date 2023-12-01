import org.junit.jupiter.api.Test;
import ru.sfedu.projectmanager.api.MongoHistoryProvider;
import ru.sfedu.projectmanager.model.HistoryRecord;
import ru.sfedu.projectmanager.model.Project;
import ru.sfedu.projectmanager.model.enums.ActionStatus;
import ru.sfedu.projectmanager.model.enums.ChangeType;

import java.time.LocalDateTime;
import java.time.Month;


public class HistoryTest {

    @Test
    public void initNewBean() {
        Project project = new Project(
                "tower defense game", "the coolest tower defense game",
                "tower_defense"
        );

        MongoHistoryProvider.save(
            new HistoryRecord<>(
                project,
                "initNewBean",
                ActionStatus.SUCCESS,
                ChangeType.CREATE
        ));

        project.setDeadline(LocalDateTime.of(2023, Month.DECEMBER, 25, 0, 0));
        MongoHistoryProvider.save(
            new HistoryRecord<>(
                project,
                "initNewBean",
                ActionStatus.SUCCESS,
                ChangeType.CREATE
            )
        );
    }
}
