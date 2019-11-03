package com.example.model;

public class BabyCharacteristic {

    private int gender;
    private int skin;

    public BabyCharacteristic(int gender, int skin) {
        this.gender = gender;
        this.skin = skin;
    }

    public BabyCharacteristic() {
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getSkin() {
        return skin;
    }

    public void setSkin(int skin) {
        this.skin = skin;
    }
}
