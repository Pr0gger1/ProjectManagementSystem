package ru.sfedu.projectmanager.model;

import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

public class Employee {
    private final String firstName;
    private final String lastName;
    private final String patronymic;
    private final String fullName;
    private final Calendar birthday;
    private String email;
    private String phoneNumber;
    private UUID id;
    private String position;


    public Employee(
            String firstName,
            String lastName,
            String patronymic,
            Calendar birthday,
            String position
    ) {
        this.id = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.position = position;
        this.birthday = birthday;
        if (patronymic != null)
            this.fullName = String.format(
                "%s %s %s", this.firstName, this.lastName,
                    this.patronymic
            ).trim();
        else this.fullName = String.format(
            "%s %s", this.firstName, this.lastName
        ).trim();
    }

    public Employee(
            UUID id,
            String firstName,
            String lastName,
            String patronymic,
            Calendar birthday,
            String position
    ) {
        this(firstName, lastName, patronymic, birthday, position);
        this.id = id;
    }

    public Employee(
            String firstName,
            String lastName,
            String patronymic,
            Calendar birthday,
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

        if (patronymic != null)
            this.fullName = String.format(
                    "%s %s %s", this.firstName, this.lastName,
                    this.patronymic
            ).trim();
        else this.fullName = String.format(
                "%s %s", this.firstName, this.lastName
        );
    }

    public Employee(String firstName, String lastName, Calendar birthday, String position) {
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

    public String getLastName() {
        return lastName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public Calendar getBirthday() {
        return birthday;
    }

    public String getFullName() {
        return fullName;
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
