package com.cbo.cbomobilereporting.ui_new.dcr_activities.Enum;

public enum CallType {
    NONE(""),
    DOCTOR("D"),
    CHEMIST("C"),
    STOKIST("S"),
    DOCTOR_REMAINDER("RcDr"),
    CHEMIST_REMINDER("Cr"),
    DAIRY("R"),
    POUlTRY("P"),
    EXPENSE("E");

    private String value ="";
    CallType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
