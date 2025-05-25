package com.example.ri;

public class OwnerPayment {
    private int ownerId;
    private String ownerName;
    private int villaId;
    private String paymentMethod;

    public OwnerPayment(int ownerId, String ownerName, int villaId, String paymentMethod) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.villaId = villaId;
        this.paymentMethod = paymentMethod;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public int getVillaId() {
        return villaId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}
