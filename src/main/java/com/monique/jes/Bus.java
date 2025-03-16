package com.monique.jes;

import com.monique.jes.joypad.Joypad;
import com.monique.jes.ppu.PPU;
import com.monique.jes.utils.Memory;
import com.monique.jes.utils.Rom;

import static com.monique.jes.utils.Unsign.*;

public class Bus implements Memory {
    private final int RAM = 0x0000; // 16 bit
    private final int RAM_MIRRORS_END = 0x1FFF; // 16 bit
    //private final int PPU_REGISTERS = 0x2000; // 16 bit
    private final int PPU_REGISTERS_MIRRORS_END = 0x3FFF; // 16 bit

    private short[] cpuVram; // 2048 bytes
    private Rom rom;
    private PPU ppu;
    private Joypad joypad;

    private int cycles;

    public Bus(Rom rom) {
        this.rom = rom;
        cpuVram = new short[2048];
        ppu = new PPU(rom.chrRom, rom.mirroring);
        joypad = new Joypad();
        cycles = 0;
    }

    public void tick(short/* u8 */ cycles) {
        this.cycles += cycles;
        ppu.tick(unsignByte(cycles * 3));
    }

    public short readPrgRom(int addr) {
        addr -= 0x8000;

        if (rom.prgRom.length == 0x4000 && addr >= 0x4000) {
            //mirror if needed
            addr = addr % 0x4000;
        }
        return rom.prgRom[addr];
    }

    @Override
    public short memRead(int addr) {
        if (addr >= RAM && addr <= RAM_MIRRORS_END) {

            int mirrorDownAddr = addr & 0x07FF;
            return cpuVram[mirrorDownAddr];

        } else if (addr == 0x2000 || addr == 0x2001 || addr == 0x2003 || addr == 0x2005 || addr == 0x2006 || addr == 0x4014) {
            System.err.printf("Attempt to read from write-only PPU address: %0x\n", addr);
        } else if (addr == 0x2002) {
            return ppu.readStatus();
        } else if (addr == 0x2004) {
            return ppu.readOamData();
        } else if (addr == 0x2007) {
            return ppu.readData();
        } else if (addr >= 0x2008 && addr <= PPU_REGISTERS_MIRRORS_END) {

            int mirrorDownAddr = addr & 0x2007;
            return memRead(mirrorDownAddr);

        } else if (addr == 0x4016) {
            return joypad.read();
        } else if (addr == 0x4017) {
            //ignoring joypad2
        } else if (addr >= 0x8000 && addr <= 0xFFFF) {
            return readPrgRom(addr);
        } else {
            System.out.printf("Ignoring mem access at %d\n.", addr);
        }
        return 0;
    }

    @Override
    public void memWrite(int addr, int value) {
        if (addr >= RAM && addr <= RAM_MIRRORS_END) {
            int mirrorDownAddr = addr & 0x07FF;
            cpuVram[mirrorDownAddr] = (short) (value & 0xFF);
        } else if (addr == 0x2000) {
            ppu.writeToCtrl(unsignByte(value));
        } else if (addr == 0x2001) {
            ppu.writeToMask(unsignByte(value));
        } else if (addr == 0x2002) {
            System.err.println("Attempt to write to PPU status register.");
        } else if (addr == 0x2003) {
            ppu.writeToOamAddr(value);
        } else if (addr == 0x2004) {
            ppu.writeToOamData(value);
        } else if (addr == 0x2005) {
            ppu.writeToScroll(value);
        } else if (addr == 0x2006) {
            ppu.writeToPPUAddr((short) (value & 0xFF));
        } else if (addr == 0x2007) {
            ppu.writeToData(unsignByte(value));
        } else if (addr >= 0x2008 && addr <= PPU_REGISTERS_MIRRORS_END) {

            int mirrorDownAddr = addr & 0x2007;
            memWrite(mirrorDownAddr, value);

        } else if (addr == 0x4014) {

            var buffer = new short[256];
            var hi = (value << 8);
            for (int i = 0; i < 256; i++) {
                buffer[i] = memRead(hi + i);
            }
            ppu.writeOamDma(buffer);

        } else if (addr == 0x4016) {
            joypad.write((short) (value & 0xFF));
        } else if (addr == 0x4017) {
            //ignoring joypad2
        } else if (addr >= 0x8000 && addr <= 0xFFFF) {
            System.err.println("Attempt to write to Cartridge ROM space.");
        } else {
            System.out.printf("Ignoring mem write-access at %d\n.", addr);
        }
    }

    public Joypad getJoypad() {
        return joypad;
    }
}
