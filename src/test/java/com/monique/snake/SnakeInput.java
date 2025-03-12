package com.monique.snake;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import com.monique.jes.cpu.CPU;

public class SnakeInput extends KeyAdapter {
    private CPU cpu;

    public SnakeInput(CPU cpu) {
        this.cpu = cpu;
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                cpu.memWrite(0xFF, 0x77);
                break;
            case KeyEvent.VK_S:
                cpu.memWrite(0xFF, 0x73);
                break;
            case KeyEvent.VK_A:
                cpu.memWrite(0xFF, 0x61);
                break;
            case KeyEvent.VK_D:
                cpu.memWrite(0xFF, 0x64);
                break;
            default:
                break;
        }
    }
}
