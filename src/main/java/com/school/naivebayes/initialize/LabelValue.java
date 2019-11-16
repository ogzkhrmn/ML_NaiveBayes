package com.school.naivebayes.initialize;

public class LabelValue {

    private MappedRecord label;
    private String val;

    public LabelValue(MappedRecord label, String val) {
        this.label = label;
        this.val = val;
    }

    public MappedRecord getLabel() {
        return label;
    }

    public String getVal() {
        return val;
    }
}
