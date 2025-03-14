package com.monique.jes.ppu.registers;

import com.monique.jes.utils.bitflag.Flag;
import com.monique.jes.utils.bitflag.InnerFlag;

public enum ControlFlag implements Flag {
    GENERATE_NMI(InnerFlag.F1),
    MASTER_SLAVE_SELECT(InnerFlag.F2),
    SPRITE_SIZE(InnerFlag.F3),
    BACKGROUND_PATTERN_ADDR(InnerFlag.F4),
    SPRITE_PATTERN_ADDR(InnerFlag.F5),
    VRAM_ADD_INCREMENT(InnerFlag.F6),
    NAMETABLE2(InnerFlag.F7),
    NAMETABLE1(InnerFlag.F8);

    private final InnerFlag FLAG;

    private ControlFlag(InnerFlag flag) {
        FLAG = flag;
    }

    @Override
    public InnerFlag getFlag() {
        return FLAG;
    }
}
