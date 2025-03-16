package com.monique.tiles;

import javax.swing.JFrame;

public class TileFrame extends JFrame {
    public TileFrame() {
        super("Tile test");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        add(new TilePanel());
        pack();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new TileFrame();
    }
}
