package com.monique.jes.cpu;

import com.monique.jes.utils.bitflag.Flag;
import com.monique.jes.utils.bitflag.InnerFlag;

public enum CPUFlag implements Flag {
    N(InnerFlag.F1),
    V(InnerFlag.F2),
    B(InnerFlag.F3),
    B2(InnerFlag.F4),
    D(InnerFlag.F5),
    I(InnerFlag.F6),
    Z(InnerFlag.F7), 
    C(InnerFlag.F8);

    private final InnerFlag FLAG;

    private CPUFlag(InnerFlag flag) {
        FLAG = flag;
    }

    @Override
    public InnerFlag getFlag() {
        return FLAG;
    }
}
