package com.monique.jes.ppu.registers;

import com.monique.jes.utils.bitflag.Flag;
import com.monique.jes.utils.bitflag.InnerFlag;

public enum MaskFlag implements Flag {
    EMPHASISE_BLUE(InnerFlag.F1),
    EMPHASISE_GREEN(InnerFlag.F2),
    EMPHASISE_RED(InnerFlag.F3),
    SHOW_SPRITES(InnerFlag.F4),
    SHOW_BACKGROUND(InnerFlag.F5),
    LEFTMOST_8PXL_SPRITE(InnerFlag.F6),
    LEFTMOST_8PXL_BACKGROUND(InnerFlag.F7),
    GREYSCALE(InnerFlag.F8);

    private final InnerFlag FLAG;

    private MaskFlag(InnerFlag flag) {
        FLAG = flag;
    }

    @Override
    public InnerFlag getFlag() {
        return FLAG;
    }
}
