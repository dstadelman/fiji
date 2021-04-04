package com.github.dstadelman.fiji.entities.tradestrats;

import com.github.dstadelman.fiji.entities.IDescription;

public class BuyAndHold implements IDescription {

    public String root;

    public BuyAndHold(String root) {
        this.root = root;
    }

    @Override
    public String getDescription() {
        return root + ": Buy and Hold";
    }

}
