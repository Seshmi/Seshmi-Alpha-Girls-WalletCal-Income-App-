package com.example.daytoday.Model;

import android.widget.TextView;

public class Debt {


    private String name;
    private String duedate;
    private Float amount;
    private String discription;

public Debt(){

}
    public Debt(String name, String duedate, Float amount, String discription) {
        this.name = name;
        this.duedate = duedate;
        this.amount = amount;
        this.discription = discription;
    }

    public String getDuedate() {
        return duedate;
    }

    public void setDuedate(String duedate) {
        this.duedate = duedate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String description) {
        this.discription = description;
    }



}
