package com.monique.jes.cpu;

import java.util.ArrayList;
import java.util.HashMap;

public class Opcode {
    private static final ArrayList<Opcode> opcodes = new ArrayList<>();

    static {
        opcodes.add(new Opcode(0x00, "BRK", 1, 7));

        opcodes.add(new Opcode(0x69, "ADC", 2, 2, AddressingMode.Immediate));
        opcodes.add(new Opcode(0x65, "ADC", 2, 3, AddressingMode.ZeroPage));
        opcodes.add(new Opcode(0x75, "ADC", 2, 4, AddressingMode.ZeroPageX));
        opcodes.add(new Opcode(0x6D, "ADC", 3, 4, AddressingMode.Absolute));
        opcodes.add(new Opcode(0x7D, "ADC", 3, 4 /* +1 if page crossed */, AddressingMode.AbsoluteX));
        opcodes.add(new Opcode(0x79, "ADC", 3, 4 /* +1 if page crossed */, AddressingMode.AbsoluteY));
        opcodes.add(new Opcode(0x61, "ADC", 2, 6, AddressingMode.IndirectX));
        opcodes.add(new Opcode(0x71, "ADC", 2, 5 /* +1 if page crossed */, AddressingMode.IndirectY));

        opcodes.add(new Opcode(0x29, "AND", 2, 2, AddressingMode.Immediate));
        opcodes.add(new Opcode(0x25, "AND", 2, 3, AddressingMode.ZeroPage));
        opcodes.add(new Opcode(0x35, "AND", 2, 4, AddressingMode.ZeroPageX));
        opcodes.add(new Opcode(0x2D, "AND", 3, 4, AddressingMode.Absolute));
        opcodes.add(new Opcode(0x3D, "AND", 3, 4 /* +1 if page crossed */, AddressingMode.AbsoluteX));
        opcodes.add(new Opcode(0x39, "AND", 3, 4 /* +1 if page crossed */, AddressingMode.AbsoluteY));
        opcodes.add(new Opcode(0x21, "AND", 2, 6, AddressingMode.IndirectX));
        opcodes.add(new Opcode(0x31, "AND", 2, 5 /* +1 if page crossed */, AddressingMode.IndirectY));

        opcodes.add(new Opcode(0x0A, "ASL", 1, 2));
        opcodes.add(new Opcode(0x06, "ASL", 2, 5, AddressingMode.ZeroPage));
        opcodes.add(new Opcode(0x16, "ASL", 2, 6, AddressingMode.ZeroPageX));
        opcodes.add(new Opcode(0x0E, "ASL", 3, 6, AddressingMode.Absolute));
        opcodes.add(new Opcode(0x1E, "ASL", 3, 7, AddressingMode.AbsoluteX));

        opcodes.add(new Opcode(0x90, "BCC", 2, 2 /* +1 if branch succeds +2 if to a new page */));
        opcodes.add(new Opcode(0xB0, "BCS", 2, 2 /* +1 if branch succeds +2 if to a new page */));
        opcodes.add(new Opcode(0xF0, "BEQ", 2, 2 /* +1 if branch succeds +2 if to a new page */));

        opcodes.add(new Opcode(0xA9, "LDA", 2, 2, AddressingMode.Immediate));
        opcodes.add(new Opcode(0xA5, "LDA", 2, 3, AddressingMode.ZeroPage));
        opcodes.add(new Opcode(0xB5, "LDA", 2, 4, AddressingMode.ZeroPageX));
        opcodes.add(new Opcode(0xAD, "LDA", 3, 4, AddressingMode.Absolute));
        opcodes.add(new Opcode(0xBD, "LDA", 3, 4 /* +1 if page crossed */, AddressingMode.AbsoluteX));
        opcodes.add(new Opcode(0xB9, "LDA", 3, 4 /* +1 if page crossed */, AddressingMode.AbsoluteY));
        opcodes.add(new Opcode(0xA1, "LDA", 2, 6, AddressingMode.IndirectX));
        opcodes.add(new Opcode(0xB1, "LDA", 2, 5 /* +1 if page crossed */, AddressingMode.IndirectY));

        opcodes.add(new Opcode(0x85, "STA", 2, 3, AddressingMode.ZeroPage));
        opcodes.add(new Opcode(0x95, "STA", 2, 4, AddressingMode.ZeroPageX));
        opcodes.add(new Opcode(0x8D, "STA", 3, 4, AddressingMode.Absolute));
        opcodes.add(new Opcode(0x9D, "STA", 3, 5, AddressingMode.AbsoluteX));
        opcodes.add(new Opcode(0x99, "STA", 3, 5, AddressingMode.AbsoluteY));
        opcodes.add(new Opcode(0x81, "STA", 2, 6, AddressingMode.IndirectX));
        opcodes.add(new Opcode(0x91, "STA", 2, 6, AddressingMode.IndirectY));

        opcodes.add(new Opcode(0xAA, "TAX", 1, 2));
        opcodes.add(new Opcode(0xE8, "INX", 1, 2));
    }

    private int code;
    private String mnemonic;
    private int length;
    private int cycles;
    private AddressingMode mode;
    
    protected Opcode(int code, String mnemonic, int length, int cycles, AddressingMode mode) {
        this.code = code;
        this.mnemonic = mnemonic;
        this.length = length;
        this.cycles = cycles;
        this.mode = mode;
    }

    protected Opcode(int code, String mnemonic, int length, int cycles) {
        this.code = code;
        this.mnemonic = mnemonic;
        this.length = length;
        this.cycles = cycles;
        this.mode = AddressingMode.NoneAddressing;
    }

    public static HashMap<Short, Opcode> getOpcodesMap() {
        HashMap<Short, Opcode> map = new HashMap<>();
        for (Opcode opcode : opcodes) {
            map.put((short) opcode.code, opcode);
        }
        return map;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getCycles() {
        return cycles;
    }

    public void setCycles(int cycles) {
        this.cycles = cycles;
    }

    public AddressingMode getMode() {
        return mode;
    }

    public void setMode(AddressingMode mode) {
        this.mode = mode;
    }
}
