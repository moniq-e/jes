package com.monique.jes.ppu;

public class PPU {
    private AddrRegister addr;
    private short/* u8 */[] chrRom; // visuals of a game stored on a cartridge 
    private ControlRegister ctrl;
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
        addr = new AddrRegister();
        ctrl = new ControlRegister();
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

        if (addrTmp <= 0x1FFF) {
            
        } else if (addrTmp <= 0x2FFF) {

        } else if (addrTmp <= 0x3EFF) {

        } else if (addrTmp <= 0x3FFF) {
            return palleteTable[addrTmp - 0x3F00];
        } else {
             
        }
    }

    public void writeToCtrl(short /* u8 */ value) {
        ctrl.update(value);
    }

    public void writeToData(int value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'writeToData'");
    }

    public short readStatus() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'readStatus'");
    }

    public short readOamData() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'readOamData'");
    }

    public void writeToMask(int value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'writeToMask'");
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
