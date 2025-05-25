package com.example.ri;

import javafx.beans.property.*;

import java.math.BigDecimal;

public class workerTableModel {

    private final IntegerProperty workerId;
    private final StringProperty workerName;
    private final StringProperty field;
    private final ObjectProperty<BigDecimal> salary;
    private final IntegerProperty workHours;
    private final StringProperty endOfLease;
    private final StringProperty phoneNumber;
    private final StringProperty birthdate;

    public workerTableModel(int workerId, String workerName, String field, BigDecimal salary,
                            int workHours, String endOfLease, String phoneNumber, String birthdate) {
        this.workerId = new SimpleIntegerProperty(workerId);
        this.workerName = new SimpleStringProperty(workerName);
        this.field = new SimpleStringProperty(field);
        this.salary = new SimpleObjectProperty<>(salary);
        this.workHours = new SimpleIntegerProperty(workHours);
        this.endOfLease = new SimpleStringProperty(endOfLease);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.birthdate = new SimpleStringProperty(birthdate);
    }

    public IntegerProperty workerIdProperty() {
        return workerId;
    }
    public int getWorkerId() {
        return workerId.get();
    }


    public StringProperty workerNameProperty() {
        return workerName;
    }

    public StringProperty fieldProperty() {
        return field;
    }

    public ObjectProperty<BigDecimal> salaryProperty() {
        return salary;
    }

    public IntegerProperty workHoursProperty() {
        return workHours;
    }

    public StringProperty endOfLeaseProperty() {
        return endOfLease;
    }

    public StringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public StringProperty birthdateProperty() {
        return birthdate;
    }
}
