package com.monique.jes.cpu;

import static com.monique.jes.utils.Unsign.*;

import java.io.InputStream;
import java.util.function.Consumer;

import com.monique.jes.Bus;
import com.monique.jes.utils.Memory;
import com.monique.jes.utils.Pair;
import com.monique.jes.utils.bitflag.BitFlag;
import com.monique.jes.utils.interrupt.Interrupt;

//21441960 Hz
public class CPU implements Memory {
    private final int DELTA_TIME = 70000;
    private Bus bus;
    private int pc; // 16 bit
    private short sp; // 8 bit
    private short acc; // 8 bit
    private BitFlag pstatus; // 8 bit
    private short irx; // 8 bit
    private short iry; // 8 bit

    public CPU(Bus bus) {
        //mem = new short[0xFFFF];
        this.bus = bus;
        pc = 0;
        sp = 0xFD;
        acc = 0;
        pstatus = new BitFlag((short) 0x24); // 0010 0100
        irx = 0;
        iry = 0;
    }

    public void reset() {
        pc = memRead16(0xFFFC);
        sp = 0xFD;
        acc = 0;
        pstatus.reset(); // 0010 0100
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
            memWrite(i + 0x8000, rom[i]);
        }
        memWrite16(0xFFFC, 0x8000);
    }

    public void loadRom(InputStream rom) {
        try {
            int data, i = 0;
            while ((data = rom.read()) != -1) {
                memWrite(i++ + 0x8000, (short) data);
            }
            memWrite16(0xFFFC, 0x8000);
            rom.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        runWithCallback(cpu -> {});
    }

    public void runWithCallback(Consumer<CPU> callback) throws IllegalArgumentException {
        var opcodes = Opcode.getOpcodesMap();
        var delta = DELTA_TIME;

        while (true) {
            if (delta == 0) {
                delta = DELTA_TIME;
            } else {
                delta--;
                continue;
            }

            if (bus.pollNMIStatus().isPresent()) {
                interrupt(Interrupt.NMI);
            }

            callback.accept(this);

            var code = memRead(pc);
            incPC();

            var pcState = pc;

            var opcode = opcodes.get(code);

            switch (code) {
                //BRK
                case 0x00 -> {
                    setStatusFlag(CPUFlag.BREAK, true);
                    System.out.println("Execution finished.");
                    return;
                }
                //ADC
                case 0x69, 0x65, 0x75, 0x6D, 0x7D, 0x79, 0x61, 0x71 -> {
                    adc(opcode.getMode());
                }
                //AND
                case 0x29, 0x25, 0x35, 0x2D, 0x3D, 0x39, 0x21, 0x31 -> {
                    and(opcode.getMode());
                }
                //ASL
                case 0x0A -> aslAccumulator();
                case 0x06, 0x16, 0x0E, 0x1E -> {
                    asl(opcode.getMode());
                }
                //BCC
                case 0x90 -> branch(!pstatus.getBitFlag(CPUFlag.CARRY));
                //BCS
                case 0xB0 -> branch(pstatus.getBitFlag(CPUFlag.CARRY));
                //BEQ
                case 0xF0 -> branch(pstatus.getBitFlag(CPUFlag.ZERO));
                //BIT
                case 0x24, 0x2C -> {
                    bit(opcode.getMode());
                }
                //BMI
                case 0x30 -> branch(pstatus.getBitFlag(CPUFlag.NEGATIVE));
                //BNE
                case 0xD0 -> branch(!pstatus.getBitFlag(CPUFlag.ZERO));
                //BPL
                case 0x10 -> branch(!pstatus.getBitFlag(CPUFlag.NEGATIVE));
                //BVC
                case 0x50 -> branch(!pstatus.getBitFlag(CPUFlag.OVERFLOW));
                //BVS
                case 0x70 -> branch(pstatus.getBitFlag(CPUFlag.OVERFLOW));
                //CLC
                case 0x18 -> setStatusFlag(CPUFlag.CARRY, false);
                //CLD
                case 0xD8 -> setStatusFlag(CPUFlag.DECIMAL, false);
                //CLI
                case 0x58 -> setStatusFlag(CPUFlag.INTERRUPT, false);
                //CLV
                case 0xB8 -> setStatusFlag(CPUFlag.OVERFLOW, false);
                //CMP
                case 0xC9, 0xC5, 0xD5, 0xCD, 0xDD, 0xD9, 0xC1, 0xD1 -> {
                    compare(opcode.getMode(), acc);
                }
                //CPX
                case 0xE0, 0xE4, 0xEC -> {
                    compare(opcode.getMode(), irx);
                }
                //CPY
                case 0xC0, 0xC4, 0xCC -> {
                    compare(opcode.getMode(), iry);
                }
                //DEC
                case 0xC6, 0xD6, 0xCE, 0xDE -> {
                    dec(opcode.getMode());
                }
                //DEX
                case 0xCA -> {
                    setIrx(irx - 1);
                    updateZNFlags(irx);
                }
                //DEY
                case 0x88 -> {
                    setIry(iry - 1);
                    updateZNFlags(iry);
                }
                //EOR
                case 0x49, 0x45, 0x55, 0x4D, 0x5D, 0x59, 0x41, 0x51 -> {
                    eor(opcode.getMode());
                }
                //INC
                case 0xE6, 0xF6, 0xEE, 0xFE -> {
                    inc(opcode.getMode());
                }
                //INX
                case 0xE8 -> {
                    setIrx(irx + 1);
                    updateZNFlags(irx);
                }
                //INY
                case 0xC8 -> {
                    setIry(iry + 1);
                    updateZNFlags(iry);
                }
                //JMP
                case 0x4C -> {
                    pc = memRead16(pc);
                }
                //JMP Indirect
                case 0x6C -> {
                    var addr = memRead16(pc);
                    
                    int indirectAddr;
                    if ((addr & 0x00FF) == 0x00FF) {
                        var lo = memRead(addr);
                        var hi = memRead(addr & 0xFF00);
                        indirectAddr = (hi << 8) | lo;
                    } else {
                        indirectAddr = memRead16(addr);
                    }

                    pc = indirectAddr;
                }
                //JSR
                case 0x20 -> {
                    stackPush16(pc + 1);
                    var addr = memRead16(pc);
                    pc = addr;
                }
                //LDA
                case 0xA9, 0xA5, 0xB5, 0xAD, 0xBD, 0xB9, 0xA1, 0xB1 -> {
                    lda(opcode.getMode());
                }
                //LDX
                case 0xA2, 0xA6, 0xB6, 0xAE, 0xBE -> {
                    ldx(opcode.getMode());
                }
                //LDY
                case 0xA0, 0xA4, 0xB4, 0xAC, 0xBC -> {
                    ldy(opcode.getMode());
                }
                //LSR Accumulator
                case 0x4A -> {
                    setStatusFlag(CPUFlag.CARRY, (acc & 0x1) != 0);
                    setAcc(acc >> 1);
                    updateZNFlags(acc);
                }
                //LSR
                case 0x46, 0x56, 0x4E, 0x5E -> {
                    lsr(opcode.getMode());
                }
                //NOP
                case 0xEA -> { }
                //ORA
                case 0x09, 0x05, 0x15, 0x0D, 0x1D, 0x19, 0x01, 0x11 -> {
                    var pair = getOperandAddr(opcode.getMode());
                    setAcc(acc | memRead(pair.getFirst()));
                    updateZNFlags(acc);

                    if (pair.getSecond()) {
                        bus.tick((short) 1);
                    }
                }
                //PHA
                case 0x48 -> stackPush(acc);
                //PHP
                case 0x08 -> {
                    stackPush((short) (pstatus.getBits() | 0x30));
                }
                //PLA
                case 0x68 -> {
                    setAcc(stackPop());
                    updateZNFlags(acc);
                }
                //PLP
                case 0x28 -> {
                    pstatus.setBits(stackPop());
                    setStatusFlag(CPUFlag.BREAK, false);
                    setStatusFlag(CPUFlag.BREAK2, true); 
                }
                //ROL Accumulator
                case 0x2A -> {
                    var carry = pstatus.getBitFlag(CPUFlag.CARRY);
                    setStatusFlag(CPUFlag.CARRY, (acc & 0x80) != 0);
                    setAcc((acc << 1) | (carry ? 1 : 0));
                    updateZNFlags(acc);
                }
                //ROL
                case 0x26, 0x36, 0x2E, 0x3E -> {
                    rol(opcode.getMode());
                }
                //ROR Accumulator
                case 0x6A -> {
                    var carry = pstatus.getBitFlag(CPUFlag.CARRY);
                    setStatusFlag(CPUFlag.CARRY, (acc & 0x1) != 0);
                    setAcc((acc >> 1) | (carry ? 0x80 : 0));
                    updateZNFlags(acc);
                }
                //ROR
                case 0x66, 0x76, 0x6E, 0x7E -> {
                    ror(opcode.getMode());
                }
                //RTI
                case 0x40 -> {
                    pstatus.setBits(stackPop());
                    setStatusFlag(CPUFlag.BREAK, false);
                    setStatusFlag(CPUFlag.BREAK2, true); 
                    pc = stackPop16();
                }
                //RTS
                case 0x60 -> {
                    pc = stackPop16();
                    incPC();
                }
                //SBC
                case 0xE9, 0xE5, 0xF5, 0xED, 0xFD, 0xF9, 0xE1, 0xF1 -> {
                    sbc(opcode.getMode());
                }
                //SEC
                case 0x38 -> setStatusFlag(CPUFlag.CARRY, true);
                //SED
                case 0xF8 -> setStatusFlag(CPUFlag.DECIMAL, true);
                //SEI
                case 0x78 -> setStatusFlag(CPUFlag.INTERRUPT, true);
                //STA
                case 0x85, 0x95, 0x8D, 0x9D, 0x99, 0x81, 0x91 -> {
                    sta(opcode.getMode());
                }
                //STX
                case 0x86, 0x96, 0x8E -> {
                    var addr = getOperandAddr(opcode.getMode()).getFirst();
                    memWrite(addr, irx);
                }
                //STY
                case 0x84, 0x94, 0x8C -> {
                    var addr = getOperandAddr(opcode.getMode()).getFirst();
                    memWrite(addr, iry);
                }
                //TAX
                case 0xAA -> {
                    setIrx(acc);
                    updateZNFlags(irx);
                }
                //TAY
                case 0xA8 -> {
                    setIry(acc);
                    updateZNFlags(iry);
                }
                //TSX
                case 0xBA -> {
                    setIrx(sp);
                    updateZNFlags(irx);
                }
                //TXA
                case 0x8A -> {
                    setAcc(irx);
                    updateZNFlags(acc);
                }
                //TXS
                case 0x9A -> setSp(irx);
                //TYA
                case 0x98 -> {
                    setAcc(iry);
                    updateZNFlags(acc);
                }
                default -> {
                    throw new IllegalArgumentException(String.format("Invalid opcode (%02x).", code));
                }
            }
            bus.tick(unsignByte(opcode.getCycles()));

            if (pcState == pc) {
                incPC(opcode.getLength() - 1);
            }
        }
    }

    public void interrupt(Interrupt interrupt) {
        stackPush16(pc);

        var flag = pstatus.clone();
        flag.setBitFlag(CPUFlag.BREAK, (interrupt.getBFlagMask() & 0x10) == 1);
        flag.setBitFlag(CPUFlag.BREAK2, (interrupt.getBFlagMask() & 0x20) == 1);

        stackPush(flag.getBits());
        setStatusFlag(CPUFlag.INTERRUPT, true);

        bus.tick(interrupt.getCpuCicles());
        setPC(memRead16(interrupt.getVectorAddr()));
    }

    public void adc(AddressingMode mode) {
        var pair = getOperandAddr(mode);
        var value = memRead(pair.getFirst());
        addToAcc(value);

        if (pair.getSecond()) {
            bus.tick((short) 1);
        }
    }

    public void and(AddressingMode mode) {
        var pair = getOperandAddr(mode);
        var value = memRead(pair.getFirst());
        setAcc(acc & value);
        updateZNFlags(acc);

        if (pair.getSecond()) {
            bus.tick((short) 1);
        }
    }

    public void aslAccumulator() {
        setStatusFlag(CPUFlag.CARRY, (acc & 0x80) != 0);
        setAcc(acc << 1);
        updateZNFlags(acc);
    }

    public void asl(AddressingMode mode) {
        var addr = getOperandAddr(mode).getFirst();
        var value = memRead(addr);
        setStatusFlag(CPUFlag.CARRY, (value & 0x80) != 0);
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

    public void bit(AddressingMode mode) {
        var addr = getOperandAddr(mode).getFirst();
        var value = memRead(addr);
        setStatusFlag(CPUFlag.ZERO, (acc & value) == 0);
        setStatusFlag(CPUFlag.NEGATIVE, (value & 0x80) != 0);
        setStatusFlag(CPUFlag.OVERFLOW, (value & 0x40) != 0);
    }

    public void compare(AddressingMode mode, short with) {
        var pair = getOperandAddr(mode);
        var value = memRead(pair.getFirst());
        setStatusFlag(CPUFlag.CARRY, value <= with);
        updateZNFlags((short) (with - value));

        if (pair.getSecond()) {
            bus.tick((short) 1);
        }
    }

    public void dec(AddressingMode mode) {
        var addr = getOperandAddr(mode).getFirst();
        var value = memRead(addr);
        memWrite(addr, --value);
        updateZNFlags(value);
    }

    public void eor(AddressingMode mode) {
        var pair = getOperandAddr(mode);
        var value = memRead(pair.getFirst());
        setAcc(acc ^ value);
        updateZNFlags(acc);

        if (pair.getSecond()) {
            bus.tick((short) 1);
        }
    }

    public void inc(AddressingMode mode) {
        var addr = getOperandAddr(mode).getFirst();
        var value = memRead(addr);
        memWrite(addr, ++value);
        updateZNFlags(value);
    }

    public void lda(AddressingMode mode) {
        var pair = getOperandAddr(mode);
        setAcc(memRead(pair.getFirst()));
        updateZNFlags(acc);

        if (pair.getSecond()) {
            bus.tick((short) 1);
        }
    }

    public void ldx(AddressingMode mode) {
        var pair = getOperandAddr(mode);
        setIrx(memRead(pair.getFirst()));
        updateZNFlags(irx);

        if (pair.getSecond()) {
            bus.tick((short) 1);
        }
    }

    public void ldy(AddressingMode mode) {
        var pair = getOperandAddr(mode);
        setIry(memRead(pair.getFirst()));
        updateZNFlags(iry);

        if (pair.getSecond()) {
            bus.tick((short) 1);
        }
    }

    public void lsr(AddressingMode mode) {
        var addr = getOperandAddr(mode).getFirst();
        var value = memRead(addr);
        setStatusFlag(CPUFlag.CARRY, (value & 0x1) != 0);
        value >>= 1;
        memWrite(addr, value);
        updateZNFlags(value);
    }

    public void rol(AddressingMode mode) {
        var addr = getOperandAddr(mode).getFirst();
        var value = memRead(addr);
        var carry = pstatus.getBitFlag(CPUFlag.CARRY);
        setStatusFlag(CPUFlag.CARRY, (value & 0x80) != 0);
        value = (short) ((value << 1) | (carry ? 1 : 0));
        memWrite(addr, value);
        updateZNFlags(value);
    }

    public void ror(AddressingMode mode) {
        var addr = getOperandAddr(mode).getFirst();
        var value = memRead(addr);
        var carry = pstatus.getBitFlag(CPUFlag.CARRY);
        setStatusFlag(CPUFlag.CARRY, (value & 0x1) != 0);
        value = (short) ((value >> 1) | (carry ? 0x80 : 0));
        memWrite(addr, value);
        updateZNFlags(value);
    }

    public void sbc(AddressingMode mode) {
        var pair = getOperandAddr(mode);
        byte value = (byte) memRead(pair.getFirst());
        addToAcc((short) (((~value) - 1) & 0xFF));

        if (pair.getSecond()) {
            bus.tick((short) 1);
        }
    }

    public void sta(AddressingMode mode) {
        var addr = getOperandAddr(mode).getFirst();
        memWrite(addr, acc);
    }

    public short stackPop() {
        sp = (short) ((sp + 1) & 0xFF);
        return memRead(0x100 + sp);
    }

    public int stackPop16() {
        var lo = stackPop();
        var hi = stackPop();
        return ((hi << 8) | lo);
    }

    public void stackPush(short value) {
        memWrite(0x100 + sp, value);
        sp = (short) ((sp - 1) & 0xFF);
    }

    public void stackPush16(int value) {
        stackPush((short) ((value >> 8) & 0xFF));
        stackPush((short) (value & 0xFF));
    }

    @Override
    public short memRead(int addr) {
        return bus.memRead(addr);
    }

    @Override
    public void memWrite(int addr, int value) {
        bus.memWrite(addr, value);
    }

    public Pair<Integer, Boolean> getOperandAddr(AddressingMode mode) {
        switch (mode) {
            case Immediate:
                return Pair.of(pc, false);
            default:
                return getAbsoluteAddr(mode, pc);
        }
    }

    public Pair<Integer, Boolean> getAbsoluteAddr(AddressingMode mode, int addr) {
        switch (mode) {
            case ZeroPage:
                return Pair.of((int) memRead(addr), false);
            case ZeroPageX:
                return Pair.of((memRead(addr) + irx) & 0xFFFF, false);
            case ZeroPageY:
                return Pair.of((memRead(addr) + iry) & 0xFFFF, false);
            case Absolute:
                return Pair.of(memRead16(addr), false);
            case AbsoluteX:
                var base = memRead16(addr);
                var res = (base + irx) & 0xFFFF;
                return Pair.of(res, pageCross(base, res));
            case AbsoluteY:
                base = memRead16(addr);
                res = (base + iry) & 0xFFFF;
                return Pair.of(res, pageCross(base, res));
            case IndirectX:
                return Pair.of(memRead16(memRead(addr) + irx), false);
            case IndirectY:
                base = memRead16(memRead(addr));
                res = (base + iry) & 0xFFFF;
                return Pair.of(res, pageCross(res, base));
            default:
                throw new IllegalArgumentException("Invalid addressing mode (" + mode + ")");
        }
    }

    public boolean pageCross(int addr1, int addr2) {
        return (addr1 & 0xFF00) != (addr2 & 0xFF00);
    }

    public void setStatusFlag(CPUFlag flag, boolean value) {
        pstatus.setBitFlag(flag, value);
    }

    public void updateZNFlags(short value) {
        setStatusFlag(CPUFlag.ZERO, value == 0);
        setStatusFlag(CPUFlag.NEGATIVE, (value & 0x80) != 0);
    }

    public void incPC() {
        pc = (pc + 1) & 0xFFFF;
    }

    public void incPC(int value) {
        pc = (pc + value) & 0xFFFF;
    }

    public void addToAcc(short value) {
        var result = acc + value + (pstatus.getBits() & 0x1);
        setStatusFlag(CPUFlag.CARRY, result > 0xFF);
        setStatusFlag(CPUFlag.OVERFLOW, ((result ^ value) & (acc ^ result) & 0x80) != 0);
        setAcc(result);
        updateZNFlags(acc);
    }

    public void setAcc(int value) {
        acc = unsignByte(value);
    }

    public void setIrx(int value) {
        irx = unsignByte(value);
    }

    public void setIry(int value) {
        iry = unsignByte(value);
    }

    public void setSp(int value) {
        sp = unsignByte(value);
    }

    // Test only
    public short getAcc() {
        return acc;
    }

    public short getStatus() {
        return pstatus.getBits();
    }

    public short getIrx() {
        return irx;
    }

    public short getIry() {
        return iry;
    }

    public int getPC() {
        return pc;
    }

    public void setPC(int value) {
        pc = unsignShort(value);
    }

    public short getSP() {
        return sp;
    }
}
