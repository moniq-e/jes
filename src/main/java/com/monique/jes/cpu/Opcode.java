package com.monique.jes.cpu;

import java.util.ArrayList;
import java.util.HashMap;

public class Opcode {
    private static final ArrayList<Opcode> opcodes = new ArrayList<>();
    private static final HashMap<Short, Opcode> opcodesMap = new HashMap<>();

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
        opcodes.add(new Opcode(0x24, "BIT", 2, 3, AddressingMode.ZeroPage));
        opcodes.add(new Opcode(0x2C, "BIT", 3, 4, AddressingMode.Absolute));
        opcodes.add(new Opcode(0x30, "BMI", 2, 2 /* +1 if branch succeds +2 if to a new page */));
        opcodes.add(new Opcode(0xD0, "BNE", 2, 2 /* +1 if branch succeds +2 if to a new page */));
        opcodes.add(new Opcode(0x10, "BPL", 2, 2 /* +1 if branch succeds +2 if to a new page */));
        opcodes.add(new Opcode(0x50, "BVC", 2, 2 /* +1 if branch succeds +2 if to a new page */));
        opcodes.add(new Opcode(0x70, "BVS", 2, 2 /* +1 if branch succeds +2 if to a new page */));

        opcodes.add(new Opcode(0x18, "CLC", 1, 2));
        opcodes.add(new Opcode(0xD8, "CLD", 1, 2));
        opcodes.add(new Opcode(0x58, "CLI", 1, 2));
        opcodes.add(new Opcode(0xB8, "CLV", 1, 2));

        opcodes.add(new Opcode(0xC9, "CMP", 2, 2, AddressingMode.Immediate));
        opcodes.add(new Opcode(0xC5, "CMP", 2, 3, AddressingMode.ZeroPage));
        opcodes.add(new Opcode(0xD5, "CMP", 2, 4, AddressingMode.ZeroPageX));
        opcodes.add(new Opcode(0xCD, "CMP", 3, 4, AddressingMode.Absolute));
        opcodes.add(new Opcode(0xDD, "CMP", 3, 4 /* +1 if page crossed */, AddressingMode.AbsoluteX));
        opcodes.add(new Opcode(0xD9, "CMP", 3, 4 /* +1 if page crossed */, AddressingMode.AbsoluteY));
        opcodes.add(new Opcode(0xC1, "CMP", 2, 6, AddressingMode.IndirectX));
        opcodes.add(new Opcode(0xD1, "CMP", 2, 5 /* +1 if page crossed */, AddressingMode.IndirectY));

        opcodes.add(new Opcode(0xE0, "CPX", 2, 2, AddressingMode.Immediate));
        opcodes.add(new Opcode(0xE4, "CPX", 2, 3, AddressingMode.ZeroPage));
        opcodes.add(new Opcode(0xEC, "CPX", 3, 4, AddressingMode.Absolute));

        opcodes.add(new Opcode(0xC0, "CPY", 2, 2, AddressingMode.Immediate));
        opcodes.add(new Opcode(0xC4, "CPY", 2, 3, AddressingMode.ZeroPage));
        opcodes.add(new Opcode(0xCC, "CPY", 3, 4, AddressingMode.Absolute));

        opcodes.add(new Opcode(0xC6, "DEC", 2, 5, AddressingMode.ZeroPage));
        opcodes.add(new Opcode(0xD6, "DEC", 2, 6, AddressingMode.ZeroPageX));
        opcodes.add(new Opcode(0xCE, "DEC", 3, 6, AddressingMode.Absolute));
        opcodes.add(new Opcode(0xDE, "DEC", 3, 7, AddressingMode.AbsoluteX));

        opcodes.add(new Opcode(0xCA, "DEX", 1, 2));
        opcodes.add(new Opcode(0x88, "DEY", 1, 2));

        opcodes.add(new Opcode(0x49, "EOR", 2, 2, AddressingMode.Immediate));
        opcodes.add(new Opcode(0x45, "EOR", 2, 3, AddressingMode.ZeroPage));
        opcodes.add(new Opcode(0x55, "EOR", 2, 4, AddressingMode.ZeroPageX));
        opcodes.add(new Opcode(0x4D, "EOR", 3, 4, AddressingMode.Absolute));
        opcodes.add(new Opcode(0x5D, "EOR", 3, 4 /* +1 if page crossed */, AddressingMode.AbsoluteX));
        opcodes.add(new Opcode(0x59, "EOR", 3, 4 /* +1 if page crossed */, AddressingMode.AbsoluteY));
        opcodes.add(new Opcode(0x41, "EOR", 2, 6, AddressingMode.IndirectX));
        opcodes.add(new Opcode(0x51, "EOR", 2, 5 /* +1 if page crossed */, AddressingMode.IndirectY));

