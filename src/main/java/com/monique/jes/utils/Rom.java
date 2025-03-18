package com.monique.jes.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import com.monique.jes.ppu.Mirroring;

public class Rom {
    public static final short[] NES_TAG = new short[]{ 0x4E, 0x45, 0x53, 0x1A };
    public static final int PRG_ROM_PAGE_SIZE = 16384;
    public static final int CHR_ROM_PAGE_SIZE = 8192;

    public final short[] prgRom;
    public final short[] chrRom;
    public final short mapper;
    public final Mirroring mirroring;

    public Rom(short[] raw) throws Exception {
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

    public static Rom of(InputStream unparsedRaw) throws Exception {
        return new Rom(rawParser(unparsedRaw));
    }

    public static Rom testRomContaining(short[] program) throws Exception {
        var resized = new short[2 * PRG_ROM_PAGE_SIZE];
        for (int i = 0; i < program.length; i++) {
            resized[i] = program[i];
        }

        var chrRom = new short[CHR_ROM_PAGE_SIZE];
        Arrays.fill(chrRom, (short) 2);

        var testRom = createRom(new TestRom(
            new short[]{ 0x4E, 0x45, 0x53, 0x1A, 0x02, 0x01, 0x31, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            Optional.empty(),
            resized,
            chrRom
        ));
        return new Rom(testRom);
    }

    private static short[] createRom(TestRom rom) {
        var res = new ArrayList<Short>();

        for (int i = 0; i < rom.header.length; i++) {
            res.add(rom.header[i]);
        }
        if (rom.trainer.isPresent()) {
            var trainer = rom.trainer.get();
            for (int i = 0; i < trainer.length; i++) {
                res.add(trainer[i]);
            }
        }
        for (int i = 0; i < rom.pgpRom.length; i++) {
            res.add(rom.pgpRom[i]);
        }
        for (int i = 0; i < rom.chrRom.length; i++) {
            res.add(rom.chrRom[i]);
        }

        var array = new short[res.size()];
        for (int i = 0; i < res.size(); i++) {
            array[i] = res.get(i).shortValue();
        }
        return array;
    }

    public static short[] rawParser(InputStream raw) {
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

    private static class TestRom {
        public final short[] header;
        public final Optional<short[]> trainer;
        public final short[] pgpRom;
        public final short[] chrRom;

        public TestRom(short[] header, Optional<short[]> trainer, short[] pgpRom, short[] chrRom) {
            this.header = header;
            this.trainer = trainer;
            this.pgpRom = pgpRom;
            this.chrRom = chrRom;
        }
    }
}
