package com.monique.jes.ppu.registers;

import com.monique.jes.utils.bitflag.BitFlag;

public class StatusRegister {
    private BitFlag bits;
    
    public StatusRegister() {
        bits = new BitFlag();
    }

    public void setVBlankStatus(boolean status) {
        bits.setBitFlag(StatusFlag.VBLANK_STARTED, status);
    }

    public void setSpriteZeroHit(boolean status) {
        bits.setBitFlag(StatusFlag.SPRITE_ZERO_HIT, status);
    }

    public void setSpriteOverflow(boolean status) {
        bits.setBitFlag(StatusFlag.SPRITE_OVERFLOW, status);
    }

    public void resetVBlankStatus() {
        bits.setBitFlag(StatusFlag.VBLANK_STARTED, false);
    }

    public boolean isInVBlank() {
        return bits.getBitFlag(StatusFlag.VBLANK_STARTED);
    }

    public short/* u8 */ snapshot() {
        return bits.getBits();
    }
}
