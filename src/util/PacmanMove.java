package util;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PacmanMove implements KeyListener {
    private volatile int x = 0;
    private volatile int y = 0;
    private volatile int xx = 0;
    private volatile int yy = 0;
    private volatile boolean controlPressed = false;
    private volatile boolean shiftPressed = false;
    private volatile boolean qPressed = false;

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_UP) {
            y = -1;
            x = 0;
            yy = -1;
            xx = 0;
        } else if (keyCode == KeyEvent.VK_DOWN) {
            y = 1;
            x = 0;
            yy = 1;
            xx = 0;
        } else if (keyCode == KeyEvent.VK_LEFT) {
            x = -1;
            y = 0;
            yy = -1;
            xx = 0;
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            x = 1;
            y = 0;
            yy = 1;
            xx = 0;
        } else if (keyCode == KeyEvent.VK_CONTROL) {
            controlPressed = true;
        } else if (keyCode == KeyEvent.VK_SHIFT) {
            shiftPressed = true;
        } else if (keyCode == KeyEvent.VK_Q) {
            qPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_CONTROL) {
            controlPressed = false;
        } else if (keyCode == KeyEvent.VK_SHIFT) {
            shiftPressed = false;
        } else if (keyCode == KeyEvent.VK_Q) {
            qPressed = false;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getXX() {
        return xx;
    }

    public int getYY() {
        return yy;
    }

    public void resetDesiredMovement() {
        x = 0;
        y = 0;
    }

    public void resetLastMovement() {
        xx = 0;
        yy = 0;
    }

    public boolean isStopGameShortcutActive() {
        return controlPressed && shiftPressed && qPressed;
    }

    public void resetStopGameShortcut() {
        controlPressed = false;
        shiftPressed = false;
        qPressed = false;
    }
}
