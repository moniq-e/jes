package com.monique.snake;

import com.monique.jes.Bus;
import com.monique.jes.cpu.CPU;
import com.monique.jes.utils.Rom;

public class SnakeGame {

    public static void main(String[] args) throws Exception {
        var rom = SnakeGame.class.getResourceAsStream("/snake.nes");
        var cpu = new CPU(new Bus(Rom.of(rom)));
        cpu.reset();

        var screen = new SnakeScreen(cpu);

        cpu.runWithCallback(c -> {
            cpu.memWrite(0xFE, (int) Math.floor(Math.random() * 16) + 1);
            screen.update();
        });

        screen.close();
    }
}
