package com.monique.jes.ppu;

import static com.monique.jes.utils.Unsign.*;

import java.util.Optional;

import com.monique.jes.ppu.registers.AddrRegister;
import com.monique.jes.ppu.registers.ControlFlag;
import com.monique.jes.ppu.registers.ControlRegister;
import com.monique.jes.ppu.registers.MaskRegister;
import com.monique.jes.ppu.registers.ScrollRegister;
import com.monique.jes.ppu.registers.StatusRegister;
import com.monique.jes.utils.Memory;

public class PPU implements Memory {
    private AddrRegister addr;
    private ControlRegister ctrl;
    private MaskRegister mask;
    private ScrollRegister scroll;
    private StatusRegister status;

    private short/* u8 */ internalDataBuf;
    private short/* u8 */ oamAddr;

    private short/* u8 */[] chrRom; // visuals of a game stored on a cartridge 
    private short/* u8 */[] palleteTable; // internal memory to keep palette tables used by a screen
    private short/* u8 */[] vram; // 2 KiB banks of space to hold background information
    private short/* u8 */[] oamData; // internal memory to keep state of sprites
    private Mirroring mirroring; // gave by the game

    private int/* u16 */ scanline;
    private int/* usize */ cycles;
    private Optional<Short> nmiInterrupt;

    public PPU(short[] chrRom, Mirroring mirroring) {
        this.chrRom = chrRom;
        this.mirroring = mirroring;
        vram = new short[2048];
        oamData = new short[256];
        palleteTable = new short[32];
        oamAddr = 0;
        addr = new AddrRegister();
        ctrl = new ControlRegister();
        mask = new MaskRegister();
        scroll = new ScrollRegister();
        status = new StatusRegister();

        nmiInterrupt = Optional.empty();
    }

    public boolean tick(short/* u8 */ cycles) {
        this.cycles += cycles;
        if (this.cycles >= 341) {
            this.cycles = this.cycles - 341;
            this.scanline += 1;

            if (this.scanline == 241) {
                status.setVBlankStatus(true);
                status.setSpriteZeroHit(false);
                if (this.ctrl.getBitsFlag(ControlFlag.GENERATE_NMI)) {
                    nmiInterrupt = Optional.of((short) 1);
                }
            }

            if (this.scanline >= 262) {
                scanline = 0;
                nmiInterrupt = Optional.empty();
                status.setSpriteZeroHit(false);
                status.resetVBlankStatus();
                return true;
            }
        }
        return false;
    }

    // Horizontal:
    //   [ A ] [ a ]
    //   [ B ] [ b ]
    
    // Vertical:
    //   [ A ] [ B ]
    //   [ a ] [ b ]
    public int/* u16 */ mirrorVramAddr(int/* u16 */ addr) {
        int mirroredVram = addr & 0b10111111111111; // mirror down 0x3000-0x3eff to 0x2000 - 0x2eff
        int vramIndex = mirroredVram - 0x2000; // to vram vector
        int nameTable = vramIndex / 0x400; // to the name table index

        if ((mirroring == Mirroring.VERTICAL && nameTable == 2) || 
            (mirroring == Mirroring.VERTICAL && nameTable == 3)) {
            return vramIndex - 0x800;
        } else if (mirroring == Mirroring.HORIZONTAL && nameTable == 2) {
            return vramIndex - 0x400;
        } else if (mirroring == Mirroring.HORIZONTAL && nameTable == 1) {
            return vramIndex - 0x400;
        } else if (mirroring == Mirroring.HORIZONTAL && nameTable == 3) {
            return vramIndex - 0x800;
        } else {
            return vramIndex;
        }
    }

    public void incrementVramAddr() {
        addr.increment(ctrl.vramAddrIncrement());
    }

    public void writeToCtrl(short /* u8 */ value) {
        boolean beforeNMIStatus = ctrl.getBitsFlag(ControlFlag.GENERATE_NMI);
        ctrl.update(value);
        if (!beforeNMIStatus && ctrl.getBitsFlag(ControlFlag.GENERATE_NMI) && status.isInVBlank()) {
            nmiInterrupt = Optional.of((short) 1);
        }

    }

    public void writeToMask(short/* u8 */ value) {
        mask.update(unsignByte(value));
    }

    public short/* u8 */ readStatus() {
        short data = status.snapshot();
        status.resetVBlankStatus();
        addr.resetLatch();
        scroll.resetLatch();
        return data;
    }

    public void writeToOamAddr(int value) {
        oamAddr = unsignByte(value);
    }

    public void writeToOamData(int value) {
        oamData[oamAddr] = unsignByte(value);
        oamAddr = unsignByte((oamAddr + 1) % oamData.length);
    }

    public short readOamData() {
        return oamData[oamAddr];
    }

    public void writeToScroll(int value) {
        scroll.write(unsignByte(value));
    }

