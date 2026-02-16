# SwingInput

A lightweight input library for Swing applications, designed for game development.

## Features

- **Simple API**: Polling-based input system instead of event-based
- **Keyboard Support**: Check current state, just pressed, and just released
- **Mouse Support**: Buttons, position (normalized), and scroll wheel
- **Lightweight**: No external dependencies, minimal overhead
- **Java 21+**: Modern Java features

## Installation

### Gradle (Kotlin DSL)

```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // from the main branch
    implementation("com.github.gabrielemercolino:SwingInput:main-SNAPSHOT")
    // from a specific tag
    implementation("com.github.gabrielemercolino:SwingInput:v1.0.0")
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.gabrielemercolino</groupId>
        <artifactId>SwingInput</artifactId>
        <version>v1.0.0</version>
    </dependency>
</dependencies>
```

## Quick Start

```java
import com.github.gabrielemercolino.swinginput.Input;
import com.github.gabrielemercolino.swinginput.Input.Keyboard;
import com.github.gabrielemercolino.swinginput.Input.Mouse;

import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Game {
    public static void main(String[] args) {
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
}
```

## API Reference

### Input Class

| Method               | Description                                   |
|----------------------|-----------------------------------------------|
| `keyboardListener()` | Returns the singleton keyboard listener       |
| `mouseListener()`    | Returns the singleton mouse listener          |
| `sync()`             | Updates input states at the end of each frame |

### Keyboard Class

| Method                         | Description                            |
|--------------------------------|----------------------------------------|
| `isPressed(int keyCode)`       | Check if a key is currently held down  |
| `wasJustPressed(int keyCode)`  | Check if a key was pressed this frame  |
| `wasJustReleased(int keyCode)` | Check if a key was released this frame |

**Note:** Use `KeyEvent` constants (e.g., `KeyEvent.VK_SPACE`, `KeyEvent.VK_W`)

### Mouse Class

| Method                        | Description                                |
|-------------------------------|--------------------------------------------|
| `isPressed(int button)`       | Check if a button is currently held down   |
| `wasJustPressed(int button)`  | Check if a button was pressed this frame   |
| `wasJustReleased(int button)` | Check if a button was released this frame  |
| `hasMoved()`                  | Check if the mouse moved this frame        |
| `getPosition()`               | Get normalized mouse position (0.0 to 1.0) |
| `getScroll()`                 | Get accumulated scroll amount this frame   |

**Note:** Use `MouseEvent` constants (e.g., `MouseEvent.BUTTON1`, `MouseEvent.BUTTON3`)

### Position Record

The `Mouse.Position` record contains normalized coordinates:

| Field | Range      | Description                               |
|-------|------------|-------------------------------------------|
| `x()` | 0.0 to 1.0 | Horizontal position (0 = left, 1 = right) |
| `y()` | 0.0 to 1.0 | Vertical position (0 = top, 1 = bottom)   |

To convert to screen coordinates:
```java
Mouse.Position pos = Mouse.getPosition();
int screenX = (int) (pos.x() * component.getWidth());
int screenY = (int) (pos.y() * component.getHeight());
```

## Important Notes

1. **Always call `Input.sync()` at the end of your game loop** - This updates the internal state and enables detection of "just pressed" and "just released" events.

2. **Optional Listeners** - You only need to add the listeners you actually use:
   - `addKeyListener()` - only needed for keyboard input
   - `addMouseListener()` - only needed for mouse button clicks
   - `addMouseMotionListener()` - only needed for mouse movement tracking
   - `addMouseWheelListener()` - only needed for scroll wheel input

3. **Thread Safety** - The library is thread-safe and can be used from any thread, but input events are processed on the Event Dispatch Thread (EDT).

4. **Normalized Coordinates** - Mouse position is normalized (0.0 to 1.0) for easy scaling to different window sizes.

## License

This project is licensed under the terms of the MIT license.
