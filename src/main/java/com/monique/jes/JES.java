package com.monique.jes;

import com.monique.jes.cpu.CPU;

public class JES {
    private JES() {
        var rom = getClass().getResourceAsStream("/snake.nes");

        var cpu = new CPU();
        cpu.loadRom(rom);
        cpu.reset();

        cpu.runWithCallback(c -> {
            try {

                Thread.sleep(70);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
    
    public static void main(String[] args) {
        new JES();
    }
}
