package com.github.gabrielemercolino.swinginput;

import com.github.gabrielemercolino.swinginput.Input.Keyboard;
import com.github.gabrielemercolino.swinginput.Input.Mouse;
import org.junit.jupiter.api.Test;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import static java.lang.System.out;

@SuppressWarnings("BusyWait")
class InputTest {
    @Test
    void keyboardInput() throws InterruptedException {
        JFrame frame = generateTestWindow(testName());
        frame.addKeyListener(Input.keyboardListener());
        frame.setVisible(true);

        Timer timer = new Timer(16, _e -> {
            if (Keyboard.wasJustPressed(KeyEvent.VK_SPACE)) out.println("Just pressed space");
            if (Keyboard.wasJustReleased(KeyEvent.VK_SPACE)) out.println("Just released space");
            Input.sync();
        });
        timer.start();

        while (frame.isVisible()) Thread.sleep(100);
    }

    @Test
    void mouseInput() throws InterruptedException {
        JFrame frame = generateTestWindow(testName());
        frame.addMouseListener(Input.mouseListener());
        frame.setVisible(true);

        Timer timer = new Timer(16, _e -> {
            if (Mouse.wasJustPressed(MouseEvent.BUTTON1)) out.println("Just pressed left mouse");
            if (Mouse.wasJustReleased(MouseEvent.BUTTON1)) out.println("Just released left mouse");
            Input.sync();
        });
        timer.start();

        while (frame.isVisible()) Thread.sleep(100);
    }

    @Test
    void mouseMotion() throws InterruptedException {
        JFrame frame = generateTestWindow(testName());
        frame.addMouseMotionListener(Input.mouseListener());
        frame.setVisible(true);

        Timer timer = new Timer(16, _e -> {
            if (Mouse.hasMoved()) out.println(Mouse.getPosition());
            Input.sync();
        });
        timer.start();

        while (frame.isVisible()) Thread.sleep(100);
    }

    @Test
    void mouseWheel() throws InterruptedException {
        JFrame frame = generateTestWindow(testName());
        frame.addMouseWheelListener(Input.mouseListener());
        frame.setVisible(true);

        Timer timer = new Timer(16, _e -> {
            int scroll = Mouse.getScroll();
            if (scroll != 0) out.println("Scroll: " + scroll);
            Input.sync();
        });
        timer.start();

        while (frame.isVisible()) Thread.sleep(100);
    }

    @Test
    void readmeExample() throws InterruptedException {
        JFrame frame = new JFrame("Game");

        // Add input listeners
        frame.addKeyListener(Input.keyboardListener());
        frame.addMouseListener(Input.mouseListener());
        frame.addMouseMotionListener(Input.mouseListener());
        frame.addMouseWheelListener(Input.mouseListener());

        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Game loop using Swing Timer (~60 FPS)
        Timer timer = new Timer(16, e -> {
            // Check keyboard input
            if (Keyboard.isPressed(KeyEvent.VK_ESCAPE)) {
                ((Timer) e.getSource()).stop();
                frame.dispose();
                return;
            }

            // Check mouse input
            if (Mouse.wasJustPressed(MouseEvent.BUTTON1)) {
                System.out.println("Left click at: " + Mouse.getPosition());
            }

            // IMPORTANT: Sync input states at the end of each frame
            Input.sync();
        });
        timer.start();

		// Don't let the main loop close, consider something better than this
		while (frame.isVisible()) Thread.sleep(100);
    }

    private static String testName() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        if (stack.length > 2) return stack[2].getMethodName();
        return "Unknown method";
    }

    private static JFrame generateTestWindow(final String testName) {
        JFrame frame = new JFrame(testName);
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(1280, 720));
        panel.setMinimumSize(new Dimension(1280, 720));
        frame.setContentPane(panel);
        frame.pack();
        return frame;
    }
}