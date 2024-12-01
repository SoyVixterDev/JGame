package JGame.Engine.Input;

import JGame.Application.Window;
import JGame.Engine.Internal.Logger;
import JGame.Engine.Structures.Vector2D;
import org.lwjgl.glfw.*;

/**
 * Class that handles user input including mouse and keyboard, utilizing GLFW's callbacks
 */
public class Input
{
    private static final GLFWKeyCallback keyboardCallback;
    private static final GLFWCursorPosCallback mousePositionCallback;
    private static final GLFWMouseButtonCallback mouseButtonCallback;
    private static final GLFWScrollCallback scrollCallback;

    private static Vector2D mousePos = Vector2D.Zero;
    private static Vector2D mouseScroll = Vector2D.Zero;

    private static final boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];
    private static final boolean[] keysDown = new boolean[GLFW.GLFW_KEY_LAST];
    private static final boolean[] keysUp = new boolean[GLFW.GLFW_KEY_LAST];

    private static final boolean[] mouseButtons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static final boolean[] mouseButtonsDown = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static final boolean[] mouseButtonsUp = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];

    //Static initializer for all the callbacks
    static
    {
        keyboardCallback = new GLFWKeyCallback()
        {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods)
            {
                keys[key] = (action != GLFW.GLFW_RELEASE);
                keysDown[key] = (action == GLFW.GLFW_PRESS);
                keysUp[key] = (action == GLFW.GLFW_RELEASE);
            }
        };

        mousePositionCallback = new GLFWCursorPosCallback()
        {
            @Override
            public void invoke(long window, double xpos, double ypos)
            {
                mousePos = new Vector2D((float) xpos, (float) ypos);
            }
        };

        mouseButtonCallback = new GLFWMouseButtonCallback()
        {
            @Override
            public void invoke(long window, int button, int action, int mods)
            {
                mouseButtons[button] = (action != GLFW.GLFW_RELEASE);
                mouseButtonsDown[button] = (action == GLFW.GLFW_PRESS);
                mouseButtonsUp[button] = (action == GLFW.GLFW_RELEASE);
            }
        };

        scrollCallback = new GLFWScrollCallback()
        {
            @Override
            public void invoke(long window, double xoffset, double yoffset)
            {
                mouseScroll = new Vector2D(Math.round(xoffset * 10)/10f, Math.round(yoffset * 10)/10f);
            }
        };
    }

    private static boolean initialized = false;

    /**
     * Called when starting the application, initializes the callbacks for the input
     */
    public static void Init()
    {
        if(initialized)
            return;

        try
        {
            initialized = true;

            GLFW.glfwSetMouseButtonCallback(Window.GetWindow(), mouseButtonCallback);
            GLFW.glfwSetCursorPosCallback(Window.GetWindow(), mousePositionCallback);
            GLFW.glfwSetKeyCallback(Window.GetWindow(), keyboardCallback);
            GLFW.glfwSetScrollCallback(Window.GetWindow(), scrollCallback);
        }
        catch(NullPointerException e)
        {
            Logger.DebugWarning("There's no window created! Create an instance of Window, Input and other functions require a window!");
        }

    }

    /**
     * Returns true if the selected key is being pressed
     * @param key
     * The key to check, using GLFW.KEY_...
     * @return
     * Returns true if the key is being pressed
     */
    public static boolean GetKeyHold(int key)
    {
        return keys[key];
    }
    /**
     * Returns true in the first frame where the key was pressed
     * @param key
     * The key to check, using GLFW.KEY_...
     * @return
     * Returns true if the key was pressed this frame
     */
    public static boolean GetKeyDown(int key)
    {
        boolean result = keysDown[key];
        if (result)
        {
            keysDown[key] = false;
        }
        return result;
    }

    /**
     * Returns true in the first frame where the key was released
     * @param key
     * The key to check, using GLFW.KEY_...
     * @return
     * Returns true if the key was released this frame
     */
    public static boolean GetKeyUp(int key)
    {
        boolean result = keysUp[key];
        if (result)
        {
            keysUp[key] = false;
        }
        return result;
    }

    /**
     * Returns true if the selected mouse button is being pressed
     * @param button
     * The button to check, using GLFW.MOUSE_BUTTON_...
     * @return
     * Returns true if the button is being pressed
     */
    public static boolean GetMouseButtonHold(int button)
    {
        return mouseButtons[button];
    }

    /**
     * Returns true in the first frame where the mouse button was pressed
     * @param button
     * The button to check, using GLFW.MOUSE_BUTTON_...
     * @return
     * Returns true if the button was pressed this frame
     */
    public static boolean GetMouseButtonDown(int button)
    {
        boolean result = mouseButtonsDown[button];
        if (result)
        {
            mouseButtonsDown[button] = false;
        }
        return result;
    }
    /**
     * Returns true in the first frame where the mouse button was released
     * @param button
     * The button to check, using GLFW.MOUSE_BUTTON_...
     * @return
     * Returns true if the button was released this frame
     */
    public static boolean GetMouseButtonUp(int button)
    {
        boolean result = mouseButtonsUp[button];
        if (result)
        {
            mouseButtonsUp[button] = false;
        }
        return result;
    }

    /**
     * Returns the mouse position in screenspace
     * @return
     * Vector containing the coordinates of the mouse's position
     */
    public static Vector2D GetMousePosition()
    {
        return mousePos;
    }

    /**
     * Returns the current mouse scroll offset
     * @return
     * The current mouse scroll offset
     */
    public static Vector2D GetMouseScroll()
    {
        return mouseScroll;
    }

    /**
     * Gets the keyboard callback
     * @return
     * The keyboard callback
     */
    public static GLFWKeyCallback GetKeyboardCallback()
    {
        return keyboardCallback;
    }
    /**
     * Gets the Mouse button callback
     * @return
     * The Mouse button callback
     */
    public static GLFWMouseButtonCallback GetMouseButtonCallback()
    {
        return mouseButtonCallback;
    }
    /**
     * Gets the Mouse position callback
     * @return
     * The Mouse position callback
     */
    public static GLFWCursorPosCallback GetMousePositionCallback()
    {
        return mousePositionCallback;
    }
    /**
     * Gets the Mouse Scroll callback
     * @return
     * The Mouse Scroll callback
     */
    public static GLFWScrollCallback GetMouseScrollCallback()
    {
        return scrollCallback;
    }

    /**
     * Frees up the callbacks used by the input system, called when the application is terminated
     */
    public static void Destroy()
    {
        keyboardCallback.free();
        mouseButtonCallback.free();
        mousePositionCallback.free();
        scrollCallback.free();
    }
}
