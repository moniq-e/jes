package com.monique.jes.utils;

public interface Memory {

    public short memRead(int addr);

    public void memWrite(int addr, int value);

    public default int memRead16(int pos) {
        return (memRead(pos + 1) << 8) | memRead(pos);
    }

    public default void memWrite16(int pos, int value) {
        memWrite(pos, (short) (value & 0xFF));
        memWrite(pos + 1, (short) ((value >> 8) & 0xFF));
    }
}
