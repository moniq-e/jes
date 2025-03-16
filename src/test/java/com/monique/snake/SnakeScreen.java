package com.monique.snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.monique.jes.cpu.CPU;

public class SnakeScreen {
    private JFrame frame;
    private JPanel panel;
    private final int SCALE = 10;
    
    public SnakeScreen(CPU cpu) {
        frame = new JFrame("Snake");
        panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());

                var pixelIndex = 0x200;
                for (int y = 0; y < 32; y++) {
                    for (int x = 0; x < 32; x++) {

                        short colorIndex = cpu.memRead(pixelIndex++);
                        g.setColor(color(colorIndex));
                        g.fillRect(x * SCALE, y * SCALE, SCALE, SCALE);
                        
                        if (pixelIndex >= 0x600) return;
                    }
                }
            }
        };

        panel.setBackground(Color.BLACK);
        panel.setDoubleBuffered(true);
        panel.setLayout(null);
        panel.setOpaque(true);
        panel.setPreferredSize(new Dimension(32 * SCALE, 32 * SCALE));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.addKeyListener(new SnakeInput(cpu));
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public Color color(short u8) {
        return switch (u8) {
            case 0 -> {
                yield Color.BLACK;
            }
            case 1 -> {
                yield Color.WHITE;
            }
            case 2, 9 -> {
                yield Color.GRAY;
            }
            case 3, 10 -> {
                yield Color.RED;
            }
            case 4, 11 -> {
                yield Color.GREEN;
            }
            case 5, 12 -> {
                yield Color.BLUE;
            }
            case 6, 13 -> {
                yield Color.MAGENTA;
            }
            case 7, 14 -> {
                yield Color.YELLOW;
            }
            default -> {
                yield Color.CYAN;
            }
        };
    }

    public void update() {
        panel.revalidate();
        panel.repaint();
    }

    public void close() {
        frame.dispose();
    }
}
