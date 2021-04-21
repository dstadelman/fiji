package com.github.dstadelman.fiji.models.portfoliostrats;

import com.github.dstadelman.fiji.models.IDescription;

public class NLot implements IDescription {

    public int nlots;

    public NLot(int nlots) {
        this.nlots = nlots;
    }

    @Override
    public String getDescription() {
        return String.format("" + nlots + " Lot");
    }

}
