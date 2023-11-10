package ru.sfedu.projectmanager.model;

import java.util.Date;

public class Employee {
    private final String firstName;
    private final String lastName;
    private final String patronymic;
    private final Date birthday;
    private String email;
    private String phoneNumber;
    private int id;
    private String position;


    Employee(String firstName, String lastName, String patronymic, Date birthday, String position) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.position = position;
        this.birthday = birthday;
    }

    Employee(String firstName, String lastName, Date birthday, String position) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = "";
        this.position = position;
        this.birthday = birthday;
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

    public Date getBirthday() {
        return birthday;
    }

    public int getId() {
        return id;
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
