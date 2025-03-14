package com.monique.jes.ppu.registers;

import static com.monique.jes.utils.Unsign.unsignByte;

import com.monique.jes.utils.bitflag.BitFlag;

public class ControlRegister {
    private BitFlag bits;

    public ControlRegister() {
        bits = new BitFlag();
    }
    
    public short /* u8 */ vramAddrIncrement() {
        return !getBitsFlag(ControlFlag.VRAM_ADD_INCREMENT) ? (short) 1 : (short) 32;
    }

    public void update(short /* u8 */ data) {
        bits.setBits(unsignByte(data));
    }
    
    /*
     * 7  bit  0
     * ---- ----
     * VPHB SINN
     * |||| ||||
     * |||| ||++- Base nametable address
     * |||| ||    (0 = $2000; 1 = $2400; 2 = $2800; 3 = $2C00)
     * |||| |+--- VRAM address increment per CPU read/write of PPUDATA
     * |||| |     (0: add 1, going across; 1: add 32, going down)
     * |||| +---- Sprite pattern table address for 8x8 sprites
     * ||||       (0: $0000; 1: $1000; ignored in 8x16 mode)
     * |||+------ Background pattern table address (0: $0000; 1: $1000)
     * ||+------- Sprite size (0: 8x8 pixels; 1: 8x16 pixels)
     * |+-------- PPU master/slave select
     * |          (0: read backdrop from EXT pins; 1: output color on EXT pins)
     * +--------- Generate an NMI at the start of the
     *            vertical blanking interval (0: off; 1: on)
     */
    public void setBitsFlag(ControlFlag flag, boolean value) {
        bits.setBitFlag(flag, value);
    }

    public boolean getBitsFlag(ControlFlag flag) {
        return bits.getBitFlag(flag);
    }
}
