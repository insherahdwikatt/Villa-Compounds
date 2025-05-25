package com.example.ri;

public class VillaTableModel {
    private final int villaId;
    private final String ownerName;
    private final int ownerId;
    private final String status;
    private final String paymentMethod;
    private final String paymentCompleted;

    public VillaTableModel(int villaId, String ownerName, int ownerId, String status, String paymentMethod, String paymentCompleted) {
        this.villaId = villaId;
        this.ownerName = ownerName;
        this.ownerId = ownerId;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.paymentCompleted = paymentCompleted;
    }

    public int getVillaId() {
        return villaId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentCompleted() {
        return paymentCompleted;
    }
}
