package com.monique.jes.ppu.registers;

import com.monique.jes.utils.bitflag.Flag;
import com.monique.jes.utils.bitflag.InnerFlag;

public enum StatusFlag implements Flag {
    VBLANK_STARTED(InnerFlag.F1),
    SPRITE_ZERO_HIT(InnerFlag.F2),
    SPRITE_OVERFLOW(InnerFlag.F3),
    NOTUSED5(InnerFlag.F4),
    NOTUSED4(InnerFlag.F5),
    NOTUSED3(InnerFlag.F6),
    NOTUSED2(InnerFlag.F7),
    NOTUSED(InnerFlag.F8);

    private final InnerFlag FLAG;

    private StatusFlag(InnerFlag flag) {
        FLAG = flag;
    }

    @Override
    public InnerFlag getFlag() {
        return FLAG;
    }
}
