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

    public short[] bgPallete(PPU ppu, int tileColumn, int tileRow) {
        int attrTableIdx = tileRow / 4 * 8 + tileColumn / 4;
        short attrByte = unsignByte(ppu.memRead(0x3c0 + attrTableIdx));

        int palleteIdx;
        int valA = tileColumn % 4 / 2;
        int valB = tileRow % 4 / 2;
        if (valA == 0 && valB == 0) {
            palleteIdx = attrByte & 0b11;
        } else if (valA == 1 && valB == 0) {
            palleteIdx = (attrByte >> 2) & 0b11;
        } else if (valA == 0 && valB == 1) {
            palleteIdx = (attrByte >> 4) & 0b11;
        } else if (valA == 1 && valB == 1) {
            palleteIdx = (attrByte >> 6) & 0b11;
        } else {
            palleteIdx = 0;
            System.err.println("JESPanel.bgPallete()");
            System.err.println("Invalid Pallete Idx for Background");
            System.exit(-1);
        }
        
        int palleteStart = 1 + (palleteIdx) * 4;

        return new short[] {
            unsignByte(ppu.getPalleteTable()[0]),
            unsignByte(ppu.getPalleteTable()[palleteStart]),
            unsignByte(ppu.getPalleteTable()[palleteStart + 1]),
            unsignByte(ppu.getPalleteTable()[palleteStart + 2])
        };
    }

    public short/* u8 */[] spritePallete(PPU ppu, short /* u8 */ palleteIdx) {
        int start = 0x11 + (palleteIdx * 4);
        return new short[] {
            0,
            unsignByte(ppu.getPalleteTable()[start]),
            unsignByte(ppu.getPalleteTable()[start + 1]),
            unsignByte(ppu.getPalleteTable()[start + 2])
        };
    }

    public void render(PPU ppu, Frame frame) {
        int bank = ppu.getCtrl().getBitsFlag(ControlFlag.BACKGROUND_PATTERN_ADDR) ? 0x1000 : 0;
        System.out.println("SYSTEM_PALETTE[0x0F] = " + Pallete.SYSTEM_PALETTE[0x0F]);

        for (int i = 0; i < 0x03C0; i++) {
            int tileBase = ppu.memRead(i);
            int tileColumn = i % 32;
            int tileRow = i / 32;
            short[] tile = Arrays.copyOfRange(ppu.getChrRom(), bank + tileBase * 16, bank + tileBase * 16 + 16);
            short[] pallete = bgPallete(ppu, tileColumn, tileRow);

            for (int y = 0; y <= 7; y++) {
                short upper = unsignByte(tile[y]);
                short lower = unsignByte(tile[y + 8]);

                for (int x = 7; x >= 0; x--) {
                    int value = ((1 & upper) << 1 | (1 & lower));
                    upper = (short) (upper >> 1);
                    lower = (short) (lower >> 1);

                    Color rgb = switch (value) {
                        case 0 -> Pallete.SYSTEM_PALETTE[ppu.getPalleteTable()[0] & 0x3F];
                        case 1 -> Pallete.SYSTEM_PALETTE[pallete[1] & 0x3F];
                        case 2 -> Pallete.SYSTEM_PALETTE[pallete[2] & 0x3F];
                        case 3 -> Pallete.SYSTEM_PALETTE[pallete[3] & 0x3F];
                        default -> {
                            System.err.println("It can't be");  
                            yield null;
                        }
                    };

                    frame.setPixel(tileColumn*8 + x, tileRow*8 + y, rgb);
                }
            }
        }

        for (int i = ppu.getOamData().length - 4; i >= 0; i -= 4) {
            int tileIdx = unsignShort(ppu.getOamData()[i + 1]);
            int tileX = ppu.getOamData()[i + 3];
            int tileY = ppu.getOamData()[i];

            boolean flipVertical = (ppu.getOamData()[i + 2] >> 7 & 1) == 1;
            boolean flipHorizontal = (ppu.getOamData()[i + 2] >> 6 & 1) == 1;
            int palleteIdx = ppu.getOamData()[i + 2] & 0b11;
            short[] spritePallete = spritePallete(ppu, unsignByte(palleteIdx));

            bank = ppu.getCtrl().getBitsFlag(ControlFlag.SPRITE_PATTERN_ADDR) ? 0x1000 : 0;
        
            short[] tile = Arrays.copyOfRange(ppu.getChrRom(), bank + tileIdx * 16, bank + tileIdx * 16 + 16);
        
            for (int y = 0; y <= 7; y++) {
                short upper = unsignByte(tile[y]);
                short lower = unsignByte(tile[y + 8]);
                ololo: for (int x = 7; x >= 0; x--) {
                    int value = (1 & lower) << 1 | (1 & upper);
                    upper >>= 1;
                    lower >>= 1;

                    Color rgb; 
                    switch (value) {
                        case 0 -> {
                            continue ololo;
                        } case 1 -> {
                            rgb = Pallete.SYSTEM_PALETTE[spritePallete[1]];
                        } case 2 -> {
                            rgb = Pallete.SYSTEM_PALETTE[spritePallete[2]];
                        } case 3 -> {
                            rgb = Pallete.SYSTEM_PALETTE[spritePallete[3]];
                        } default -> {
                            System.err.println("It can't be");  
                            rgb = null;
                        }
                    };

                    if (flipHorizontal == false && flipVertical == false) {
                        frame.setPixel(tileX + x, tileY + y, rgb);
                    } else if (flipHorizontal == true && flipVertical == false) {
                        frame.setPixel(tileX + 7 - x, tileY + y, rgb);
                    } else if (flipHorizontal == false && flipVertical == true) {
                        frame.setPixel(tileX + x, tileY + 7 - y, rgb);
                    } else if (flipHorizontal == true && flipVertical == true) {
                        frame.setPixel(tileX + 7 - x, tileY + 7 - y, rgb);
                    }
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
