package com.monique;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import org.junit.Test;

import com.monique.jes.Bus;
import com.monique.jes.cpu.CPU;
import com.monique.jes.utils.Rom;

public class JESTest implements Trace {
    public final Rom TEST_ROM;

    public JESTest() throws Exception {
        TEST_ROM = Rom.of(getClass().getResourceAsStream("/nestest.nes"));
    }

    @Test
    public void testRom() throws Exception {
        new File("./log.txt").delete();
        var log = new FileWriter("./log.txt");

        var cpu = new CPU(new Bus(TEST_ROM));
        cpu.reset();
        cpu.setPC(0xC000);
        try {
            cpu.runWithCallback(c -> {
                try {
                    log.write(trace(c) + "\n");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        log.close();
    }

    @Test
    public void testFormatTrace() {
        var bus = new Bus(TEST_ROM);
        bus.memWrite(100, 0xA2);
        bus.memWrite(101, 0x01);
        bus.memWrite(102, 0xCA);
        bus.memWrite(103, 0x88);
        bus.memWrite(104, 0x00);

        var cpu = new CPU(bus);
        cpu.setPC(0x64);
        cpu.setAcc(1);
        cpu.setIrx(2);
        cpu.setIry(3);
        var result = new ArrayList<String>();
        cpu.runWithCallback(c -> {
            try {
                result.add(trace(c));
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        });
        assertEquals("0064  A2 01     LDX #$01                        A:01 X:02 Y:03 P:24 SP:FD", result.get(0));
        assertEquals("0066  CA        DEX                             A:01 X:01 Y:03 P:24 SP:FD", result.get(1));
        assertEquals("0067  88        DEY                             A:01 X:00 Y:03 P:26 SP:FD", result.get(2));
    }

    @Test
    public void testMemReadWriteToRam() {
        var bus = new Bus(TEST_ROM);
        bus.memWrite(0x01, 0x55);
        assertEquals(bus.memRead(0x01), 0x55);
    }

    @Test
    public void testFormatMemAccess() {
        var bus = new Bus(TEST_ROM);
        // ORA ($33), Y
        bus.memWrite(0x64, 0x11);
        bus.memWrite(0x65, 0x33);

        //data
        bus.memWrite(0x33, 00);
        bus.memWrite(0x34, 04);
 
        //target cell
        bus.memWrite(0x400, 0xAA);
 
        var cpu = new CPU(bus);
        cpu.setPC(0x64);
        cpu.setIry(0);
        var result = new ArrayList<String>();
        cpu.runWithCallback(c -> {
            try {
                result.add(trace(c));
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        });
        assertEquals("0064  11 33     ORA ($33),Y = 0400 @ 0400 = AA  A:00 X:00 Y:00 P:24 SP:FD", result.get(0));
    }

    @Test
    public void testImmediateLDA() throws Exception {
        var cpu = new CPU(new Bus(Rom.testRomContaining(new short[]{0xA9, 0x05, 0x00})));
        cpu.run();

        assertEquals(cpu.getAcc(), 0x05);
        assertEquals((cpu.getStatus() & 0x2), 0);
        assertEquals((cpu.getStatus() & 0x80), 0);
    }

    @Test
    public void testLDAZeroFlag() throws Exception {
        var cpu = new CPU(new Bus(Rom.testRomContaining(new short[]{0xA9, 0x00, 0x00})));
        cpu.run();

        assertEquals((cpu.getStatus() & 0x2), 0x2);
    }

    @Test
    public void testLDAFromMemory() throws Exception {
        var cpu = new CPU(new Bus(Rom.testRomContaining(new short[]{0xA5, 0x10, 0x00})));
        cpu.memWrite(0x10, 0x55);
        cpu.run();

        assertEquals(cpu.getAcc(), 0x55);
    }

    @Test
    public void testTAX() throws Exception {
        var cpu = new CPU(new Bus(Rom.testRomContaining(new short[]{0xAA, 0x00})));
        cpu.setAcc((short) 0xA);
        cpu.run();

        assertEquals(cpu.getIrx(), 0xA);
    }

    @Test
    public void test5OpsWorkingTogether() throws Exception {
        var cpu = new CPU(new Bus(Rom.testRomContaining(new short[]{0xA9, 0xC0, 0xAA, 0xE8, 0x00})));
        cpu.run();

        assertEquals(cpu.getIrx(), 0xC1);
    }

    @Test
    public void testInxOverflow() throws Exception {
        var cpu = new CPU(new Bus(Rom.testRomContaining(new short[]{0xE8, 0xE8, 0x00})));
        cpu.setIrx((short) 0xFF);
        cpu.run();

        assertEquals(cpu.getIrx(), 1);
    }
}
