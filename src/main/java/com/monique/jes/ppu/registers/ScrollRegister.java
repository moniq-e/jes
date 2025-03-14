package com.monique.jes.ppu.registers;

public class ScrollRegister {
    private short/* u8 */ scrollX;
    private short/* u8 */ scrollY;
    private boolean latch;

    public ScrollRegister() {
        scrollX = 0;
        scrollY = 0;
        latch = false;
    }

    public void write(short/* u8 */ data) {
        if (!latch) {
            scrollX = data;
        } else {
            scrollY = data;
        }
        latch = !latch;
    }

    public void resetLatch() {
        latch = false;
    }
}
