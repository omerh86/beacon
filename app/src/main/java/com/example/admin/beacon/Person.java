package com.example.admin.beacon;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Admin on 1/28/2016.
 */

public class Person implements Serializable {
    private String name;
    private String phoneNumber;
    private Bitmap icon;
    private String massege;
    private String id;


    public Person() {
        this("", "", "Massage", "0");
    }

    public Person(String name, String phonenum, String massege, String id) {
        setName(name);
        setPhoneNumber(phonenum);
        setMassege(massege);
        setId(id);
        setIcon(null);

    }

    @Override
    public String toString() {
        return getName() + " " + getPhoneNumber();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getMassege() {
        return massege;
    }

    public void setMassege(String massege) {
        this.massege = massege;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
