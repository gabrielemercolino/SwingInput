package com.github.gabrielemercolino.swinginput;

import com.github.gabrielemercolino.swinginput.Input.Keyboard;
import org.junit.jupiter.api.Test;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.event.KeyEvent;

class InputTest {
	@Test
	void keyboardInput() {
		JFrame frame = generateTestWindow(testName());
		frame.setVisible(true);

		while (frame.isVisible()) {
			if (Keyboard.wasJustPressed(KeyEvent.VK_SPACE)) System.out.println("Just pressed space");
			if (Keyboard.wasJustReleased(KeyEvent.VK_SPACE)) System.out.println("Just released space");
			Input.sync();
		}
	}

	private static String testName() {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		if (stack.length > 2) return stack[2].getMethodName();
		return "Unknown method";
	}

	private static JFrame generateTestWindow(final String testName) {
		JFrame frame = new JFrame(testName);
		frame.addKeyListener(Input.keyboardListener);
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(1280, 720));
		panel.setMinimumSize(new Dimension(1280, 720));
		frame.setContentPane(panel);
		frame.pack();
		return frame;
	}
}