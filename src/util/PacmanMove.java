package util;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PacmanMove implements KeyListener {
    private volatile int desiredDx = 0;
    private volatile int desiredDy = 0;

    private volatile int lastSuccessfulDx = 0;
    private volatile int lastSuccessfulDy = 0;

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
            desiredDy = -1;
            desiredDx = 0;
            lastSuccessfulDy = -1;
            lastSuccessfulDx = 0;
        } else if (keyCode == KeyEvent.VK_DOWN) {
            desiredDy = 1;
            desiredDx = 0;
            lastSuccessfulDy = 1;
            lastSuccessfulDx = 0;
        } else if (keyCode == KeyEvent.VK_LEFT) {
            desiredDx = -1;
            desiredDy = 0;
            lastSuccessfulDx = -1;
            lastSuccessfulDy = 0;
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            desiredDx = 1;
            desiredDy = 0;
            lastSuccessfulDy = 1;
            lastSuccessfulDx = 0;
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

    public int getDesiredDx() {
        return desiredDx;
    }

    public int getDesiredDy() {
        return desiredDy;
    }

    public int getLastSuccessfulDx() {
        return lastSuccessfulDx;
    }

    public int getLastSuccessfulDy() {
        return lastSuccessfulDy;
    }

    public void setLastSuccessfulDx(int dx) {
        this.lastSuccessfulDx = dx;
    }

    public void setLastSuccessfulDy(int dy) {
        this.lastSuccessfulDy = dy;
    }

    public void resetMovementState() {
        desiredDx = 0;
        desiredDy = 0;
        lastSuccessfulDx = 0;
        lastSuccessfulDy = 0;
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
