package JGame.Application;

import JGame.Engine.Internal.Logger;
import JGame.Engine.Internal.Time;
import JGame.Engine.Graphics.Misc.Camera;
import JGame.Engine.Structures.ColorRGBA;
import JGame.Engine.Structures.Vector2D;
import JGame.Engine.Input.Input;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL46.*;

/**
 * The window utility class, it allows you to create and configure the game window
 */
public class Window
{
    public static final int WINDOW_RESIZABLE = 0;
    public static final int WINDOW_SHOW_TITLE = 1;
    public static final int WINDOW_FULLSCREEN = 2;
    public static final int WINDOW_VSYNC = 3;

    static String title;
    static ColorRGBA backgroundColor = ColorRGBA.Black;
    static long window;

    private static Vector2D currentWindowSize = new Vector2D(1280, 720);
    private static Vector2D defaultWindowSize = new Vector2D(1280, 720);

    private static boolean isFullscreen = false;

    private static final GLFWWindowSizeCallback windowSizeCallback = new GLFWWindowSizeCallback()
    {
        @Override
        public void invoke(long window, int width, int height)
        {
            currentWindowSize = new Vector2D(width, height);

            if(!isFullscreen)
            {
                defaultWindowSize = new Vector2D(width, height);
            }

            try
            {
                GL.getCapabilities();
            }
            catch (RuntimeException e)
            {
                return;
            }

            ResetViewport();
            Camera.CalculateProjectionMatrix();
        }
    };


    /**
     * Creates and Initializes the window for the game, in borderless fullscreen mode, call this function to have access to graphic output
     * @param title
     * The window title
     */
    public static void CreateWindow(String title)
    {
        CreateWindow
        (
                -1,
                -1,
                true,
                title
        );
    }

    /**
     * Creates and Initializes the window for the game, call this function to have access to graphic output
     * @param width
     * Width of the screen in pixels
     * @param height
     * Height of the screen in pixels
     * @param fullscreen
     * Fullscreen state
     * @param title
     * Window Title
     */
    public static void CreateWindow(int width, int height, boolean fullscreen, String title)
    {
        if(!GLFW.glfwInit())
        {
            Logger.DebugError("GLFW not initialized!");
            window = 0;
            return;
        }


        Window.title = title;

        InitWindow(width, height, fullscreen);

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwShowWindow(window);

        InitGL();
    }

    /**
     * Initializes GL and creates a viewport
     */
    private static void InitGL()
    {
        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);

