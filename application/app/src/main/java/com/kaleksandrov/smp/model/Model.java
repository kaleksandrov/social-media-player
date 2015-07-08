package com.kaleksandrov.smp.model;

/**
 * Created by kaleksandrov on 4/25/15.
 */
public class Model {
    private int mId;
    private String mTitle;

    public Model(int id, String title) {
        this.mId = id;
        this.mTitle = title;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mTitle;
    }
}
