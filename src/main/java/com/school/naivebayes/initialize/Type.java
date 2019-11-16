package com.school.naivebayes.initialize;

public enum Type {

    ONE_TWO(2),
    ONE_FIVE(5),
    ONE_TEN(10);

    int rate;

    Type(int rate) {
        this.rate = rate;
    }

    public int getRate() {
        return rate;
    }
}
