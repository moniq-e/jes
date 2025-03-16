package com.monique.jes.ppu.registers;

import com.monique.jes.utils.Pair;
import static com.monique.jes.utils.Unsign.unsignShort;
import static com.monique.jes.utils.Unsign.unsignByte;

public class AddrRegister {
    private Pair<Short, Short> value; // 8 bit each
    private boolean hiPtr;

    public AddrRegister() {
        value = new Pair<>((short) 0, (short) 0); // high byte first, low byte second
        hiPtr = true;
    }

    public void set(int /* u16 */ data) {
        data = unsignShort(data);

        value.setFirst(unsignByte(data >> 8));
        value.setSecond(unsignByte(data & 0xFF));
    }
    public int get() {
        return unsignShort((unsignShort(value.getFirst()) << 8) | unsignShort(value.getFirst()));
    }

    public void update(short /* u8 */ data) {
        data = unsignByte(data);

        if (hiPtr) value.setFirst(data);
        else value.setSecond(data);

        if (get() > 0x3FFF) { // mirror down addr above 0x3FFF
            set(get() & 0b11111111111111);
        }
        hiPtr = !hiPtr;
    }

    public void increment(short /* u8 */ inc) {
        short /* u8 */ low = unsignByte(value.getSecond());
        value.setSecond(unsignByte(value.getSecond() + inc));

        if (low > value.getSecond()) {
            value.setFirst(unsignByte(value.getFirst() + 1));
        }

        if (get() > 0x3FFF) { // mirror down addr above 0x3FFF
            set(get() & 0b11111111111111);
        }
    }

    public void resetLatch() {
        hiPtr = true;
    }
}
