package JGame.Application;

import JGame.Engine.Basic.BaseObject;
import JGame.Engine.Basic.JGameObject;
import JGame.Engine.Graphics.Renderers.RayTracingRenderer;
import JGame.Engine.Internal.Time;
import JGame.Engine.Graphics.Renderers.Renderer;
import JGame.Engine.Input.Input;
import JGame.Engine.Physics.General.Physics;
import Project.JGameInstance;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

/**
 * The class used internally by the engine to start the game application, handles the main application loop.
 */
public class Application
{
    public static int targetFramerate = 144;

    private static boolean running = true;

    private static boolean useRayTracing = false;

    public Application()
    {
        RunApplication();
    }

    /**
     * Handles the application runtime, calling rendering and update functions and handling exiting
     */
    void RunApplication()
    {
        JGameInstance.Instance._internalInitialize();

        Input.Init();
        Time.Initialize();

        while(running)
        {
            double startTime = Time.Current();
            double frameTime = (double)1.0f / (double)targetFramerate;

            GLFW.glfwPollEvents();

            if(Input.GetKeyDown(GLFW.GLFW_KEY_ESCAPE))
                Input.SetMouseLock(false);

            EarlyUpdate();
            Physics.UpdatePhysics();
            Update();
            Render();
            LateUpdate();

            Input.ResetInputStates();

            double elapsedTime = Time.Current() - startTime;
            double sleepTime = frameTime - elapsedTime;

            if (sleepTime >= 0)
            {
                Sleep(sleepTime);
            }
        }

        JGameObject.Terminate();
        Window.Terminate();
        Input.Destroy();
    }

    /**
     * Busy Waiting precise sleep in seconds
     * @param seconds
     * Seconds to sleep
     */
    public static void Sleep(double seconds)
    {
        long targetTime = System.nanoTime() + (long) (seconds * 1_000_000_000);

        while (System.nanoTime() <= targetTime)
        {
            Thread.yield();
        }
    }

    /**
     * Stops the application and terminates all objects
     */
    public static void Quit()
    {
        running = false;
    }

    /**
     * Called at the beginning of the frame, before rendering and physics updates
     */
    private void EarlyUpdate()
    {
        for(BaseObject baseObj : new ArrayList<>(BaseObject.allBaseObjects))
        {
            if(baseObj != null && baseObj.IsAvailable())
            {
                baseObj._internalEarlyUpdate();
            }
        }

    }
    /**
     * Called at the middle of each frame, after rendering
     */
    private void Update()
    {
        Time.UpdateTime();

        for(BaseObject baseObj : new ArrayList<>(BaseObject.allBaseObjects))
        {
            if(baseObj != null && baseObj.IsAvailable())
            {
                baseObj._internalUpdate();
            }
        }
        Window.Update();
    }

    /**
     * Called at the end of each frame
     */
    private void LateUpdate()
    {
        for(BaseObject baseObj : new ArrayList<>(BaseObject.allBaseObjects))
        {
            if(baseObj != null && baseObj.IsAvailable())
            {
                baseObj._internalLateUpdate();
            }
        }
    }

    /**
     * Function that handles calling the different functions needed for rendering
     */
    private void Render()
    {
        if(Window.window == 0)
            return;

        if(useRayTracing)
        {
            RayTracingRenderer.RenderAll();
        }
        else
        {
            Renderer.RenderOpaques();
            Renderer.RenderTransparents();
        }


        Window.SwapBuffers();

    }

    /**
     * Sets the ray tracing state
     * @param value
     */
    public static void SetRaytracing(boolean value)
    {
        if(value == useRayTracing)
            return;

        useRayTracing = value;

        if(value)
            RayTracingRenderer.InitializeRenderer();
        else
            RayTracingRenderer.CleanupRenderer();
    }

    /**
     * Gets the current state of the ray tracing engine (enabled or disabled)
     * @return
     * The current state of the ray tracing engine (enabled or disabled)
     */
    public static boolean GetRaytracingState()
    {
        return useRayTracing;
    }
}
