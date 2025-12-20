package com.github.gabrielemercolino.swinginput;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("preview")
public final class Input {
	private static final StableValue<Keyboard> keyboardListener = StableValue.of();
	private static final StableValue<Mouse> mouseListener = StableValue.of();

	public static Keyboard keyboardListener() {
		return keyboardListener.orElseSet(Keyboard::new);
	}

	public static Mouse mouseListener() {
		return mouseListener.orElseSet(Mouse::new);
	}

	public static void sync() {
		if (keyboardListener.isSet()) Keyboard.sync();
		if (mouseListener.isSet()) Mouse.sync();
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
		private static volatile float x, y;
		private static volatile boolean hasMoved;

		@Override
		public void mousePressed(MouseEvent e) {
			current.put(e.getButton(), true);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			current.put(e.getButton(), false);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			hasMoved = true;
			var position = e.getPoint();
			var owner = e.getComponent();
			x = (float) position.x / owner.getWidth();
			y = (float) position.y / owner.getHeight();
		}

		private static void sync() {
			previous.putAll(current);
			hasMoved = false;
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

		public static boolean hasMoved() {
			return hasMoved;
		}

		public static Position getPosition() {
			return new Position(x, y);
		}

		public record Position(float x, float y){}
	}
}