        opcodes.add(new Opcode(0xE6, "INC", 2, 5, AddressingMode.ZeroPage));
        opcodes.add(new Opcode(0xF6, "INC", 2, 6, AddressingMode.ZeroPageX));
        opcodes.add(new Opcode(0xEE, "INC", 3, 6, AddressingMode.Absolute));
        opcodes.add(new Opcode(0xFE, "INC", 3, 7, AddressingMode.AbsoluteX));

        opcodes.add(new Opcode(0xE8, "INX", 1, 2));
        opcodes.add(new Opcode(0xC8, "INY", 1, 2));

        opcodes.add(new Opcode(0x4C, "JMP", 3, 3, AddressingMode.Absolute));
        opcodes.add(new Opcode(0x6C, "JMP", 3, 5)); // Indirect

        opcodes.add(new Opcode(0x20, "JSR", 3, 6));

        opcodes.add(new Opcode(0xA9, "LDA", 2, 2, AddressingMode.Immediate));
        opcodes.add(new Opcode(0xA5, "LDA", 2, 3, AddressingMode.ZeroPage));
        opcodes.add(new Opcode(0xB5, "LDA", 2, 4, AddressingMode.ZeroPageX));
        opcodes.add(new Opcode(0xAD, "LDA", 3, 4, AddressingMode.Absolute));
        opcodes.add(new Opcode(0xBD, "LDA", 3, 4 /* +1 if page crossed */, AddressingMode.AbsoluteX));
        opcodes.add(new Opcode(0xB9, "LDA", 3, 4 /* +1 if page crossed */, AddressingMode.AbsoluteY));
        opcodes.add(new Opcode(0xA1, "LDA", 2, 6, AddressingMode.IndirectX));
        opcodes.add(new Opcode(0xB1, "LDA", 2, 5 /* +1 if page crossed */, AddressingMode.IndirectY));

        opcodes.add(new Opcode(0xA2, "LDX", 2, 2, AddressingMode.Immediate));
        opcodes.add(new Opcode(0xA6, "LDX", 2, 3, AddressingMode.ZeroPage));
        opcodes.add(new Opcode(0xB6, "LDX", 2, 4, AddressingMode.ZeroPageY));
        opcodes.add(new Opcode(0xAE, "LDX", 3, 4, AddressingMode.Absolute));
        opcodes.add(new Opcode(0xBE, "LDX", 3, 4 /* +1 if page crossed */, AddressingMode.AbsoluteY));

        opcodes.add(new Opcode(0xA0, "LDY", 2, 2, AddressingMode.Immediate));
        opcodes.add(new Opcode(0xA4, "LDY", 2, 3, AddressingMode.ZeroPage));
        opcodes.add(new Opcode(0xB4, "LDY", 2, 4, AddressingMode.ZeroPageX));
        opcodes.add(new Opcode(0xAC, "LDY", 3, 4, AddressingMode.Absolute));
        opcodes.add(new Opcode(0xBC, "LDY", 3, 4 /* +1 if page crossed */, AddressingMode.AbsoluteX));

        opcodes.add(new Opcode(0x4A, "LSR", 1, 2));
        opcodes.add(new Opcode(0x46, "LSR", 2, 5, AddressingMode.ZeroPage));
        opcodes.add(new Opcode(0x56, "LSR", 2, 6, AddressingMode.ZeroPageX));
        opcodes.add(new Opcode(0x4E, "LSR", 3, 6, AddressingMode.Absolute));
        opcodes.add(new Opcode(0x5E, "LSR", 3, 7, AddressingMode.AbsoluteX));

        opcodes.add(new Opcode(0xEA, "NOP", 1, 2));

        opcodes.add(new Opcode(0x09, "ORA", 2, 2, AddressingMode.Immediate));
        opcodes.add(new Opcode(0x05, "ORA", 2, 3, AddressingMode.ZeroPage));
        opcodes.add(new Opcode(0x15, "ORA", 2, 4, AddressingMode.ZeroPageX));
        opcodes.add(new Opcode(0x0D, "ORA", 3, 4, AddressingMode.Absolute));
        opcodes.add(new Opcode(0x1D, "ORA", 3, 4 /* +1 if page crossed */, AddressingMode.AbsoluteX));
        opcodes.add(new Opcode(0x19, "ORA", 3, 4 /* +1 if page crossed */, AddressingMode.AbsoluteY));
        opcodes.add(new Opcode(0x01, "ORA", 2, 6, AddressingMode.IndirectX));
        opcodes.add(new Opcode(0x11, "ORA", 2, 5 /* +1 if page crossed */, AddressingMode.IndirectY));

