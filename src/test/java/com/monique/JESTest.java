package com.monique;

import static org.junit.Assert.*;

import org.junit.Test;

import com.monique.jes.CPU;

public class JESTest {

    @Test
    public void testImmediateLDA() {
        var cpu = new CPU();
        cpu.loadAndRun(new short[]{0xA9, 0x05, 0x00});

        assertEquals(cpu.getAcc(), 0x05);
        assertEquals((cpu.getStatus() & 0x2), 0);
        assertEquals((cpu.getStatus() & 0x80), 0);
    }

    @Test
    public void testLDAZeroFlag() {
        var cpu = new CPU();
        cpu.loadAndRun(new short[]{0xA9, 0x00, 0x00});

        assertEquals((cpu.getStatus() & 0x2), 0x2);
    }

    @Test
    public void testLDAFromMemory() {
        var cpu = new CPU();
        cpu.memWrite(0x10, 0x55);
        cpu.loadAndRun(new short[]{0xA5, 0x10, 0x00});

        assertEquals(cpu.getAcc(), 0x55);
    }

    @Test
    public void testTAX() {
        var cpu = new CPU();
        cpu.loadRom(new short[]{0xAA, 0x00});
        cpu.reset();
        cpu.setAcc((short) 0xA);
        cpu.run();

        assertEquals(cpu.getIrx(), 0xA);
    }

    @Test
    public void test5OpsWorkingTogether() {
        var cpu = new CPU();
        cpu.loadAndRun(new short[]{0xA9, 0xC0, 0xAA, 0xE8, 0x00});

        assertEquals(cpu.getIrx(), 0xC1);
    }

    @Test
    public void testInxOverflow() {
        var cpu = new CPU();
        cpu.loadRom(new short[]{0xE8, 0xE8, 0x00});
        cpu.reset();
        cpu.setIrx((short) 0xFF);
        cpu.run();

        assertEquals(cpu.getIrx(), 1);
    }
}
