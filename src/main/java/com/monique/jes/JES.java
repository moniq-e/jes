package com.monique.jes;

import com.monique.jes.cpu.CPU;
import com.monique.jes.utils.Rom;

public class JES {
    private JES() {
        try {
            var rom = getClass().getResourceAsStream("/snake.nes");

            var bus = new Bus(new Rom(rom), b -> {
                
            });

            var cpu = new CPU(bus);
            cpu.reset();
            cpu.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    public static void main(String[] args) {
        new JES();
    }
}
