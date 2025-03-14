package com.monique.jes.ppu;

public class UnexpectedAccessException extends Exception {
    public UnexpectedAccessException(int addr) {
        super("Unexpected access to mirrored space " + addr);
    }
}
