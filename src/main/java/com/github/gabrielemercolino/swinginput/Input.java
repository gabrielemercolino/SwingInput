package com.github.gabrielemercolino.swinginput;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class Input {
	@SuppressWarnings("preview")
	public static final Supplier<Keyboard> keyboardListener = StableValue.supplier(Keyboard::new);

	public static void sync() {
		Keyboard.sync();
	}

	public static final class Keyboard extends KeyAdapter {
		private static final Map<Integer, Boolean> previous = new ConcurrentHashMap<>();
		private static final Map<Integer, Boolean> current = new ConcurrentHashMap<>();

		@Override
		public void keyPressed(KeyEvent e) {
			current.put(e.getKeyCode(), true);
		}

		@Override
		public void keyReleased(KeyEvent e) {
			current.put(e.getKeyCode(), false);
		}

		private static void sync() {
			previous.putAll(current);
		}

		public static boolean isPressed(int keyCode) {
			return current.getOrDefault(keyCode, false);
		}

		public static boolean wasJustPressed(int keyCode) {
			return !previous.getOrDefault(keyCode, false) && isPressed(keyCode);
		}

		public static boolean wasJustReleased(int keyCode) {
			return previous.getOrDefault(keyCode, false) && !isPressed(keyCode);
		}
	}

	public static final class Mouse extends MouseAdapter {
		private static final Map<Integer, Boolean> previous = new ConcurrentHashMap<>();
		private static final Map<Integer, Boolean> current = new ConcurrentHashMap<>();

		@Override
		public void mousePressed(MouseEvent e) {
			current.put(e.getButton(), true);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			current.put(e.getButton(), false);
		}

		private static void sync() {
			previous.putAll(current);
		}

		public static boolean isPressed(int mouseKeyCode) {
			return current.getOrDefault(mouseKeyCode, false);
		}

		public static boolean wasJustPressed(int mouseKeyCode) {
			return !previous.getOrDefault(mouseKeyCode, false) && isPressed(mouseKeyCode);
		}

		public static boolean wasJustReleased(int mouseKeyCode) {
			return previous.getOrDefault(mouseKeyCode, false) && !isPressed(mouseKeyCode);
		}
	}
}
