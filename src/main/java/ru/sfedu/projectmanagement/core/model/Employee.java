package ru.sfedu.projectmanagement.core.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvIgnore;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import ru.sfedu.projectmanagement.core.model.enums.EntityType;
import ru.sfedu.projectmanagement.core.utils.xml.adapters.XmlLocalDateAdapter;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "employee")
@XmlType(name = "Employee")
public class Employee implements Entity {
    @CsvIgnore
    @XmlTransient
    private final EntityType entityType = EntityType.Employee;

    @CsvBindByName(column = "id", required = true)
    @XmlAttribute(required = true)
    private UUID id;

    @CsvBindByName(column = "first_name", required = true)
    @XmlElement(name = "first_name", required = true)
    private String firstName;

    @CsvBindByName(column = "last_name", required = true)
    @XmlElement(required = true)
    private String lastName;

    @CsvBindByName(column = "patronymic")
    @XmlElement
    private String patronymic;

    @CsvBindByName(column = "full_name", required = true)
    @XmlElement(name = "full_name", required = true)
    private String fullName;

    @CsvBindByName(column = "birthday", required = true)
    @CsvDate("yyyy-MM-dd")
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(XmlLocalDateAdapter.class)
    private LocalDate birthday;

    @CsvBindByName(column = "email", required = true)
    @XmlElement(required = true)
    private String email;

    @CsvBindByName(column = "phone_number")
    @XmlElement(name = "phone_number")
    private String phoneNumber;

    @CsvBindByName(column = "position", required = true)
    @XmlElement(required = true)
    private String position;

    public Employee() {
        id = UUID.randomUUID();
    }

    public Employee(
            String firstName,
            String lastName,
            String patronymic,
            LocalDate birthday,
            String position
    ) {
        this.id = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.position = position;
        this.birthday = birthday;
        updateFullName();
    }

    public Employee(
            UUID id,
            String firstName,
            String lastName,
            String patronymic,
            LocalDate birthday,
            String position
    ) {
        this(firstName, lastName, patronymic, birthday, position);
        this.id = id;
    }

    public Employee(
            String firstName,
            String lastName,
            String patronymic,
            LocalDate birthday,
            String email,
            String phoneNumber,
            UUID id,
            String position
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.birthday = birthday;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.position = position;

        updateFullName();
    }

    public Employee(String firstName, String lastName, LocalDate birthday, String position) {
        this.id = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = null;
        this.position = position;
        this.birthday = birthday;
        this.fullName = String.format("%s %s", this.firstName, this.lastName);
    }

    @Override
    public EntityType getEntityType() {
        return entityType;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPosition() {
        return position;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        updateFullName();
    }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        updateFullName();
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
        updateFullName();
    }

    public LocalDate getBirthday() {
        return birthday;
    }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    private void updateFullName() {
        fullName = firstName + " " + lastName;
        if (patronymic != null)
            fullName += " " + patronymic;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "entityType=" + entityType +
                ", id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", fullName='" + fullName + '\'' +
                ", birthday=" + birthday +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", position='" + position + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Employee employee = (Employee) object;
        return Objects.equals(firstName, employee.firstName) && Objects.equals(lastName, employee.lastName) && Objects.equals(patronymic, employee.patronymic) && Objects.equals(fullName, employee.fullName) && Objects.equals(birthday, employee.birthday) && Objects.equals(email, employee.email) && Objects.equals(phoneNumber, employee.phoneNumber) && Objects.equals(getId(), employee.getId()) && Objects.equals(position, employee.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, patronymic, fullName, birthday, email, phoneNumber, getId(), position);
    }
}
