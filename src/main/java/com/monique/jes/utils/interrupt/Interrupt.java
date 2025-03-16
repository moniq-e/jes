package com.monique.jes.utils.interrupt;

public class Interrupt {
    public static final Interrupt NMI = new Interrupt();
    private InterruptType type;
    private int vectorAddr; // 16 bit
    private short bFlagMask; // 8 bit
    private short cpuCicles; // 8 bit

    public Interrupt() {
        this(InterruptType.NMI, (short) 0xFFFA, (short) 0x20, (short) 2);
    }

    public Interrupt(InterruptType type, int vectorAddr, short bFlagMask, short cpuCicles) {
        this.type = type;
        this.vectorAddr = vectorAddr;
        this.bFlagMask = bFlagMask;
        this.cpuCicles = cpuCicles;
    }

    public InterruptType getType() {
        return type;
    }

    public int getVectorAddr() {
        return vectorAddr;
    }

    public short getBFlagMask() {
        return bFlagMask;
    }

    public short getCpuCicles() {
        return cpuCicles;
    }
}