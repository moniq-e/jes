package com.monique.jes.joypad;

import com.monique.jes.utils.bitflag.BitFlag;

public class Joypad {
    private boolean strobe;
    private short buttonIndex;
    private BitFlag buttonStatus; // 8 bit

    public Joypad() {
        strobe = false;
        buttonIndex = 0;
        buttonStatus = new BitFlag();
    }

    public void write(short value) {
        strobe = (value & 1) == 1;

        if (strobe) {
            buttonIndex = 0;
        }
    }

    public short read() {
        if (buttonIndex > 7) {
            return (short) 1;
        }
        var res = (short) ((buttonStatus.getBits() & (1 << buttonIndex)) >> buttonIndex);
        if (!strobe && buttonIndex <= 7) {
            buttonIndex += 1;
        }
        return res;
    }

    public void setButtonPressed(JoypadButton flag, boolean pressed) {
        buttonStatus.setBitFlag(flag, pressed);
    }
}
