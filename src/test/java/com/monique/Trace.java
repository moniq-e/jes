package com.monique;

import java.util.ArrayList;

import com.monique.jes.cpu.CPU;
import com.monique.jes.cpu.Opcode;

public interface Trace {
    public default String trace(CPU cpu) throws Exception {
        var opcodes = Opcode.getOpcodesMap();

        var code = cpu.memRead(cpu.getPC());
        var opcode = opcodes.get(code);

        var begin = cpu.getPC();
        var hexDump = new ArrayList<Short>();
        hexDump.add(code);

        Pair pair = switch (opcode.getMode()) {
            case Immediate, NoneAddressing -> new Pair(0, 0);
            default -> {
                var addr = cpu.getAbsoluteAddr(opcode.getMode(), begin + 1).getFirst();
                yield new Pair(addr, cpu.memRead(addr));
            }
        };

        String tmp = switch (opcode.getLength()) {
            case 1 -> {
                yield switch (opcode.getCode()) {
                    case 0x0A, 0x4A, 0x2A, 0x6A -> "A ";
                    default -> "";
                };
            }
            case 2 -> {
                var address = cpu.memRead(begin + 1);
                hexDump.add(address);

                yield switch (opcode.getMode()) {
                    case Immediate -> String.format("#$%02x", address);
                    case ZeroPage -> String.format("$%02x = %02x", pair.getAddr(), pair.getValue());
                    case ZeroPageX -> String.format( "$%02x,X @ %02x = %02x", address, pair.getAddr(), pair.getValue());
                    case ZeroPageY -> String.format("$%02x,Y @ %02x = %02x", address, pair.getAddr(), pair.getValue());
                    case IndirectX -> String.format("($%02x,X) @ %02x = %04x = %02x", address, (address + cpu.getIrx()), pair.getAddr(), pair.getValue());
                    case IndirectY -> String.format("($%02x),Y = %04x @ %04x = %02x", address, (pair.getAddr() - cpu.getIry()) & 0xFFFF, pair.getAddr(), pair.getValue());
                    case NoneAddressing -> {
                        // assuming local jumps: BNE, BVS, etc....
                        int address2 = begin + 2 + ((byte) address);
                        yield String.format("$%04x", address2);
                    }
                    default -> throw new Exception(String.format("Unexpected addressing mode %s has ops-len 2. Code %02x", opcode.getMode(), opcode.getCode()));
                };
            }
            case 3 -> {
                var addressLow = cpu.memRead(begin + 1);
                var addressHigh = cpu.memRead(begin + 2);
                hexDump.add(addressLow);
                hexDump.add(addressHigh);

                var address = cpu.memRead16(begin + 1);

                yield switch (opcode.getMode()) {
                    case NoneAddressing -> {
                        if (opcode.getCode() == 0x6C) {
                            //jmp indirect
                            int jmpAddr; 
                            if ((address & 0x00FF) == 0x00FF) {
                                var lo = cpu.memRead(address);
                                var hi = cpu.memRead(address & 0xFF00);
                                jmpAddr = (hi << 8 | lo);
                            } else {
                                jmpAddr = cpu.memRead16(address);
                            }

                            // var jmpAddr = cpu.mem_read_u16(address);
                            yield String.format("($%04x) = %04x", address, jmpAddr);
                        } else {
                            yield String.format("$%04x", address);
                        }
                    }
                    case Absolute -> String.format("$%04x = %02x", pair.getAddr(), pair.getValue());
                    case AbsoluteX -> String.format("$%04x,X @ %04x = %02x", address, pair.getAddr(), pair.getValue());
                    case AbsoluteY -> String.format("$%04x,Y @ %04x = %02x", address, pair.getAddr(), pair.getValue());
                    default -> throw new Exception(String.format("Unexpected addressing mode %s has ops-len 3. Code %02x", opcode.getMode(), opcode.getCode()));
                };
            }
            default -> "";
        };

        var hexArr = new ArrayList<String>();
        hexDump.forEach(h -> hexArr.add(String.format("%02x", h.shortValue())));
        var hexStr = String.join(" ", hexArr);

        var asmStr = String.format("%04x  %-8s %4s %s", begin, hexStr, opcode.getMnemonic(), tmp).trim();

        return String.format("%-47s A:%02x X:%02x Y:%02x P:%02x SP:%02x", asmStr, cpu.getAcc(), cpu.getIrx(), cpu.getIry(), cpu.getStatus(), cpu.getSP()).toUpperCase();
    }

    public class Pair {
        private int addr;
        private int value;

        public Pair(int addr, int value) {
            this.addr = addr;
            this.value = value;
        }

        public int getAddr() {
            return addr;
        }

        public int getValue() {
            return value;
        }
    }
}
