package com.github.dstadelman.fiji.models.tradestrats;

import com.github.dstadelman.fiji.models.IDescription;

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
