package com.example.ri;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import java.math.BigDecimal;

public class WorkerPayment {

    private final SimpleIntegerProperty workerId;
    private final SimpleStringProperty workerName;
    private final SimpleStringProperty bankAccount;
    private final SimpleObjectProperty<BigDecimal> salary;
    private final SimpleStringProperty payDate;

    public WorkerPayment(int workerId, String workerName, String bankAccount, BigDecimal salary, String payDate) {
        this.workerId = new SimpleIntegerProperty(workerId);
        this.workerName = new SimpleStringProperty(workerName);
        this.bankAccount = new SimpleStringProperty(bankAccount);
        this.salary = new SimpleObjectProperty<>(salary);
        this.payDate = new SimpleStringProperty(payDate);
    }

    public int getWorkerId() {
        return workerId.get();
    }

    public SimpleIntegerProperty workerIdProperty() {
        return workerId;
    }

    public String getWorkerName() {
        return workerName.get();
    }

    public SimpleStringProperty workerNameProperty() {
        return workerName;
    }

    public String getBankAccount() {
        return bankAccount.get();
    }

    public SimpleStringProperty bankAccountProperty() {
        return bankAccount;
    }

    public BigDecimal getSalary() {
        return salary.get();
    }

    public SimpleObjectProperty<BigDecimal> salaryProperty() {
        return salary;
    }

    public String getPayDate() {
        return payDate.get();
    }

    public SimpleStringProperty payDateProperty() {
        return payDate;
    }
}
