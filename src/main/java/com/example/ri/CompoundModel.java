package com.example.ri;

public class CompoundModel {
    private int compoundId;
    private Integer managerId;
    private String location;
    private int numberOfVillas;
    private double price;
    private double totalCost;
    private double profits;

    public CompoundModel(int compoundId, Integer managerId, String location,
                         int numberOfVillas, double price, double totalCost, double profits) {
        this.compoundId = compoundId;
        this.managerId = managerId;
        this.location = location;
        this.numberOfVillas = numberOfVillas;
        this.price = price;
        this.totalCost = totalCost;
        this.profits = profits;
    }

    public int getCompoundId() { return compoundId; }
    public Integer getManagerId() { return managerId; }
    public String getLocation() { return location; }
    public int getNumberOfVillas() { return numberOfVillas; }
    public double getPrice() { return price; }
    public double getTotalCost() { return totalCost; }
    public double getProfits() { return profits; }
}
