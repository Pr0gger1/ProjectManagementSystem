package ru.sfedu.projectmanagement.core.model;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import ru.sfedu.projectmanagement.core.utils.xml.adapters.XmlLocalDateAdapter;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "employee")
@XmlType(name = "Employee")
public class Employee implements Entity {
    @XmlAttribute(required = true)
    private UUID id;

    @XmlElement(required = true)
    private String firstName;

    @XmlElement(required = true)
    private String lastName;

    @XmlElement()
    private String patronymic;

    @XmlElement(required = true)
    private String fullName;

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(XmlLocalDateAdapter.class)
    private LocalDate birthday;

    @XmlElement(required = true)
    private String email;

    @XmlElement()
    private String phoneNumber;

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
        return String.format("""
        Employee: {
            id: %s,
            fullName: %s,
            firstName: %s,
            lastName: %s,
            patronymic: %s,
            birthday: %s,
            phoneNumber: %s,
            email: %s,
            position: %s
        }
        """, id, fullName,
                firstName, lastName,
                patronymic, birthday.toString(),
                phoneNumber, email, position
            );
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Employee employee = (Employee) object;
        return Objects.equals(firstName, employee.firstName) && Objects.equals(lastName, employee.lastName) && Objects.equals(patronymic, employee.patronymic) && Objects.equals(fullName, employee.fullName) && Objects.equals(birthday, employee.birthday) && Objects.equals(email, employee.email) && Objects.equals(phoneNumber, employee.phoneNumber) && Objects.equals(id, employee.id) && Objects.equals(position, employee.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, patronymic, fullName, birthday, email, phoneNumber, id, position);
    }
}
