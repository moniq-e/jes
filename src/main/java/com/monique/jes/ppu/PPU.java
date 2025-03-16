package com.monique.jes.ppu;

import static com.monique.jes.utils.Unsign.*;

import com.monique.jes.ppu.registers.AddrRegister;
import com.monique.jes.ppu.registers.ControlRegister;
import com.monique.jes.ppu.registers.MaskRegister;
import com.monique.jes.ppu.registers.ScrollRegister;
import com.monique.jes.ppu.registers.StatusRegister;

public class PPU {
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
    }

    public void writeToPPUAddr(short /* u8 */ value) {
        addr.update(value);
    }

    public void incrementVramAddr() {
        addr.increment(ctrl.vramAddrIncrement());
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
                // TODO: Unimplemented addr
                return -1;
            } else if (addrTmp <= 0x3FFF) {
                return unsignByte(palleteTable[addrTmp - 0x3F00]);
            } else {
                throw new UnexpectedAccessException(addrTmp);
            }
        } catch (UnexpectedAccessException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void writeToCtrl(short /* u8 */ value) {
        ctrl.update(value);
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

    public void writeToData(int value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'writeToData'");
    }

    public short/* u8 */ readStatus() {
        short data = status.snapshot();
        status.resetVBlankStatus();
        addr.resetLatch();
        scroll.resetLatch();
        return data;
    }

    public short readOamData() {
        return oamData[oamAddr];
    }

    public void writeToMask(short/* u8 */ value) {
        mask.update(unsignByte(value));
    }

    public void writeToOamAddr(int value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'writeToOamAddr'");
    }

    public void writeToOamData(int value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'writeToOamData'");
    }

    public void writeToScroll(int value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'writeToScroll'");
    }

    public void writeOamDma(short[] buffer) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'writeOamDma'");
    }
}
