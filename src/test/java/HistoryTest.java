import org.junit.jupiter.api.Test;
import ru.sfedu.projectmanagement.core.api.BaseProviderTest;
import ru.sfedu.projectmanagement.core.api.MongoHistoryProvider;
import ru.sfedu.projectmanagement.core.utils.types.HistoryRecord;
import ru.sfedu.projectmanagement.core.model.enums.ActionStatus;
import ru.sfedu.projectmanagement.core.model.enums.ChangeType;

import java.time.LocalDateTime;
import java.time.Month;


public class HistoryTest extends BaseProviderTest {

    @Test
    public void initNewBean() {
        MongoHistoryProvider.save(
            new HistoryRecord<>(
                project1,
                "initNewBean",
                ActionStatus.SUCCESS,
                ChangeType.CREATE
        ));

        project1.setDeadline(LocalDateTime.of(2023, Month.DECEMBER, 25, 0, 0));
        MongoHistoryProvider.save(
            new HistoryRecord<>(
                project1,
                "initNewBean",
                ActionStatus.SUCCESS,
                ChangeType.CREATE
            )
        );
    }
}
