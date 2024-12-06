package JGame.Engine;

import JGame.Engine.EventSystem.Event1PHandler;
import JGame.Engine.Structures.Vector2D;

/**
 * Class holding setting variables used throughout the engine, edit the code directly to change settings.
 */
public final class Settings
{
    public static final class Debug
    {
        private static boolean debugView = false;
        private static boolean debugBVH = false;
        public static final Event1PHandler<Boolean> changeDebugViewEvent = new Event1PHandler<>();
        public static final Event1PHandler<Boolean> changeDebugBVHEvent = new Event1PHandler<>();

        /**
         * Sets the debug view to the desired value and updates all necessary components
         * @param value
         * The value to be set
         */
        public static void SetDebugView(boolean value)
        {
            debugView = value;
            changeDebugViewEvent.Invoke(value);
        }
        public static boolean GetDebugView()
        {
            return debugView;
        }

        public static boolean GetDebugBVH()
        {
            return debugBVH;
        }

        public static void SetDebugBVH(boolean value)
        {
            Debug.debugBVH = value;
            changeDebugBVHEvent.Invoke(value);
        }
    }

    /**
     * Class holding references to the shadowmap resolutions used by all lights in the scene, change these values
     * to alter the resolution used;
     */
    public static final class Shadowmap
    {
        public static final Vector2D RESOLUTION_DIRECTIONAL = new Vector2D(2048, 2048);
        public static final Vector2D RESOLUTION_NON_DIRECTIONAL = new Vector2D(512, 512);

        public static final float DIRECTIONAL_HALF_SIZE = 20f;
        public static final float DIRECTIONAL_NEAR_PLANE = 0.01f;
        public static final float DIRECTIONAL_FAR_PLANE = 100f;

        public static final float SPOTLIGHT_NEAR_PLANE = 0.01f;

        public static final float POINTLIGHT_NEAR_PLANE = 0.001f;
    }
    /**
     * Class holding references and values related to lighting
     */
    public static final class Lighting
    {
        public static final int MAX_LIGHTS = 10;
    }

    /**
     * Class holding settings for physics simulations
     */
    public static final class Physics
    {
        /**
         * The force the gravity exerts in objects
         */
        public static final float gravityForce = 10f;
        /**
         * The interval in seconds between physics updates
         */
        public static final double physicsUpdateInterval = 1f/60f;
        /**
         * The max number of potential collisions to check during the broad collision detection phase in a single frame
         */
        public static final int broadCollisionLimit = 10000;
        /**
         * The max number of contacts to generate during the fine collision detection phase in a single frame
         */
        public static final int fineCollisionLimit = 5000;
        /**
         * The max number of iterations during the contact resolution phase in a single frame
         */
        public static final int contactResolutionLimit = 4000;
    }
}