        opcodes.add(new Opcode(0x48, "PHA", 1, 3));
        opcodes.add(new Opcode(0x08, "PHP", 1, 3));

        opcodes.add(new Opcode(0x68, "PLA", 1, 4));
        opcodes.add(new Opcode(0x28, "PLP", 1, 4));

        opcodes.add(new Opcode(0x2A, "ROL", 1, 2));
        opcodes.add(new Opcode(0x26, "ROL", 2, 5, AddressingMode.ZeroPage));
        opcodes.add(new Opcode(0x36, "ROL", 2, 6, AddressingMode.ZeroPageX));
        opcodes.add(new Opcode(0x2E, "ROL", 3, 6, AddressingMode.Absolute));
        opcodes.add(new Opcode(0x3E, "ROL", 3, 7, AddressingMode.AbsoluteX));

        opcodes.add(new Opcode(0x6A, "ROR", 1, 2));
        opcodes.add(new Opcode(0x66, "ROR", 2, 5, AddressingMode.ZeroPage));
        opcodes.add(new Opcode(0x76, "ROR", 2, 6, AddressingMode.ZeroPageX));
        opcodes.add(new Opcode(0x6E, "ROR", 3, 6, AddressingMode.Absolute));
        opcodes.add(new Opcode(0x7E, "ROR", 3, 7, AddressingMode.AbsoluteX));

        opcodes.add(new Opcode(0x40, "RTI", 1, 6));
        opcodes.add(new Opcode(0x60, "RTS", 1, 6));

        opcodes.add(new Opcode(0xE9, "SBC", 2, 2, AddressingMode.Immediate));
        opcodes.add(new Opcode(0xE5, "SBC", 2, 3, AddressingMode.ZeroPage));
        opcodes.add(new Opcode(0xF5, "SBC", 2, 4, AddressingMode.ZeroPageX));
        opcodes.add(new Opcode(0xED, "SBC", 3, 4, AddressingMode.Absolute));
        opcodes.add(new Opcode(0xFD, "SBC", 3, 4 /* +1 if page crossed */, AddressingMode.AbsoluteX));
        opcodes.add(new Opcode(0xF9, "SBC", 3, 4 /* +1 if page crossed */, AddressingMode.AbsoluteY));
        opcodes.add(new Opcode(0xE1, "SBC", 2, 6, AddressingMode.IndirectX));
        opcodes.add(new Opcode(0xF1, "SBC", 2, 5 /* +1 if page crossed */, AddressingMode.IndirectY));

        opcodes.add(new Opcode(0x38, "SEC", 1, 2));
        opcodes.add(new Opcode(0xF8, "SED", 1, 2));
        opcodes.add(new Opcode(0x78, "SEI", 1, 2));

        opcodes.add(new Opcode(0x85, "STA", 2, 3, AddressingMode.ZeroPage));
        opcodes.add(new Opcode(0x95, "STA", 2, 4, AddressingMode.ZeroPageX));
        opcodes.add(new Opcode(0x8D, "STA", 3, 4, AddressingMode.Absolute));
        opcodes.add(new Opcode(0x9D, "STA", 3, 5, AddressingMode.AbsoluteX));
        opcodes.add(new Opcode(0x99, "STA", 3, 5, AddressingMode.AbsoluteY));
        opcodes.add(new Opcode(0x81, "STA", 2, 6, AddressingMode.IndirectX));
        opcodes.add(new Opcode(0x91, "STA", 2, 6, AddressingMode.IndirectY));

        opcodes.add(new Opcode(0x86, "STX", 2, 3, AddressingMode.ZeroPage));
        opcodes.add(new Opcode(0x96, "STX", 2, 4, AddressingMode.ZeroPageY));
        opcodes.add(new Opcode(0x8E, "STX", 3, 4, AddressingMode.Absolute));

        opcodes.add(new Opcode(0x84, "STY", 2, 3, AddressingMode.ZeroPage));
        opcodes.add(new Opcode(0x94, "STY", 2, 4, AddressingMode.ZeroPageX));
        opcodes.add(new Opcode(0x8C, "STY", 3, 4, AddressingMode.Absolute));

        opcodes.add(new Opcode(0xAA, "TAX", 1, 2));
        opcodes.add(new Opcode(0xA8, "TAY", 1, 2));
        opcodes.add(new Opcode(0xBA, "TSX", 1, 2));
        opcodes.add(new Opcode(0x8A, "TXA", 1, 2));
        opcodes.add(new Opcode(0x9A, "TXS", 1, 2));
        opcodes.add(new Opcode(0x98, "TYA", 1, 2));

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
        if (opcodesMap.size() == 0) {
            for (Opcode opcode : opcodes) {
                opcodesMap.put((short) opcode.code, opcode);
            }
        }
        return opcodesMap;
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
