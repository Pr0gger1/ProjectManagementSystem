package ru.sfedu.projectmanager.model;

import java.util.Calendar;
import java.util.UUID;

public class Employee {
    private final String firstName;
    private final String lastName;
    private final String patronymic;
    private final String fullName;
    private final Calendar birthday;
    private String email;
    private String phoneNumber;
    private final UUID id = UUID.randomUUID();
    private String position;


    public Employee(String firstName, String lastName, String patronymic, Calendar birthday, String position) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.position = position;
        this.birthday = birthday;
        this.fullName = String.format(
            "%s %s %s", this.firstName, this.lastName,
                this.patronymic
        ).trim();
    }

    public Employee(String firstName, String lastName, Calendar birthday, String position) {
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
}
