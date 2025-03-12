package com.monique.jes;

import com.monique.jes.utils.Memory;

public class Bus implements Memory {
    private short[] cpuVram; // 2048 bytes
    private final int RAM = 0x0000; // 16 bit
    private final int RAM_MIRRORS_END = 0x1FFF; // 16 bit
    private final int PPU_REGISTERS = 0x2000; // 16 bit
    private final int PPU_REGISTERS_MIRRORS_END = 0x3FFF; // 16 bit

    public Bus() {
        cpuVram = new short[2048];
    }

    @Override
    public short memRead(int addr) {
        if (addr >= RAM && addr <= RAM_MIRRORS_END) {

            int mirrorDownAddr = addr & 0x07FF;
            return cpuVram[mirrorDownAddr];

        } else if (addr >= PPU_REGISTERS && addr <= PPU_REGISTERS_MIRRORS_END) {
            int mirrorDownAddr = addr & 0x2007;
            //return cpuVram[mirrorDownAddr];
        } else {
            System.out.printf("Ignoring mem access at %d\n", addr);
        }
        return 0;
    }

    @Override
    public void memWrite(int addr, int value) {
        if (addr >= RAM && addr <= RAM_MIRRORS_END) {

            int mirrorDownAddr = addr & 0x07FF;
            cpuVram[mirrorDownAddr] = (short) (value & 0xFF);

        } else if (addr >= PPU_REGISTERS && addr <= PPU_REGISTERS_MIRRORS_END) {
            int mirrorDownAddr = addr & 0x2007;
            //return cpuVram[mirrorDownAddr];
        } else {
            System.out.printf("Ignoring mem write-access at %d\n", addr);
        }
    }
}
