package com.monique.jes.utils.bitflag;

public class BitFlag {
    private short bits;
    private final short DEFAULT_BITS;

    public BitFlag() {
        DEFAULT_BITS = 0;
        reset();
    }

    public BitFlag(short defaultBits) {
        DEFAULT_BITS = defaultBits;
        reset();
    }

    public short getBits() {
        return bits;
    }

    public void setBits(short bits) {
        this.bits = bits;
    }

    public void reset() {
        bits = DEFAULT_BITS;
    }

    public void setBitFlag(Flag flag, boolean value) {
        switch (flag.getFlag()) {
            case F1: // 1000 0000
                if (value) {
                    bits |= 0x80;
                } else {
                    bits &= 0x7F;
                }
                break;
            case F2: // 0100 0000
                if (value) {
                    bits |= 0x40;
                } else {
                    bits &= 0xBF;
                }
                break;
            case F3: // 0010 0000
                if (value) {
                    bits |= 0x20;
                } else {
                    bits &= 0xDF;
                }
                break;
            case F4: // 0001 0000
                if (value) {
                    bits |= 0x10;
                } else {
                    bits &= 0xEF;
                }
                break;
            case F5: // 0000 1000
                if (value) {
                    bits |= 0x8;
                } else {
                    bits &= 0xF7;
                }
                break;
            case F6: // 0000 0100
                if (value) {
                    bits |= 0x4;
                } else {
                    bits &= 0xFB;
                }
                break;
            case F7: // 0000 0010
                if (value) {
                    bits |= 0x2;
                } else {
                    bits &= 0xFD;
                }
                break;
            case F8: // 0000 0001
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

    public boolean getBitFlag(Flag flag) {
        return switch (flag.getFlag()) {
            case F1: // 1000 0000
                yield (bits & 0x80) != 0;
            case F2: // 0100 0000
                yield (bits & 0x40) != 0;
            case F3: // 0010 0000
                yield (bits & 0x20) != 0;
            case F4: // 0001 0000
                yield (bits & 0x10) != 0;
            case F5: // 0000 1000
                yield (bits & 0x8) != 0;
            case F6: // 0000 0100
                yield (bits & 0x4) != 0;
            case F7: // 0000 0010
                yield (bits & 0x2) != 0;
            case F8: // 0000 0001
                yield (bits & 0x1) != 0;
            default:
                yield false;
        };
    }
}
