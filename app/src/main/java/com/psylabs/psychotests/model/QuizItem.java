package com.psylabs.psychotests.model;

import org.parceler.Parcel;

/**
 * Created by Andrew on 30.03.2017.
 */
@Parcel
public class QuizItem {

    int id;
    String name;
    boolean isPassed;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPassed() {
        return isPassed;
    }

    public void setPassed(boolean passed) {
        isPassed = passed;
    }
}
