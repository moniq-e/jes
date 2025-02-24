package com.monique.jes.cpu;

//21441960 Hz
public class CPU {
    private short[] mem; // 0xFFFF bytes
    private int pc; // 16 bit
    private short sp; // 8 bit
    private short acc; // 8 bit
    private short pstatus; // 8 bit
    private short irx; // 8 bit
    private short iry; // 8 bit

    public CPU() {
        mem = new short[0xFFFF];
        pc = 0;
        sp = 0;
        acc = 0;
        pstatus = 0;
        irx = 0;
        iry = 0;
    }

    public void reset() {
        pc = memRead16(0xFFFC);
        sp = 0;
        acc = 0;
        pstatus = 0;
        irx = 0;
        iry = 0;
    }

    public void loadAndRun(short[] rom) {
        loadRom(rom);
        reset();
        run();
    }

    public void loadRom(short[] rom) {
        for (int i = 0; i < rom.length; i++) {
            mem[i + 0x8000] = rom[i];
        }
        memWrite16(0xFFFC, 0x8000);
    }

    public void run() {
        var opcodes = Opcode.getOpcodesMap();

        while (true) {
            var code = memRead(pc);
            incPC();

            var pcState = pc;

            var opcode = opcodes.get(code);

            switch (code) {
                case 0x00: //BRK
                    return;
                case 0x69: //ADC
                case 0x65:
                case 0x75:
                case 0x6D:
                case 0x7D:
                case 0x79:
                case 0x61:
                case 0x71:
                    adc(opcode.getMode());
                    break;
                case 0x29: //AND
                case 0x25:
                case 0x35:
                case 0x2D:
                case 0x3D:
                case 0x39:
                case 0x21:
                case 0x31:
                    and(opcode.getMode());
                    break;
                case 0x0A: //ASL
                    asl_accumulator();
                    break;
                case 0x06:
                case 0x16:
                case 0x0E:
                case 0x1E:
                    asl(opcode.getMode());
                    break;
                case 0x90: //BCC
                    branch((pstatus & 0x1) == 0);
                    break;
                case 0xB0: //BCS
                    branch((pstatus & 0x1) != 0);
                    break;
                case 0xF0: //BEQ
                    branch((pstatus & 0x2) != 0);
                    break;
                case 0xA9: //LDA
                case 0xA5:
                case 0xB5:
                case 0xAD:
                case 0xBD:
                case 0xB9:
                case 0xA1:
                case 0xB1:
                    lda(opcode.getMode());
                    break;
                case 0x85: //STA
                case 0x95:
                case 0x8D:
                case 0x9D:
                case 0x99:
                case 0x81:
                case 0x91:
                    sta(opcode.getMode());
                    break;
                case 0xAA: //TAX
                    setIrx(acc);
                    updateZNFlags(irx);
                    break;
                case 0xE8: //INX
                    setIrx(irx + 1);
                    updateZNFlags(irx);
                    break;
                default:
                    break;
            }

            if (pcState == pc) {
                incPC(opcode.getLength() - 1);
            }
        }
    }

    public void adc(AddressingMode mode) {
        var addr = getOperandAddr(mode);
        var value = memRead(addr);
        var result = acc + value + (pstatus & 0x1);
        setStatusFlag(Flag.C, result > 0xFF);
        setStatusFlag(Flag.V, ((result ^ value) & (acc ^ result) & 0x80) != 0);
        setAcc(result);
        updateZNFlags(acc);
    }

    public void and(AddressingMode mode) {
        var addr = getOperandAddr(mode);
        var value = memRead(addr);
        setAcc(acc & value);
        updateZNFlags(acc);
    }

    public void asl_accumulator() {
        setStatusFlag(Flag.C, (acc & 0x80) != 0);
        setAcc(acc << 1);
        updateZNFlags(acc);
    }

