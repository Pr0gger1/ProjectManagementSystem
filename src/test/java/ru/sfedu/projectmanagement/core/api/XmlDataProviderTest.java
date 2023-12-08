package ru.sfedu.projectmanagement.core.api;

import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.sfedu.projectmanagement.core.model.*;
import ru.sfedu.projectmanagement.core.model.enums.BugStatus;
import ru.sfedu.projectmanagement.core.model.enums.Priority;
import ru.sfedu.projectmanagement.core.model.enums.WorkStatus;
import ru.sfedu.projectmanagement.core.Constants;
import ru.sfedu.projectmanagement.core.utils.ResultCode;
import ru.sfedu.projectmanagement.core.utils.types.Result;
import ru.sfedu.projectmanagement.core.utils.xml.XmlUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


class XmlDataProviderTest {

    private static final DataProvider xmlDataProvider = new XmlDataProvider();
    private static final ArrayList<Employee> team = new ArrayList<>();
    private static final Employee employee = new Employee(
            "Nikolay",
            "Eremeev",
            LocalDate.of(1999, Month.MAY, 6),
            "Senior mobile dev lead"
    );

    private static final Project project = new Project(
            "mobile bank app",
            "mobile app for bank based on kotlin and swift",
            "mobile_bank"
    );

    private static final Task task = new Task(
            "create main page of application",
            "main page of bank application",
            employee.getId(),
            employee.getFullName(),
            project.getId(),
            Priority.MEDIUM
    );

    private static final ArrayList<Task> tasks = new ArrayList<>();
    private static final ArrayList<BugReport> bugReports = new ArrayList<>();


    private static final BugReport bugReport = new BugReport(
            "mobile_bank_report_12-05-2023",
            "this is a bug report description",
            employee.getId(),
            employee.getFullName(),
            project.getId(),
            Priority.HIGH
    );

    private static final Event event = new Event(
            "mobile bank app presentation",
            "show client what we did",
            employee.getId(),
            employee.getFullName(),
            project.getId(),
            LocalDateTime.of(2023, Month.DECEMBER, 22, 15, 0),
            LocalDateTime.of(2023, Month.DECEMBER, 22, 10, 0)
    );

    private final static ArrayList<Event> events = new ArrayList<>(){{add(event);}};

    private static final Documentation documentation = new Documentation(
            "app documentation",
            "app documentation description",
            new HashMap<>() {{
                put("chapter one", "some loooooong text");
                put("chapter two", "another some loooooong text");
            }},
            employee.getId(),
            employee.getFullName(),
            project.getId()
    );

    @BeforeAll
    public static void createTaskList() {
        Task task1 = new Task(
                "Task 1",
                "Description for Task 1",
                UUID.fromString("f39369ef-e5a9-4de6-b27e-a305f63f71e5"),
                employee.getId(),
                employee.getFullName(),
                "mobile_bank",
                LocalDateTime.of(2023, Month.DECEMBER, 24,0, 0),
                "Comment for Task 1",
                Priority.HIGH,
                new ArrayList<>(Arrays.asList("Tag1", "tag2")),
                WorkStatus.COMPLETED,
                LocalDateTime.now().withNano(0),
                LocalDateTime.of(2023, Month.DECEMBER, 20, 15, 12)
        );

        Task task2 = new Task(
                "Task 2",
                "Description for Task 2",
                UUID.fromString("f8480200-6e0c-4ef4-815c-225e6bc0aa66"),
                employee.getId(),
                employee.getFullName(),
                "mobile_bank",
                LocalDateTime.of(2023, Month.NOVEMBER, 15,0, 0),
                "Comment for Task 2",
                Priority.MEDIUM,
                new ArrayList<>(Arrays.asList("Tag1", "tag2")),
                WorkStatus.COMPLETED,
                LocalDateTime.now().withNano(0),
                LocalDateTime.of(2023, Month.NOVEMBER, 18, 10, 54)
        );

        Task task3 = new Task(
                "Task 3",
                "Description for Task 3",
                UUID.fromString("b0a274fe-9005-4491-9b4b-0f0882e4f879"),
                employee.getId(),
                employee.getFullName(),
                "mobile_bank",
                LocalDateTime.of(2023, Month.DECEMBER, 15, 0, 0),
                "Comment for Task 3",
                Priority.LOW,
                new ArrayList<>(Arrays.asList("Tag1", "tag2")),
                WorkStatus.IN_PROGRESS,
                LocalDateTime.now().withNano(0),
                null
        );

        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
    }

