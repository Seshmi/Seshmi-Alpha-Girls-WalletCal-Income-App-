package com.example.daytoday.Model;

public class List {
    String type;
    float amount;
    String note;
    String date;
    String id;

    public List(){}

    public List(String type, float amount, String note, String date, String id) {
        this.type = type;
        this.amount = amount;
        this.note = note;
        this.date = date;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
