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
    private static Vector2D lastMousePos = Vector2D.Zero;
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
                if(key == -1) return;

                keys[key] = (action != GLFW.GLFW_RELEASE);

                if (action == GLFW.GLFW_PRESS)
                {
                    keysDown[key] = true;
                }
                else if (action == GLFW.GLFW_RELEASE)
                {
                    keysUp[key] = true;
                }
            }
        };

        mousePositionCallback = new GLFWCursorPosCallback()
        {
            @Override
            public void invoke(long window, double xpos, double ypos)
            {
                lastMousePos = mousePos;
                mousePos = new Vector2D((float) xpos, (float) ypos);
            }
        };

        mouseButtonCallback = new GLFWMouseButtonCallback()
        {
            @Override
            public void invoke(long window, int button, int action, int mods)
            {
                if(button == -1) return;
                mouseButtons[button] = (action != GLFW.GLFW_RELEASE);

                if (action == GLFW.GLFW_PRESS)
                {
                    mouseButtonsDown[button] = true;
                }
                else if (action == GLFW.GLFW_RELEASE)
                {
                    mouseButtonsUp[button] = true;
                }
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

            InitializeMousePosition();
        }
        catch(NullPointerException e)
        {
            Logger.DebugWarning("There's no window created! Create an instance of Window, Input and other functions require a window!");
        }

    }

    /**
     * Initializes mouse position
     */
    private static void InitializeMousePosition()
    {
        double[] xpos = new double[1];
        double[] ypos = new double[1];

        GLFW.glfwGetCursorPos(Window.GetWindow(), xpos, ypos);

        mousePos = new Vector2D((float) xpos[0], (float) ypos[0]);
        lastMousePos = mousePos;
    }

    /**
     * Sets the mouse cursor position to the center of the window
     */
    public static void CenterMouse()
    {
        Vector2D center = Window.GetWindowSize().Scale(1.0f/2.0f);
        SetMousePosition(center);
    }

    /**
     * Sets the mouse position in window space
     */
    public static void SetMousePosition(Vector2D position)
    {
        long window = Window.GetWindow();
        if (window != -1)
        {
            GLFW.glfwSetCursorPos(window, position.x, position.y);

            mousePos = position;
            lastMousePos = mousePos;
        }
    }


    /**
     * Called by the application to reset input states like key down or up or lastMousePos
     */
    public static void ResetInputStates()
    {
        for (int i = 0; i < mouseButtonsDown.length; i++)
        {
            mouseButtonsDown[i] = false;
            mouseButtonsUp[i] = false;
        }
        for (int i = 0; i < keysDown.length; i++)
        {
            keysDown[i] = false;
            keysUp[i] = false;
        }

        lastMousePos = mousePos;
        mouseScroll = Vector2D.Zero;
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
        return keysDown[key];
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
        return keysUp[key];
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
        return mouseButtonsDown[button];
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
        return mouseButtonsUp[button];
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
     * Returns the normalized mouse position in screenspace (0.0, 0.0) top left to (1.0, 1.0) bottom right
     * @return
     * Vector containing the coordinates of the mouse's position
     */
    public static Vector2D GetMouseNormalizedPosition()
    {
        return mousePos.Divide(Window.GetWindowSize());
    }


    /**
     * Gets the difference of the mouse's position between frames
     * @return
     * The difference of the mouse's position between frames
     */
    public static Vector2D GetMouseMovement()
    {

        return Vector2D.Subtract(mousePos, lastMousePos);
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


    public static void SetMouseVisibility(boolean visible)
    {
        if(Window.GetWindow() != -1)
            GLFW.glfwSetInputMode(Window.GetWindow(), GLFW.GLFW_CURSOR, visible ? GLFW.GLFW_CURSOR_NORMAL : GLFW.GLFW_CURSOR_HIDDEN);
    }
    public static void SetMouseLock(boolean locked)
    {
        if(Window.GetWindow() != -1)
            GLFW.glfwSetInputMode(Window.GetWindow(), GLFW.GLFW_CURSOR, locked ? GLFW.GLFW_CURSOR_DISABLED : GLFW.GLFW_CURSOR_NORMAL);
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
