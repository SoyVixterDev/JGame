package JGame.Engine.Internal;

import JGame.Engine.Settings;
import jdk.jfr.Unsigned;

/**
 * Class used to obtain information and handle time related information
 */
public class Time
{
    //Frame variables
    public static boolean calculateFramerate = true;

    public static float timeScale = 1.0f;

    private static final int DELTA_TIME_HISTORY_SIZE = 60;
    private static final double[] DELTA_TIME_HISTORY = new double[DELTA_TIME_HISTORY_SIZE];

    private static double avgFramerate = 0;
    private static int frameCount = 0;
    private static double lastFrameTime;
    private static double lastPhysicsUpdateTime;

    //Time variables
    private static double deltaTime = 0;
    private static double physicsDeltaTime = 0;

    @Unsigned
    private static long frame = 0;

    public static void Initialize()
    {
        lastFrameTime = Current();
        lastPhysicsUpdateTime = Current();
    }

    /**
     * Function used to update the time variables
     */
    public static void UpdateTime()
    {
        frame++;
        double currentTime = Current();
        deltaTime = Math.max(currentTime - lastFrameTime, Double.MIN_VALUE);

        if(calculateFramerate)
        {
            UpdateFramerate();
        }

        lastFrameTime = currentTime;
    }

    /**
     * Function used to update the physics delta time. In practical cases this should always return the same value,
     * but if the framerate goes below 60 it may change.
     */
    public static void UpdatePhysicsTime()
    {
        double currentTime = Current();

        physicsDeltaTime = Math.max(currentTime - lastPhysicsUpdateTime, 0);

        lastPhysicsUpdateTime = currentTime;
    }
    /**
     * Function used to Calculate and Update the current average framerate
     */
    private static void UpdateFramerate()
    {
        DELTA_TIME_HISTORY[frameCount] = deltaTime;
        frameCount = (frameCount + 1) % DELTA_TIME_HISTORY_SIZE;

        double avgDeltaTime = 0;

        for(double dt : DELTA_TIME_HISTORY)
        {
            avgDeltaTime += dt;
        }

        avgDeltaTime = avgDeltaTime/DELTA_TIME_HISTORY_SIZE;

        avgFramerate = 1f/Math.max(avgDeltaTime, Double.MIN_VALUE);
    }

    /**
     * Gets the amount of frames from the start of the application
     * @return The amount of frames from the start of the application
     */
    public static long Frame() { return frame; }

    /**
     * Gets the time in seconds between the last and current frame
     * @return
     * The time between the last and current frame, in seconds
     */
    public static double DeltaTime()
    {
        return deltaTime;
    }

    /**
     * Gets the interval between physics updates
     * @return
     * The interval between physics updates
     */
    public static double PhysicsDeltaTime()
    {
        return Settings.Physics.physicsUpdateInterval;
    }

    /**
     * Gets the exact time between the last and current physics updates, should only be used for debug, physics calculations expect PhysicsDeltaTime
     * @return
     * The time between the last and current physics updates, in seconds
     */
    public static double ExactPhysicsDeltaTime()
    {
        return physicsDeltaTime;
    }


    /**
     * Returns the current time in seconds
     * @return
     * The current time in seconds
     */
    public static double Current()
    {
        return System.currentTimeMillis() / (double)1000f;
    }

    public static double AverageFramerate()
    {
        return avgFramerate;
    }


    public static class Stopwatch
    {
        private double startTime = 0.0f;
        private double endTime = 0.0f;

        /**
         * Resets and starts the stopwatch
         */
        public void Start()
        {
            endTime = -1.0f;
            startTime = Time.Current();
        }

        /**
         * Stops the stopwatch
         */
        public void Stop()
        {
            endTime = Time.Current();
        }

        /**
         * Gets the time measured by the stopwatch
         * @return
         * The time measured by the stopwatch in seconds
         */
        public double Elapsed()
        {
            double timeToCompare = endTime < 0.0f ? Time.Current() : endTime;

            return timeToCompare - startTime;
        }
    }

}
