package com.monique.jes.cpu;

import com.monique.jes.utils.bitflag.Flag;
import com.monique.jes.utils.bitflag.InnerFlag;

public enum CPUFlag implements Flag {
    NEGATIVE(InnerFlag.F1),
    OVERFLOW(InnerFlag.F2),
    BREAK2(InnerFlag.F3),
    BREAK(InnerFlag.F4),
    DECIMAL(InnerFlag.F5),
    INTERRUPT(InnerFlag.F6),
    ZERO(InnerFlag.F7), 
    CARRY(InnerFlag.F8);

    private final InnerFlag FLAG;

    private CPUFlag(InnerFlag flag) {
        FLAG = flag;
    }

    @Override
    public InnerFlag getFlag() {
        return FLAG;
    }
}
