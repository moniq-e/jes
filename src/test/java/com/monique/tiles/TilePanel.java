package com.monique.tiles;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.JPanel;

import com.monique.jes.ppu.Frame;
import com.monique.jes.ppu.Pallete;
import com.monique.jes.utils.Rom;

public class TilePanel extends JPanel {

    private final int SCALE = 3;
    private final int SCREEN_WIDTH = 256;
    private final int SCREEN_HEIGHT = 240;
    private BufferedImage texture;

    public TilePanel() {
        Rom rom = null;
        try {
            rom = new Rom(getClass().getResourceAsStream("/pacman.nes"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        var rightBank = showTileBank(rom.chrRom, 1);
        texture = rightBank.getData();

        this.setPreferredSize(new Dimension(SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE));
        this.setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(this.texture, 0, 0, SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE, null);
    }

    public Frame showTileBank(short[]/* u8 */ chrRom, int bank) {
        assert bank <= 1;

        var frame = new Frame();
        int tileX = 0;
        int tileY = 0;
        bank = (bank * 0x1000);
        
        for (int tileN = 0; tileN < 255; tileN++) {
            if (tileN != 0 && tileN % 20 == 0) {
                tileY += 10;
                tileX = 0;
            }
            short[] tile = Arrays.copyOfRange(chrRom, (bank + tileN * 16), (bank + tileN * 16 + 15));

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

                    frame.setPixel(tileX + x, tileY + y, rgb);
                }
            }

            tileX += 10;
        }
        
        return frame;
    }

    public Frame showTile(short[]/* u8 */ chrRom, int bank, int tileN) {
        assert bank <= 1;

        var frame = new Frame();
        bank = (bank * 0x1000);
        
        short[] tile = Arrays.copyOfRange(chrRom, (bank + tileN * 16), (bank + tileN * 16 + 15));

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

                frame.setPixel(x, y, rgb);
            }
        }

        return frame;
    }
}