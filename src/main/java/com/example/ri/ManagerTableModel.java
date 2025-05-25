package com.example.ri;

public class ManagerTableModel {
    private int id, compoundId;
    private String firstName, lastName, phone, email, salary, bankAccount;

    public ManagerTableModel(int id, String fullName, int compoundId, String phone, String email, String salary, String bankAccount) {
        this.id = id;
        this.compoundId = compoundId;
        this.phone = phone;
        this.email = email;
        this.salary = salary;
        this.bankAccount = bankAccount;

        String[] parts = fullName.split(" ", 2);
        if (parts.length == 2) {
            this.firstName = parts[0];
            this.lastName = parts[1];
        } else {
            this.firstName = fullName;
            this.lastName = "";
        }
    }

    public int getId() { return id; }
    public int getCompoundId() { return compoundId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getSalary() { return salary; }
    public String getBankAccount() { return bankAccount; }

    public String getName() {
        return firstName + " " + lastName;
    }

    public void setName(String name) {
        String[] parts = name.split(" ", 2);
        if (parts.length == 2) {
            this.firstName = parts[0];
            this.lastName = parts[1];
        } else {
            this.firstName = name;
            this.lastName = "";
        }
    }

    public void setCompoundId(int compoundId) { this.compoundId = compoundId; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setSalary(String salary) { this.salary = salary; }
    public void setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }
}
