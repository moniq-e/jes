package com.monique;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.monique.jes.ppu.Mirroring;
import com.monique.jes.ppu.PPU;
import static com.monique.jes.utils.Unsign.*;

public class PPUTest {

    public PPU newEmptyRom() {
        return new PPU(new short[2048], Mirroring.HORIZONTAL);
    }

    @Test
    public void testPPUVRamWrites() {
        var ppu = newEmptyRom();

        ppu.writeToPPUAddr(unsignByte(0x23));
        ppu.writeToPPUAddr(unsignByte(0x05));
        ppu.writeToData(unsignByte(0x66));

        assertEquals(unsignByte(0x66), ppu.memRead(0x0305));
    }

    @Test
    public void testPPUVRamReads() {
        var ppu = newEmptyRom();
        ppu.writeToCtrl((short) 0);
        
        ppu.memWrite(0x0305, unsignByte(0x66));

        ppu.writeToPPUAddr(unsignByte(0x23));
        ppu.writeToPPUAddr(unsignByte(0x05));

        ppu.readData(); 
        assertEquals(ppu.getAddr().get(), 0x2306);
        assertEquals(ppu.readData(), 0x66);
    }

    @Test
    public void testPPUVRamReadsCrossPage() {
        var ppu = newEmptyRom();
        ppu.writeToCtrl((short) 0);
        ppu.memWrite(0x01FF, unsignByte(0x66));
        ppu.memWrite(0x0200, unsignByte(0x77));

        ppu.writeToPPUAddr(unsignByte(0x21));
        ppu.writeToPPUAddr(unsignByte(0xFF));

        ppu.readData(); 
        assertEquals(ppu.readData(), 0x66);
        assertEquals(ppu.readData(), 0x77);
    }

    @Test
    public void testPPUVRamReadsStep32() {
        var ppu = newEmptyRom();
        ppu.writeToCtrl((short) 0b100);

        ppu.memWrite(0x01FF, unsignByte(0x66));
        ppu.memWrite(0x01FF + 32, unsignByte(0x77));
        ppu.memWrite(0x01FF + 64, unsignByte(0x88));

        ppu.writeToPPUAddr(unsignByte(0x21));
        ppu.writeToPPUAddr(unsignByte(0xFF));

        ppu.readData(); 
        assertEquals(ppu.readData(), 0x66);
        assertEquals(ppu.readData(), 0x77);
        assertEquals(ppu.readData(), 0x88);
    }

    @Test
    public void testVRamHorizontalMirror() {
        var ppu = newEmptyRom();
        ppu.writeToPPUAddr(unsignByte(0x24));
        ppu.writeToPPUAddr(unsignByte(0x05));

        ppu.writeToData(unsignByte(0x66));

        ppu.writeToPPUAddr(unsignByte(0x28));
        ppu.writeToPPUAddr(unsignByte(0x05));

        ppu.writeToData(unsignByte(0x77));

        ppu.writeToPPUAddr(unsignByte(0x20));
        ppu.writeToPPUAddr(unsignByte(0x05));

        ppu.readData();
        assertEquals(ppu.readData(), 0x66);
        
        ppu.writeToPPUAddr(unsignByte(0x2C));
        ppu.writeToPPUAddr(unsignByte(0x05));

        ppu.readData();
        assertEquals(ppu.readData(), 0x77);
    }

    @Test
    public void testVRamVerticalMirror() {
        var ppu = new PPU(new short[2048], Mirroring.VERTICAL);

        ppu.writeToPPUAddr(unsignByte(0x20));
        ppu.writeToPPUAddr(unsignByte(0x05));

        ppu.writeToData(unsignByte(0x66));

        ppu.writeToPPUAddr(unsignByte(0x2C));
        ppu.writeToPPUAddr(unsignByte(0x05));

        ppu.writeToData(unsignByte(0x77));

        ppu.writeToPPUAddr(unsignByte(0x28));
        ppu.writeToPPUAddr(unsignByte(0x05));

        ppu.readData();
        assertEquals(ppu.readData(), 0x66);
        
        ppu.writeToPPUAddr(unsignByte(0x24));
        ppu.writeToPPUAddr(unsignByte(0x05));

        ppu.readData();
        assertEquals(ppu.readData(), 0x77);
    }

    @Test
    public void testReadStatusResetsLatch() {
        var ppu = newEmptyRom();

        ppu.memWrite(0x0305, unsignByte(0x66));

        ppu.writeToPPUAddr(unsignByte(0x23));
        ppu.writeToPPUAddr(unsignByte(0x05));

        ppu.readData(); 
        assertEquals(ppu.getAddr().get(), 0x2306);
        assertEquals(ppu.readData(), 0x66);
    }

    @Test
    public void testPPUVRamMirroring() {
        var ppu = newEmptyRom();
        ppu.writeToCtrl((short) 0);
        ppu.memWrite(0x0305, 0x66);
        
        ppu.writeToPPUAddr(unsignByte(0x63));
        ppu.writeToPPUAddr(unsignByte(0x05));

        ppu.readData();
        assertEquals(ppu.readData(), 0x66);
    }

    @Test
    public void testReadStatusResetsVBlank() {
        var ppu = newEmptyRom();
        var statusTmp = ppu.getStatus();
        statusTmp.setVBlankStatus(true);
        ppu.setStatus(statusTmp);

        short status = ppu.readStatus();

        assertEquals(status >> 7, 1);
        assertEquals(ppu.getStatus().snapshot() >> 7, 0);
    }

    @Test
    public void testOamReadWrite() {
        var ppu = newEmptyRom();
        ppu.writeToOamAddr(0x10);
        ppu.writeToOamData(0x66);
        ppu.writeToOamData(0x77);

        ppu.writeToOamAddr(0x10);
        assertEquals(ppu.readOamData(), 0x66);

        ppu.writeToOamAddr(0x11);
        assertEquals(ppu.readOamData(), 0x77);
    }

    @Test
    public void testOamDma() {
        var ppu = newEmptyRom();

        var data = new short[256];
        for (int i = 0; i < data.length; i++) {
            data[i] = 0x66;
        }
        data[0] = 0x77;
        data[255] = 0x88;

        ppu.writeToOamAddr(0x10);
        ppu.writeOamDma(data);
        
        ppu.writeToOamAddr(0xF);
        assertEquals(ppu.readOamData(), 0x88);

        ppu.writeToOamAddr(0x10);
        ppu.writeToOamAddr(0x77);
        ppu.writeToOamAddr(0x11);
        ppu.writeToOamAddr(0x66);
    }
}
