package com.monique.jes;

import com.monique.jes.cpu.CPU;
import com.monique.jes.utils.Rom;

public class JES {
    private JES() {
        try {
            var rom = getClass().getResourceAsStream("/snake.nes");

            var cpu = new CPU(new Bus(new Rom(rom)));
            cpu.reset();
    
            cpu.runWithCallback(c -> {
                try {
    
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    public static void main(String[] args) {
        new JES();
    }
}
