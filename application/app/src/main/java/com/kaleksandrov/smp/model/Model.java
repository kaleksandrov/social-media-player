package com.kaleksandrov.smp.model;

import com.kaleksandrov.smp.util.StringUtils;

/**
 * Created by kaleksandrov on 4/25/15.
 */
public class Model implements Comparable<Model> {
    private int mId;
    private String mName;

    public Model(int id, String name) {
        this.mId = id;
        this.mName = name;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    @Override
    public int compareTo(Model another) {
        if (StringUtils.isEmpty(this.mName)) {
            return -1;
        }

        if (StringUtils.isEmpty(another.mName)) {
            return 1;
        }

        return this.mName.compareTo(another.mName);
    }
}
