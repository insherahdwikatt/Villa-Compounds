package com.example.ri;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ownerTableModel {

    private final IntegerProperty ownerId;
    private final StringProperty ownerName;
    private final StringProperty phoneNumber;
    private final StringProperty email;
    private final StringProperty birthdate;
    private final StringProperty nationalId;
    private final IntegerProperty propertyCount;

    public ownerTableModel(int ownerId, String ownerName, String phoneNumber, String email,
                           String birthdate, String nationalId, int propertyCount) {
        this.ownerId = new SimpleIntegerProperty(ownerId);
        this.ownerName = new SimpleStringProperty(ownerName);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.email = new SimpleStringProperty(email);
        this.birthdate = new SimpleStringProperty(birthdate);
        this.nationalId = new SimpleStringProperty(nationalId);
        this.propertyCount = new SimpleIntegerProperty(propertyCount);
    }

    // ownerId
    public int getOwnerId() {
        return ownerId.get();
    }
    public void setOwnerId(int value) {
        ownerId.set(value);
    }
    public IntegerProperty ownerIdProperty() {
        return ownerId;
    }

    // ownerName
    public String getOwnerName() {
        return ownerName.get();
    }
    public void setOwnerName(String value) {
        ownerName.set(value);
    }
    public StringProperty ownerNameProperty() {
        return ownerName;
    }

    // phoneNumber
    public String getPhoneNumber() {
        return phoneNumber.get();
    }
    public void setPhoneNumber(String value) {
        phoneNumber.set(value);
    }
    public StringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    // email
    public String getEmail() {
        return email.get();
    }
    public void setEmail(String value) {
        email.set(value);
    }
    public StringProperty emailProperty() {
        return email;
    }

    // birthdate
    public String getBirthdate() {
        return birthdate.get();
    }
    public void setBirthdate(String value) {
        birthdate.set(value);
    }
    public StringProperty birthdateProperty() {
        return birthdate;
    }

    // nationalId
    public String getNationalId() {
        return nationalId.get();
    }
    public void setNationalId(String value) {
        nationalId.set(value);
    }
    public StringProperty nationalIdProperty() {
        return nationalId;
    }

    // propertyCount
    public int getPropertyCount() {
        return propertyCount.get();
    }
    public void setPropertyCount(int value) {
        propertyCount.set(value);
    }
    public IntegerProperty propertyCountProperty() {
        return propertyCount;
    }
}