    public void asl(AddressingMode mode) {
        var addr = getOperandAddr(mode);
        var value = memRead(addr);
        setStatusFlag(Flag.C, (value & 0x80) != 0);
        value <<= 1;
        memWrite(addr, value);
        updateZNFlags(value);
    }

    public void branch(boolean condition) {
        if (condition) {
            var jump = (byte) memRead(pc);
            incPC(1 + (jump & 0xFFFF));
        }
    }

    public void lda(AddressingMode mode) {
        var addr = getOperandAddr(mode);
        setAcc(memRead(addr));
        updateZNFlags(acc);
    }

    public void sta(AddressingMode mode) {
        var addr = getOperandAddr(mode);
        memWrite(addr, acc);
    }

    public short memRead(int addr) {
        return (short) (mem[addr] & 0xFF);
    }

    public int memRead16(int pos) {
        return (memRead(pos + 1) << 8) | memRead(pos);
    }

    public void memWrite(int addr, int value) {
        mem[addr] = (short) (value & 0xFF);
    }

    public void memWrite16(int pos, int value) {
        memWrite(pos, (short) (value & 0xFF));
        memWrite(pos + 1, (short) ((value >> 8) & 0xFF));
    }

    public int getOperandAddr(AddressingMode mode) {
        switch (mode) {
            case Immediate:
                return pc;
            case ZeroPage:
                return memRead(pc);
            case ZeroPageX:
                return (memRead(pc) + irx) & 0xFFFF;
            case ZeroPageY:
                return (memRead(pc) + iry) & 0xFFFF;
            case Absolute:
                return memRead16(pc);
            case AbsoluteX:
                return (memRead16(pc) + irx) & 0xFFFF;
            case AbsoluteY:
                return (memRead16(pc) + iry) & 0xFFFF;
            case IndirectX:
                return memRead16(memRead(pc) + irx);
            case IndirectY:
                return memRead16(memRead(pc)) + iry;
            case NoneAddressing:
            default:
                throw new IllegalArgumentException("Invalid addressing mode (" + mode + ")");
        }
    }

    public void setStatusFlag(Flag flag, boolean value) {
        switch (flag) {
            case N: // 1000 0000
                if (value) {
                    pstatus |= 0x80;
                } else {
                    pstatus &= 0x7F;
                }
                break;
            case V: // 0100 0000
                if (value) {
                    pstatus |= 0x40;
                } else {
                    pstatus &= 0xBF;
                }
                break;
            case B: // 0001 0000
                if (value) {
                    pstatus |= 0x10;
                } else {
                    pstatus &= 0xEF;
                }
                break;
            case D: // 0000 1000
                if (value) {
                    pstatus |= 0x8;
                } else {
                    pstatus &= 0xF7;
                }
                break;
            case I: // 0000 0100
                if (value) {
                    pstatus |= 0x4;
                } else {
                    pstatus &= 0xFB;
                }
                break;
            case Z: // 0000 0010
                if (value) {
                    pstatus |= 0x2;
                } else {
                    pstatus &= 0xFD;
                }
                break;
            case C: // 0000 0001
                if (value) {
                    pstatus |= 0x1;
                } else {
                    pstatus &= 0xFE;
                }
                break;
            default:
                break;
        }
    }

    public void updateZNFlags(short value) {
        setStatusFlag(Flag.Z, value == 0);
        setStatusFlag(Flag.N, (value & 0x80) != 0);
    }

    public short unsignByte(int value) {
        return (short) (value & 0xFF);
    }

    public void incPC() {
        pc = (pc + 1) & 0xFFFF;
    }

    public void incPC(int value) {
        pc = (pc + value) & 0xFFFF;
    }

    public void setAcc(int value) {
        acc = unsignByte(value);
    }

    public void setIrx(int value) {
        irx = unsignByte(value);
    }

    // Test only
    public short getAcc() {
        return acc;
    }

    public short getStatus() {
        return pstatus;
    }

    public short getIrx() {
        return irx;
    }
}
