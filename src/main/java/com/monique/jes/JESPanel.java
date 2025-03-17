package com.monique.jes;

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
    private BufferedImage texture;

    public JESPanel() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setLayout(null);
        texture = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
    }

    public void render(PPU ppu, Frame frame) {
        int bank = ppu.getCtrl().getBitsFlag(ControlFlag.BACKGROUND_PATTERN_ADDR) ? 1 : 0;
        
        for (int i = 0; i < 0x03C0; i++) {
            int tileBase = unsignShort(ppu.getVram()[i]);
            int tileX = i % 32;
            int tileY = i / 32;
            short[] tile = Arrays.copyOfRange(ppu.getChrRom(), (bank + tileBase * 16), (bank + tileBase * 16 + 15));

            for (int y = 0; y < 7; y++) {
                short upper = tile[y];
                short lower = tile[y + 8];

                for (int x = 7; x >= 0; x--) {
                    short value = (short) ((1 & upper) << 1 | (1 & lower));
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

                    frame.setPixel(tileX*8 + x, tileY*8 + y, rgb);
                }
            }
        }

        repaint();
        revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {

        System.out.println("executing paint");

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(this.texture, 0, 0, SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE, null);
    }
}
