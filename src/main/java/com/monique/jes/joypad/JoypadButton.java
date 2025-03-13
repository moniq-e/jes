package com.monique.jes.joypad;

import com.monique.jes.utils.bitflag.Flag;
import com.monique.jes.utils.bitflag.InnerFlag;

public enum JoypadButton implements Flag {
    RIGHT(InnerFlag.F1),
    LEFT(InnerFlag.F2),
    DOWN(InnerFlag.F3),
    UP(InnerFlag.F4),
    START(InnerFlag.F5),
    SELECT(InnerFlag.F6),
    B(InnerFlag.F7),
    A(InnerFlag.F8);

    private final InnerFlag FLAG;

    private JoypadButton(InnerFlag flag) {
        FLAG = flag;
    }

    @Override
    public InnerFlag getFlag() {
        return FLAG;
    }
}
