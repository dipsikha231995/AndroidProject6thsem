package com.example.applicationformcv;

public class DeedCategoryModel {

    //declare member variables
    private int code;
    private String section;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    @Override
    public String toString() {
        return "DeedCategoryModel{" +
                "code=" + code +
                ", section='" + section + '\'' +
                '}';
    }
}
