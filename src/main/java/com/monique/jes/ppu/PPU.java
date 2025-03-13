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
}
