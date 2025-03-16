package com.monique.jes.ppu;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Frame {
    private BufferedImage data;
    private final int WIDTH  = 256;
    private final int HEIGHT = 240;

    public Frame() {
        this.data = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    }
    
    public void setPixel(int x, int y, Color rgb) {
        data.setRGB(x, y, rgb.getRGB());
    }

    public BufferedImage getData() {
        return data;
    }

    public void setData(BufferedImage data) {
        this.data = data;
    }
}
