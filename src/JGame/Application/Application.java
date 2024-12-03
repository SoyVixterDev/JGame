package JGame.Application;

import JGame.Engine.Basic.BaseObject;
import JGame.Engine.Internal.InternalGameInstance;
import JGame.Engine.Basic.JGameObject;
import JGame.Engine.Internal.Time;
import JGame.Engine.Graphics.Renderers.Renderer;
import JGame.Engine.Input.Input;
import JGame.Engine.Physics.General.Physics;
import Project.JGameInstance;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.concurrent.locks.LockSupport;

/**
 * The class used internally by the engine to start the game application, handles the main application loop.
 */
public class Application
{
    public static int targetFramerate = 144;

    private static boolean running = true;

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

            EarlyUpdate();
            Render();
            Update();
            LateUpdate();

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
    private void Sleep(double seconds)
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
        InternalGameInstance.Instance._internalEarlyUpdate();

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
        Physics.UpdatePhysics();

        InternalGameInstance.Instance._internalUpdate();

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
        InternalGameInstance.Instance._internalLateUpdate();
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

        Renderer.RenderOpaques();
        Renderer.RenderTransparents();

        Window.SwapBuffers();

    }

}
