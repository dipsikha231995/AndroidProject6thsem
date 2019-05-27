package com.example.applicationformcv;

public class ApplicantTypeModel {
    private String type;
    private int exempted;

    public ApplicantTypeModel(String type, int exempted) {
        this.type = type;
        this.exempted = exempted;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getExempted() {
        return exempted;
    }

    public void setExempted(int exempted) {
        this.exempted = exempted;
    }

    @Override
    public String toString() {
        return "ApplicantTypeModel{" + "type=" + type + ", exempted=" + exempted + '}';
    }
}