    public void writeToPPUAddr(short /* u8 */ value) {
        addr.update(value);
    }

    public void writeToData(short/* u8 */ value) {
        int addrTmp = addr.get();
        try {
            if (addrTmp <= 0x1FFF) {
                throw new Exception("Attempt to write to chr rom space" + addrTmp);
            } else if (addrTmp <= 0x2FFF) {
                memWrite(mirrorVramAddr(addrTmp), value);
            } else if (addrTmp <= 0x3EFF) {
                throw new Exception("Addr " + addrTmp + " shouldn't be used in reality");
            } else if (addrTmp == 0x3F10 || addrTmp == 0x3F14 || addrTmp == 0x3F18 || addrTmp == 0x3F1C) {
                int addrMirror = addrTmp - 0x10;
                palleteTable[addrMirror - 0x3F00] = value;
            } else if (addrTmp <= 0x3FFF) {
                palleteTable[addrTmp - 0x3F00] = value;
            } else {
                throw new UnexpectedAccessException(addrTmp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        incrementVramAddr();
    }

    public short /* u8 */ readData() {
        var addrTmp = addr.get();
        incrementVramAddr();
        try {
            if (addrTmp <= 0x1FFF) {
                short result = internalDataBuf;
                internalDataBuf = unsignByte(chrRom[addrTmp]);
                return unsignByte(result);
            } else if (addrTmp <= 0x2FFF) {
                short result = internalDataBuf;
                internalDataBuf = unsignByte(vram[mirrorVramAddr(addrTmp)]);
                return unsignByte(result);
            } else if (addrTmp <= 0x3EFF) {
                throw new Exception("Addr " + addrTmp + " shouldn't be used in reality");
            } else if (addrTmp == 0x3F10 || addrTmp == 0x3F14 || addrTmp == 0x3F18 || addrTmp == 0x3F1C) {
                int addrMirror = addrTmp - 0x10;
                return unsignByte(palleteTable[addrMirror - 0x3F00]);
            } else if (addrTmp <= 0x3FFF) {
                return unsignByte(palleteTable[addrTmp - 0x3F00]);
            } else {
                throw new UnexpectedAccessException(addrTmp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void writeOamDma(short[]/* u8 */ data) {
        for (short x : data) {
            oamData[oamAddr] = x;
            oamAddr = unsignByte((oamAddr + 1) % oamData.length);
        }
    }

    public Optional<Short> pollNmiInterrupt() {
        return nmiInterrupt;
    }

    public AddrRegister getAddr() {
        return addr;
    }

    public ControlRegister getCtrl() {
        return ctrl;
    }

    public MaskRegister getMask() {
        return mask;
    }

    public ScrollRegister getScroll() {
        return scroll;
    }

    public StatusRegister getStatus() {
        return status;
    }

    public short getInternalDataBuf() {
        return internalDataBuf;
    }

    public short getOamAddr() {
        return oamAddr;
    }

    public short[] getChrRom() {
        return chrRom;
    }

    public short[] getPalleteTable() {
        return palleteTable;
    }

    public short[] getOamData() {
        return oamData;
    }

    public Mirroring getMirroring() {
        return mirroring;
    }

    public int getScanline() {
        return scanline;
    }

    public int getCycles() {
        return cycles;
    }

    public Optional<Short> getNmiInterrupt() {
        return nmiInterrupt;
    }

    public void setAddr(AddrRegister addr) {
        this.addr = addr;
    }

    public void setCtrl(ControlRegister ctrl) {
        this.ctrl = ctrl;
    }

    public void setMask(MaskRegister mask) {
        this.mask = mask;
    }

    public void setScroll(ScrollRegister scroll) {
        this.scroll = scroll;
    }

    public void setStatus(StatusRegister status) {
        this.status = status;
    }

    public void setInternalDataBuf(short internalDataBuf) {
        this.internalDataBuf = internalDataBuf;
    }

    public void setOamAddr(short oamAddr) {
        this.oamAddr = oamAddr;
    }

    public void setChrRom(short[] chrRom) {
        this.chrRom = chrRom;
    }

    public void setPalleteTable(short[] palleteTable) {
        this.palleteTable = palleteTable;
    }

    public void setOamData(short[] oamData) {
        this.oamData = oamData;
    }

    public void setMirroring(Mirroring mirroring) {
        this.mirroring = mirroring;
    }

    public void setScanline(int scanline) {
        this.scanline = scanline;
    }

    public void setCycles(int cycles) {
        this.cycles = cycles;
    }

    public void setNmiInterrupt(Optional<Short> nmiInterrupt) {
        this.nmiInterrupt = nmiInterrupt;
    }

    @Override
    public short memRead(int addr) {
        return unsignByte(vram[addr]);
    }

    @Override
    public void memWrite(int addr, int value) {
        vram[addr] = unsignByte(value);
    }
}
