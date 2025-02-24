package com.monique.jes.cpu;

import java.util.ArrayList;
import java.util.HashMap;

public class Opcode {
    private static final ArrayList<Opcode> opcodes = new ArrayList<>();

    static {
        opcodes.add(new Opcode(0x00, "BRK", 1, 7, AddressingMode.NoneAddressing));

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

        opcodes.add(new Opcode(0xAA, "TAX", 1, 2, AddressingMode.NoneAddressing));
        opcodes.add(new Opcode(0xE8, "INX", 1, 2, AddressingMode.NoneAddressing));
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
