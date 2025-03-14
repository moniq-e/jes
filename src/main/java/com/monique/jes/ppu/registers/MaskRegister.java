package com.monique.jes.ppu.registers;

import java.util.ArrayList;

import com.monique.jes.utils.bitflag.BitFlag;

public class MaskRegister {
    private BitFlag bits;

    public MaskRegister() {
        bits = new BitFlag();
    }

    public boolean isGrayscale() {
        return bits.getBitFlag(MaskFlag.GREYSCALE);
    }

    public boolean leftmost8pxlBackground() {
        return bits.getBitFlag(MaskFlag.LEFTMOST_8PXL_BACKGROUND);
    }

    public boolean leftmost8pxlSprite() {
        return bits.getBitFlag(MaskFlag.LEFTMOST_8PXL_SPRITE);
    }

    public boolean showBackground() {
        return bits.getBitFlag(MaskFlag.SHOW_BACKGROUND);
    }

    public boolean showSprites() {
        return bits.getBitFlag(MaskFlag.SHOW_SPRITES);
    }

    public ArrayList<Color> emphasize() {
        ArrayList<Color> result = new ArrayList<>();
        if (bits.getBitFlag(MaskFlag.EMPHASISE_RED)) {
            result.add(Color.RED);
        }
        if (bits.getBitFlag(MaskFlag.EMPHASISE_GREEN)) {
            result.add(Color.GREEN);
        }
        if (bits.getBitFlag(MaskFlag.EMPHASISE_BLUE)) {
            result.add(Color.BLUE);
        }

        return result;
    }

    public void update(short/* u8 */ data) {
        bits.setBits(data);
    }
}
