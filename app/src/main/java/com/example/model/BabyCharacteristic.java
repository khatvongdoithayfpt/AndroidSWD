package com.example.model;

public class BabyCharacteristic {

    private int sex;
    private int skin;

    public BabyCharacteristic(int sex, int skin) {
        this.sex = sex;
        this.skin = skin;
    }

    public BabyCharacteristic() {
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getSkin() {
        return skin;
    }

    public void setSkin(int skin) {
        this.skin = skin;
    }
}
