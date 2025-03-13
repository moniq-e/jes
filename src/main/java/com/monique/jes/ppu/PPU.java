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
    }

    public void writeToPPUAddr(short /* u8 */ value) {
        addr.update(value);
    }

    public short readData() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'readData'");
    }

    public void writeToCtrl(int value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'writeToCtrl'");
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
}