        glViewport(0,0, (int)currentWindowSize.x, (int)currentWindowSize.y);
    }

    /**
     * Initializes the callbacks used by the window
     */
    private static void InitCallbacks()
    {
        GLFW.glfwSetWindowSizeCallback(window, windowSizeCallback);
    }

    /**
     * Terminates the callbacks used by the window
     */
    private static void TerminateCallbacks()
    {
        windowSizeCallback.free();
    }

    /**
     * Initializes the window and GLFW
     * @param width
     * Width for the window
     * @param height
     * Height for the window
     * @param fullscreen
     * Fullscreen state
     */
    private static void InitWindow(int width, int height, boolean fullscreen)
    {
        //Selects the OpenGL Version to be 4.6
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 6);

        //Apparently ensures we don't use deprecated functions
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);

        //Disables V-Sync by Default
        GLFW.glfwSwapInterval(0);

        window = GLFW.glfwCreateWindow(1, 1, title,0, 0);

        InitCallbacks();

        SetWindowProperty(WINDOW_FULLSCREEN, fullscreen);
        SetWindowSize(width, height);
        CenterWindow();
    }

    public static void ResetViewport()
    {
        glViewport(0,0, (int)currentWindowSize.x, (int)currentWindowSize.y);
    }

    /**
     * Updates the window's variables including the background color and other behaviors,
     * should only be called by the internal application
     */
    public static void Update()
    {
        if(window == 0)
            return;

        glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        UpdateFramerateDisplay();

        if(Input.GetKeyDown(GLFW.GLFW_KEY_F11))
        {
            Window.SetWindowProperty(Window.WINDOW_FULLSCREEN, !isFullscreen);
        }

        if(GLFW.glfwWindowShouldClose(window))
            Application.Quit();
    }

    /**
     * Terminates all the callbacks, destroys the window and terminates GLFW
     */
    public static void Terminate()
    {
        TerminateCallbacks();
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    /**
     * Sets the window size, if either width or height is zero then the default is the screen size
     * @param width
     * New window width
     * @param height
     * New height width
     */
    public static void SetWindowSize(int width, int height)
    {
        if(width < 0 || height < 0)
        {
            GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            if(vidMode != null)
            {
                width = vidMode.width();
                height = vidMode.height();
            }
            else
            {
                Logger.DebugError("Monitor not found, Cannot set window size!");
            }
        }

        GLFW.glfwSetWindowSize(window, width, height);
    }

    /**
     * Returns the window size as Vector2D
     * @return
     * The window size as a Vector2D
     */
    public static Vector2D GetWindowSize()
    {
        return currentWindowSize;
    }

    /**
     * Returns the aspect ratio of the window
     * @return
     * The aspect ratio of the window
     */
    public static float GetWindowAspectRatio()
    {
        return currentWindowSize.x/currentWindowSize.y;
    }

    /**
     * Sets the position for the window in the screen
     * @param position
     * Position in screenspace, measured in pixels
     */
    public static void SetWindowPosition(Vector2D position)
    {
        GLFW.glfwSetWindowPos(window, (int)position.x, (int)position.y);
    }

    /**
     * Sets a window's property, check the constants in Window for the different properties that can be changed
     * @param property
     * The ID of the property to change
     * @param value
     * The new value for the property
     */
    public static void SetWindowProperty(int property, boolean value)
    {
        switch(property)
        {
            case WINDOW_RESIZABLE -> GLFW.glfwSetWindowAttrib(window, GLFW.GLFW_RESIZABLE, value ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
            case WINDOW_SHOW_TITLE -> GLFW.glfwSetWindowAttrib(window, GLFW.GLFW_DECORATED, value ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
            case WINDOW_FULLSCREEN -> SetFullscreen(value);
            case WINDOW_VSYNC -> GLFW.glfwSwapInterval(value ? 1 : 0);
        }

    }

    /**
     * Sets the background for the Viewport
     * @param backgroundColor
     * The new color for the background
     */
    public static void SetViewportBackground(ColorRGBA backgroundColor)
    {
        Window.backgroundColor = backgroundColor;
    }

    /**
     * Sets the window as borderless fullscreen, including setting the window as not resizable, hiding the title
     * @param value
     * The fullscreen state, either true or false
     */
    private static void SetFullscreen(boolean value)
    {
        SetWindowProperty(WINDOW_RESIZABLE, !value);
        SetWindowProperty(WINDOW_SHOW_TITLE, !value);

        isFullscreen = value;

        if(value)
        {
            SetWindowSize(-1, -1);
            SetWindowPosition(Vector2D.Zero);
        }
        else
        {
            SetWindowSize((int)defaultWindowSize.x, (int)defaultWindowSize.y);
            CenterWindow();
        }
    }

    /**
     * Centers the window in the middle of the screen
     */
    private static void CenterWindow()
    {
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        if(vidMode != null)
        {
            SetWindowPosition(new Vector2D((float) (vidMode.width() - (int) defaultWindowSize.x) /2, (float) (vidMode.height() - (int) defaultWindowSize.y) /2));
        }
        else
        {
            Logger.DebugError("Monitor not found, Cannot center window!");
        }
    }

    /**
     * Updates the title with the framerate
     */
    private static void UpdateFramerateDisplay()
    {
        GLFW.glfwSetWindowTitle(window, title + " | " + Math.round(Time.AverageFramerate()) + " FPS");
    }

    /**
     * Calls the glfwSwapBuffers function of GLFW
     */
    public static void SwapBuffers()
    {
        GLFW.glfwSwapBuffers(window);
    }


    /**
     * Returns the window pointer
     * @return
     * The window pointer
     */
    public static long GetWindow()
    {
        return window;
    }
}
