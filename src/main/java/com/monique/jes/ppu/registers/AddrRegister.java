package com.monique.jes.ppu.registers;

import com.monique.jes.utils.Pair;
import static com.monique.jes.utils.Unsign.unsignShort;
import static com.monique.jes.utils.Unsign.unsignByte;

public class AddrRegister {
    private Pair value;
    private boolean hiPtr;

    public AddrRegister() {
        value = new Pair((short) 0, (short) 0); // high byte first, low byte second
        hiPtr = true;
    }

    public void set(int /* u16 */ data) {
        data = unsignShort(data);

        value.first = (short) unsignByte(data >> 8);
        value.second = (short) unsignByte(data & 0xFF);
    }
    public int get() {
        return unsignShort((unsignShort(value.first) << 8) | unsignShort(value.second));
    }

    public void update(short /* u8 */ data) {
        data = unsignByte(data);

        if (hiPtr) value.first = data;
        else value.second = data;

        if (get() > 0x3FFF) { // mirror down addr above 0x3FFF
            set(get() & 0b11111111111111);
        }
        hiPtr = !hiPtr;
    }

    public void increment(short /* u8 */ inc) {
        short /* u8 */ low = unsignByte(value.second);
        value.second = unsignByte(value.second + inc);

        if (low > value.second) {
            value.first = unsignByte(value.first + 1);
        }

        if (get() > 0x3FFF) { // mirror down addr above 0x3FFF
            set(get() & 0b11111111111111);
        }
    }

    public void resetLatch() {
        hiPtr = true;
    }
}
