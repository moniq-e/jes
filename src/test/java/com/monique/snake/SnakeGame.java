package com.monique.snake;

import com.monique.jes.Bus;
import com.monique.jes.cpu.CPU;
import com.monique.jes.utils.Rom;

public class SnakeGame {

    public static void main(String[] args) throws Exception {
        var rom = SnakeGame.class.getResourceAsStream("/snake.nes");
        var cpu = new CPU(new Bus(new Rom(rom)));
        cpu.reset();

        var screen = new SnakeScreen(cpu);

        cpu.runWithCallback(c -> {
            try {
                cpu.memWrite(0xFE, (int) Math.floor(Math.random() * 16) + 1);
                screen.update();
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
