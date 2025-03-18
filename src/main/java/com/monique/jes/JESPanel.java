package com.monique.jes;

import static com.monique.jes.utils.Unsign.unsignByte;
import static com.monique.jes.utils.Unsign.unsignShort;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.JPanel;

import com.monique.jes.ppu.Frame;
import com.monique.jes.ppu.PPU;
import com.monique.jes.ppu.Pallete;
import com.monique.jes.ppu.registers.ControlFlag;

public class JESPanel extends JPanel {
    private final int SCALE = 3;
    private final int SCREEN_WIDTH = 256;
    private final int SCREEN_HEIGHT = 240;
    private Frame texture;

    public JESPanel(Frame texture) {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setLayout(null);
        this.texture = texture;
    }

    public void render(PPU ppu, Frame frame) {
        int bank = ppu.getCtrl().getBitsFlag(ControlFlag.BACKGROUND_PATTERN_ADDR) ? 1 : 0;
        
        for (int i = 0; i < 0x03C0; i++) {
            int tileBase = ppu.memRead(i);
            int tileColumn = i % 32;
            int tileRow = i / 32;
            short[] tile = Arrays.copyOfRange(ppu.getChrRom(), bank + tileBase * 16, bank + tileBase * 16 + 16);

            for (int y = 0; y <= 7; y++) {
                short upper = unsignByte(tile[y]);
                short lower = unsignByte(tile[y + 8]);

                for (int x = 7; x >= 0; x--) {
                    int value = ((1 & upper) << 1 | (1 & lower));
                    upper = (short) (upper >> 1);
                    lower = (short) (lower >> 1);

                    Color rgb = switch (value) {
                        case 0 -> {
                            yield Pallete.SYSTEM_PALETTE[0x01];
                        } case 1 -> {
                            yield Pallete.SYSTEM_PALETTE[0x23];
                        } case 2 -> {
                            yield Pallete.SYSTEM_PALETTE[0x27];
                        } case 3 -> {
                            yield Pallete.SYSTEM_PALETTE[0x30];
                        } default -> {
                            System.err.println("It can't be");  
                            yield null;
                        }
                    };

                    frame.setPixel(tileColumn*8 + x, tileRow*8 + y, rgb);
                }
            }
        }

        repaint();
        revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(this.texture.getData(), 0, 0, SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE, null);
    }
}
