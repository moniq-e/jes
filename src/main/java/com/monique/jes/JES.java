package com.monique.jes;

import java.io.InputStream;

import javax.swing.JFrame;

import com.monique.jes.cpu.CPU;
import com.monique.jes.joypad.InputHandler;
import com.monique.jes.ppu.Frame;
import com.monique.jes.utils.Rom;

public class JES extends JFrame {
    JES() {
        super("JES");

        try {
            InputStream romFile = getClass().getResourceAsStream("/pacman.nes");

            Frame frame = new Frame();

            JESPanel panel = new JESPanel(frame);
            Bus bus = new Bus(new Rom(romFile), b -> {
                panel.render(b.getPPU(), frame);
            });
            
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setResizable(false);

            add(panel);
            pack();
            addKeyListener(new InputHandler(bus.getJoypad()));
            setFocusable(true);

            setLocationRelativeTo(null);
            setVisible(true);
            
            CPU cpu = new CPU(bus);
            cpu.reset();
            cpu.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    public static void main(String[] args) {
        new JES();
    }
}
