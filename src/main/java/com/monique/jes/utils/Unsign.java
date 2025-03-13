package com.monique.jes.utils;

public interface Unsign {
    /**
     * Unsign a short
     * 
     * @param signed short
     * @return unsigned short
     */
    public static int unsignShort(int signed) {
        return signed & 0xFFFF;
    }

    /**
     * Unsign a byte
     * 
     * @param signed byte
     * @return unsigned byte
     */
    public static short unsignByte(int signed) {
        return (short) (signed & 0xFF);
    }
}