    @BeforeAll
    static void createTeamList() {
        LocalDate birthday1 = LocalDate.of(1990, Month.MAY, 15);
        Employee employee1 = new Employee("Иван", "Иванов", "Иванович", birthday1, "Разработчик");
        team.add(employee1);

        LocalDate birthday2 = LocalDate.of(1985, Month.MARCH, 14);
        Employee employee2 = new Employee("Елена", "Петрова", "Александровна", birthday2, "Тестировщик");
        team.add(employee2);

        LocalDate birthday3 = LocalDate.of(1992, Month.AUGUST, 7);
        Employee employee3 = new Employee("Андрей", "Смирнов", null, birthday3, "Дизайнер");
        team.add(employee3);
    }

    @BeforeAll
    static void createBugReportList() {
        BugReport bugReport1 = new BugReport("Bug 1", "Description 1", employee.getId(), employee.getFullName(), project.getId());
        BugReport bugReport2 = new BugReport("Bug 2", "Description 2", employee.getId(), employee.getFullName(), project.getId(), Priority.HIGH);
        BugReport bugReport3 = new BugReport("Bug 3", "Description 3", employee.getId(), employee.getFullName(), project.getId(), Priority.MEDIUM);
        bugReport3.setStatus(BugStatus.CLOSED);

        bugReports.add(bugReport1);
        bugReports.add(bugReport2);
        bugReports.add(bugReport3);
    }

    @BeforeEach
    void resetDb() throws JAXBException {
        XmlUtil.truncateFile(Constants.DATASOURCE_PATH_XML
                .concat(Constants.PROJECTS_FILE_PATH)
                .concat(Constants.FILE_XML_EXTENSION));
    }

    @Test
    void processNewProject() {
        Result<?> actual = xmlDataProvider.processNewProject(project);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Test
    void processNewTask() {
    }

    @Test
    void processNewBugReport() {
        Result<?> actual = xmlDataProvider.processNewBugReport(bugReport);
        assertEquals(ResultCode.SUCCESS, actual.getCode());
    }

    @Test
    void processNewDocumentation() {
    }

    @Test
    void processNewEvent() {
    }

    @Test
    void monitorProjectCharacteristics() {
    }

    @Test
    void calculateProjectReadiness() {
    }

    @Test
    void calculateLaborEfficiency() {
    }

    @Test
    void trackTaskStatus() {
    }

    @Test
    void trackBugReportStatus() {
    }

    @Test
    void bindEmployeeToProject() {
    }

    @Test
    void bindProjectManager() {
    }

    @Test
    void bindTaskExecutor() {
    }

    @Test
    void deleteProject() {
    }

    @Test
    void deleteTask() {
    }

    @Test
    void deleteBugReport() {
    }

    @Test
    void deleteEvent() {
    }

    @Test
    void deleteDocumentation() {
    }

    @Test
    void deleteEmployee() {
    }

    @Test
    void getProjectById() {

    }

    @Test
    void getTasksByProjectId() {
    }

    @Test
    void getTaskById() {
    }

    @Test
    void getTasksByEmployeeId() {
    }

    @Test
    void getBugReportsByProjectId() {
    }

    @Test
    void getBugReportById() {
    }

    @Test
    void getEventsByProjectId() {
    }

    @Test
    void getEventById() {
    }

    @Test
    void getDocumentationById() {
    }

    @Test
    void getDocumentationsByProjectId() {
    }

    @Test
    void getProjectTeam() {
    }

    @Test
    void getEmployeeById() {
    }
}