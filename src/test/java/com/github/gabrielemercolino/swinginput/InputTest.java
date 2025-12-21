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

@SuppressWarnings("BusyWait")
class InputTest {
	@Test
	void keyboardInput() throws InterruptedException {
		JFrame frame = generateTestWindow(testName());
		frame.addKeyListener(Input.keyboardListener());
		frame.setVisible(true);

		Timer timer = new Timer(16, _e -> {
			if (Keyboard.wasJustPressed(KeyEvent.VK_SPACE)) IO.println("Just pressed space");
			if (Keyboard.wasJustReleased(KeyEvent.VK_SPACE)) IO.println("Just released space");
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
			if (Mouse.wasJustPressed(MouseEvent.BUTTON1)) IO.println("Just pressed left mouse");
			if (Mouse.wasJustReleased(MouseEvent.BUTTON1)) IO.println("Just released left mouse");
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
			if (Mouse.hasMoved()) IO.println(Mouse.getPosition());
			Input.sync();
		});
		timer.start();

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