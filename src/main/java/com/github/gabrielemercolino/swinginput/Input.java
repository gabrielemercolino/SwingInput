package com.github.gabrielemercolino.swinginput;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for handling keyboard and mouse input in Swing applications.
 * <p>
 * This class provides a simplified API for game development by abstracting
 * Swing's event-based input system into a polling-based system.
 * <p>
 * <b>Important:</b> You must call {@link #sync()} at the end of each game loop
 * iteration to update the input states.
 *
 * <p>Example usage:
 * <pre>{@code
 * // In your game initialization:
 * frame.addKeyListener(Input.keyboardListener());
 * frame.addMouseListener(Input.mouseListener());
 * frame.addMouseMotionListener(Input.mouseListener());
 * frame.addMouseWheelListener(Input.mouseListener());
 *
 * // In your game loop:
 * while (running) {
 *     if (Keyboard.isPressed(KeyEvent.VK_W)) {
 *         player.moveUp();
 *     }
 *     if (Mouse.wasJustPressed(MouseEvent.BUTTON1)) {
 *         player.shoot();
 *     }
 *     Input.sync(); // Update input states
 * }
 * }</pre>
 *
 * @author Gabriele Mercolino
 * @version 1.0.0
 */
public final class Input {
	private static Keyboard keyboardListener;
	private static Mouse mouseListener;

	/**
	 * Returns the singleton keyboard listener instance.
	 * <p>
	 * This listener should be added to your JFrame or JPanel using
	 * {@code addKeyListener()}. Only add this listener if you need keyboard input;
	 * it is optional if you only use mouse input.
	 *
	 * @return the keyboard listener instance
	 */
	public static Keyboard keyboardListener() {
		if (keyboardListener != null) return keyboardListener;
		keyboardListener = new Keyboard();
		return keyboardListener;
	}

	/**
	 * Returns the singleton mouse listener instance.
	 * <p>
	 * This listener can be added to your JFrame or JPanel using the following methods:
	 * <ul>
	 *   <li>{@code addMouseListener()} - for mouse button clicks</li>
	 *   <li>{@code addMouseMotionListener()} - for mouse movement tracking</li>
	 *   <li>{@code addMouseWheelListener()} - for scroll wheel input</li>
	 * </ul>
	 * You only need to add the listeners for the functionality you actually use.
	 *
	 * @return the mouse listener instance
	 */
	public static Mouse mouseListener() {
		if (mouseListener != null) return mouseListener;
		mouseListener = new Mouse();
		return mouseListener;
	}

	/**
	 * Synchronizes the input states at the end of each game loop iteration.
	 * <p>
	 * This method must be called once per frame to update the internal state
	 * tracking, enabling detection of "just pressed" and "just released" events.
	 * <p>
	 * <b>Must be called after all input checks in your game loop.</b>
	 */
	public static void sync() {
		if (keyboardListener != null) Keyboard.sync();
		if (mouseListener != null) Mouse.sync();
	}

	/**
	 * Keyboard input handler extending {@link KeyAdapter}.
	 * <p>
	 * Provides methods to check key states: current press, just pressed, and just released.
	 * Use constants from {@link KeyEvent} for key codes (e.g., {@code KeyEvent.VK_SPACE}).
	 *
	 * <p>Example:
	 * <pre>{@code
	 * if (Keyboard.isPressed(KeyEvent.VK_W)) {
	 *     // Key W is currently held down
	 * }
	 * if (Keyboard.wasJustPressed(KeyEvent.VK_SPACE)) {
	 *     // Space was pressed this frame
	 * }
	 * }</pre>
	 */
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

		/**
		 * Checks if a key is currently being held down.
		 *
		 * @param keyCode the key code (use {@link KeyEvent} constants)
		 * @return true if the key is currently pressed
		 */
		public static boolean isPressed(int keyCode) {
			return current.getOrDefault(keyCode, false);
		}

		/**
		 * Checks if a key was pressed during this frame.
		 * <p>
		 * Returns true only for the first frame after the key is pressed.
		 *
		 * @param keyCode the key code (use {@link KeyEvent} constants)
		 * @return true if the key was just pressed this frame
		 */
		public static boolean wasJustPressed(int keyCode) {
			return !previous.getOrDefault(keyCode, false) && isPressed(keyCode);
		}

		/**
		 * Checks if a key was released during this frame.
		 * <p>
		 * Returns true only for the first frame after the key is released.
		 *
		 * @param keyCode the key code (use {@link KeyEvent} constants)
		 * @return true if the key was just released this frame
		 */
		public static boolean wasJustReleased(int keyCode) {
			return previous.getOrDefault(keyCode, false) && !isPressed(keyCode);
		}
	}

	/**
	 * Mouse input handler extending {@link MouseAdapter}.
	 * <p>
	 * Provides methods to check mouse button states, track position, and handle scroll.
	 * Mouse position is normalized (0.0 to 1.0) relative to the component.
	 * Use constants from {@link MouseEvent} for button codes.
	 *
	 * <p>Example:
	 * <pre>{@code
	 * if (Mouse.isPressed(MouseEvent.BUTTON1)) {
	 *     // Left mouse button is held down
	 * }
	 * Mouse.Position pos = Mouse.getPosition();
	 * // pos.x() and pos.y() are 0.0 to 1.0
	 * int scroll = Mouse.getScroll(); // Scroll amount in this frame
	 * }</pre>
	 */
	public static final class Mouse extends MouseAdapter {
		private static final Map<Integer, Boolean> previous = new ConcurrentHashMap<>();
		private static final Map<Integer, Boolean> current = new ConcurrentHashMap<>();
		private static volatile float x, y;
		private static volatile boolean hasMoved;
		// Using AtomicInteger because += operation on volatile int is not atomic
		// and can cause race conditions between EDT (event dispatch thread) and game loop
		private static final AtomicInteger scrollAmount = new AtomicInteger(0);

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

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			scrollAmount.addAndGet(e.getUnitsToScroll());
		}

		private static void sync() {
			previous.putAll(current);
			hasMoved = false;
			scrollAmount.set(0);
		}

		/**
		 * Checks if a mouse button is currently being held down.
		 *
		 * @param mouseKeyCode the button code (use {@link MouseEvent} constants)
		 * @return true if the button is currently pressed
		 */
		public static boolean isPressed(int mouseKeyCode) {
			return current.getOrDefault(mouseKeyCode, false);
		}

		/**
		 * Checks if a mouse button was pressed during this frame.
		 * <p>
		 * Returns true only for the first frame after the button is pressed.
		 *
		 * @param mouseKeyCode the button code (use {@link MouseEvent} constants)
		 * @return true if the button was just pressed this frame
		 */
		public static boolean wasJustPressed(int mouseKeyCode) {
			return !previous.getOrDefault(mouseKeyCode, false) && isPressed(mouseKeyCode);
		}

		/**
		 * Checks if a mouse button was released during this frame.
		 * <p>
		 * Returns true only for the first frame after the button is released.
		 *
		 * @param mouseKeyCode the button code (use {@link MouseEvent} constants)
		 * @return true if the button was just released this frame
		 */
		public static boolean wasJustReleased(int mouseKeyCode) {
			return previous.getOrDefault(mouseKeyCode, false) && !isPressed(mouseKeyCode);
		}

		/**
		 * Checks if the mouse has moved during this frame.
		 *
		 * @return true if the mouse position changed this frame
		 */
		public static boolean hasMoved() {
			return hasMoved;
		}

		/**
		 * Gets the accumulated scroll amount for this frame.
		 * <p>
		 * Positive values indicate scrolling down/backward,
		 * negative values indicate scrolling up/forward.
		 * <p>
		 * The value is reset to 0 after each call to {@link Input#sync()}.
		 *
		 * @return the scroll amount accumulated this frame
		 */
		public static int getScroll() {
			return scrollAmount.get();
		}

		/**
		 * Gets the current mouse position.
		 * <p>
		 * Coordinates are normalized between 0.0 and 1.0 relative to the component.
		 *
		 * @return the mouse position as a {@link Position} record
		 */
		public static Position getPosition() {
			return new Position(x, y);
		}

		/**
		 * Record representing normalized mouse position.
		 * <p>
		 * Both x and y are in the range [0.0, 1.0], where:
		 * <ul>
		 *   <li>(0.0, 0.0) is the top-left corner</li>
		 *   <li>(1.0, 1.0) is the bottom-right corner</li>
		 * </ul>
		 *
		 * @param x normalized x coordinate (0.0 to 1.0)
		 * @param y normalized y coordinate (0.0 to 1.0)
		 */
		public record Position(float x, float y){}
	}
}
