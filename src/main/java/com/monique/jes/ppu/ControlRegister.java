package com.monique.jes.ppu;

import static com.monique.jes.utils.Unsign.unsignByte;

public class ControlRegister {
    private short /* u8 */ bits;

    public ControlRegister() {
        bits = 0;
    }
    
    public short /* u8 */ vramAddrIncrement() {
        return !getBitsFlag(Flag.VRAM_ADD_INCREMENT) ? (short) 1 : (short) 32;
    }

    public void update(short /* u8 */ data) {
        bits = unsignByte(data);
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
    public void setBitsFlag(Flag flag, boolean value) {
        switch (flag) {
            case GENERATE_NMI: // 1000 0000
                if (value) {
                    bits |= 0x80;
                } else {
                    bits &= 0x7F;
                }
                break;
            case MASTER_SLAVE_SELECT: // 0100 0000
                if (value) {
                    bits |= 0x40;
                } else {
                    bits &= 0xBF;
                }
                break;
            case SPRITE_SIZE: // 0010 0000
                if (value) {
                    bits |= 0x20;
                } else {
                    bits &= 0xDF;
                }
                break;
            case BACKGROUND_PATTERN_ADDR: // 0001 0000
                if (value) {
                    bits |= 0x10;
                } else {
                    bits &= 0xEF;
                }
                break;
            case SPRITE_PATTERN_ADDR: // 0000 1000
                if (value) {
                    bits |= 0x8;
                } else {
                    bits &= 0xF7;
                }
                break;
            case VRAM_ADD_INCREMENT: // 0000 0100
                if (value) {
                    bits |= 0x4;
                } else {
                    bits &= 0xFB;
                }
                break;
            case NAMETABLE2: // 0000 0010
                if (value) {
                    bits |= 0x2;
                } else {
                    bits &= 0xFD;
                }
                break;
            case NAMETABLE1: // 0000 0001
                if (value) {
                    bits |= 0x1;
                } else {
                    bits &= 0xFE;
                }
                break;
            default:
                break;
        }
    }

    public boolean getBitsFlag(Flag flag) {
        return switch (flag) {
            case GENERATE_NMI: // 1000 0000
                yield (bits & 0x80) != 0;
            case MASTER_SLAVE_SELECT: // 0100 0000
                yield (bits & 0x40) != 0;
            case SPRITE_SIZE: // 0010 0000
                yield (bits & 0x20) != 0;
            case BACKGROUND_PATTERN_ADDR: // 0001 0000
                yield (bits & 0x10) != 0;
            case SPRITE_PATTERN_ADDR: // 0000 1000
                yield (bits & 0x8) != 0;
            case VRAM_ADD_INCREMENT: // 0000 0100
                yield (bits & 0x4) != 0;
            case NAMETABLE2: // 0000 0010
                yield (bits & 0x2) != 0;
            case NAMETABLE1: // 0000 0001
                yield (bits & 0x1) != 0;
            default:
                yield false;
        };
    }
}
