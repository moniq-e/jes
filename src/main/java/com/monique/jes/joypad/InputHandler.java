package com.monique.jes.joypad;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class InputHandler extends KeyAdapter {
    private Joypad joypad;
    private HashMap<Integer, JoypadButton> keyMap = new HashMap<>();

    public InputHandler(Joypad joypad) {
        this.joypad = joypad;

        keyMap.put(KeyEvent.VK_UP, JoypadButton.UP);
        keyMap.put(KeyEvent.VK_LEFT, JoypadButton.LEFT);
        keyMap.put(KeyEvent.VK_DOWN, JoypadButton.DOWN);
        keyMap.put(KeyEvent.VK_RIGHT, JoypadButton.RIGHT);
        keyMap.put(KeyEvent.VK_SPACE, JoypadButton.SELECT);
        keyMap.put(KeyEvent.VK_ENTER, JoypadButton.START);
        keyMap.put(KeyEvent.VK_X, JoypadButton.A);
        keyMap.put(KeyEvent.VK_Z, JoypadButton.B);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        var button = keyMap.get(e.getKeyCode());

        if (button != null) joypad.setButtonPressed(button, true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        var button = keyMap.get(e.getKeyCode());

        if (button != null) joypad.setButtonPressed(button, false);
    }
}
