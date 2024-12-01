package JGame.Application;

import JGame.Engine.Basic.BaseEngineClass;
import JGame.Engine.Internal.InternalGameInstance;
import JGame.Engine.Basic.JGameObject;
import JGame.Engine.Internal.Time;
import JGame.Engine.Graphics.Renderers.Renderer;
import JGame.Engine.Input.Input;
import JGame.Engine.Physics.General.Physics;
import JGame.Engine.Settings;
import Project.JGameInstance;
import org.lwjgl.glfw.GLFW;

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
        JGameInstance._internalInitialize();

        Input.Init();
        Time.Initialize();

        while(running)
        {

            GLFW.glfwPollEvents();

//            double startTime = Time.Current();
//            double frameTime = 1.0f / targetFramerate;

            EarlyUpdate();
            Render();
            Update();
            LateUpdate();

//            double elapsedTime = startTime - Time.Current();
//            double sleepTime = frameTime - elapsedTime; // Time to sleep in seconds
//
//            if (sleepTime > 0)
//            {
//                try
//                {
//                    Thread.sleep((long)(sleepTime * 1000) - 2);
//                }
//                catch (InterruptedException ignored){}
//            }
        }

        JGameObject.Terminate();
        Window.Terminate();
        Input.Destroy();
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

        for(BaseEngineClass baseObj : BaseEngineClass.allBaseObjects)
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
        for(BaseEngineClass baseObj : BaseEngineClass.allBaseObjects)
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
        for(BaseEngineClass baseObj : BaseEngineClass.allBaseObjects)
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
