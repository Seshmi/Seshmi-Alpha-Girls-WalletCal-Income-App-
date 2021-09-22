package com.example.daytoday.Model;

public class DataIncome {

    private float amount;
    private String type;
    private String note;

    public DataIncome(){

    }

    public DataIncome(float amount, String type, String note) {

        this.amount = amount;
        this.type = type;
        this.note = note;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
