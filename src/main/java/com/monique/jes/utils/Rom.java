package com.monique.jes.utils;

import java.io.InputStream;
import java.util.ArrayList;

import com.monique.jes.ppu.Mirroring;

public class Rom {
    public final short[] NES_TAG = new short[]{ 0x4E, 0x45, 0x53, 0x1A };
    public final int PRG_ROM_PAGE_SIZE = 16384;
    public final int CHR_ROM_PAGE_SIZE = 8192;

    public final short[] prgRom;
    public final short[] chrRom;
    public final short mapper;
    public final Mirroring mirroring;

    public Rom(InputStream unparsedRaw) throws Exception {
        var raw = rawParser(unparsedRaw);

        if (!isFileINES(raw)) {
            throw new Exception("File is not in iNES file format");
        }

        mapper = (short) (raw[7] & 0xF0 | raw[6] >> 4);

        var inesVersion = (raw[7] >> 2) & 0x3;
        if (inesVersion != 0) {
            throw new Exception("Only supported version of iNES is 1.0");
        }

        var fourScreen = (raw[6] & 0x8) != 0;
        var vertMirror = (raw[6] & 0x1) != 0;

        if (fourScreen) mirroring = Mirroring.FOUR_SCREEN;
        else if (vertMirror) mirroring = Mirroring.VERTICAL;
        else mirroring = Mirroring.HORIZONTAL;

        var prgRomSize = raw[4] * PRG_ROM_PAGE_SIZE;
        var chrRomSize = raw[5] * CHR_ROM_PAGE_SIZE;

        var skipTrainer = (raw[6] & 0x4) != 0;

        var prgRomStart = 16 + (skipTrainer ? 512 : 0);
        var chrRomStart = prgRomStart + prgRomSize;

        prgRom = new short[prgRomSize];
        var prgRomIndex = 0;
        for (int i = prgRomStart; i < (prgRomStart + prgRomSize); i++) {
            prgRom[prgRomIndex] = raw[i];
            prgRomIndex++;
        }

        chrRom = new short[chrRomSize];
        var chrRomIndex = 0;
        for (int i = chrRomStart; i < (chrRomStart + chrRomSize); i++) {
            chrRom[chrRomIndex] = raw[i];
            chrRomIndex++;
        }
    }

    public boolean isFileINES(short[] raw) {
        var res = true;
        for (int i = 0; i < NES_TAG.length; i++) {
            if (NES_TAG[i] != raw[i]) {
                res = false;
                break;
            }
        }
        return res;
    }

    public short[] rawParser(InputStream raw) {
        var parsedRaw = new ArrayList<Short>();

        try {
            int data;
            while ((data = raw.read()) != -1) {
                parsedRaw.add((short) data);
            }
            raw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        var array = new short[parsedRaw.size()];
        for (int i = 0; i < parsedRaw.size(); i++) {
            array[i] = parsedRaw.get(i).shortValue();
        }
        return array;
    }
}
