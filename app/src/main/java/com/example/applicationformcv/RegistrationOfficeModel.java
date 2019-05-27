package com.example.applicationformcv;

public class RegistrationOfficeModel {
    private String officeName;
    private int sroCode;

    public RegistrationOfficeModel(String officeName, int sroCode) {
        this.officeName = officeName;
        this.sroCode = sroCode;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public int getSroCode() {
        return sroCode;
    }

    public void setSroCode(int sroCode) {
        this.sroCode = sroCode;
    }

    @Override
    public String toString() {
        return "RegistrationOfficeModel{" + "officeName=" + officeName + ", sroCode=" + sroCode + '}';
    }
}
