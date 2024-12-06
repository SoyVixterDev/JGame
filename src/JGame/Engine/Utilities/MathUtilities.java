package JGame.Engine.Utilities;

public class MathUtilities
{
    /**
     * Clamps the value between the minimum and maximum
     * @param a
     * The value to clamp
     * @param min
     * The minimum value
     * @param max
     * The maximum value
     * @return
     * The clamped value
     */
    public static double Clamp(double a, double min, double max)
    {
        return Math.max(min, Math.min(a, max));
    }
    /**
     * Clamps the value between the minimum and maximum
     * @param a
     * The value to clamp
     * @param min
     * The minimum value
     * @param max
     * The maximum value
     * @return
     * The clamped value
     */
    public static float Clamp(float a, float min, float max)
    {
        return Math.max(min, Math.min(a, max));
    }
    /**
     * Clamps the value between the minimum and maximum
     * @param a
     * The value to clamp
     * @param min
     * The minimum value
     * @param max
     * The maximum value
     * @return
     * The clamped value
     */
    public static int Clamp(int a, int min, int max)
    {
        return Math.max(min, Math.min(a, max));
    }

    /**
     * Determines blending modes for variables
     */
    public enum BlendingMode
    {
        /**
         * Selects the minimum value
         */
        Min,
        /**
         * Selects the maximum value
         */
        Max,
        /**
         * Calculates the average between values
         */
        Average,
        /**
         * Calculates the product between values
         */
        Multiply
    }

    /**
     * Blends between two values using the provided blending mode
     * @param a
     * Value A
     * @param b
     * Value B
     * @param mode
     * Blending mode to use
     * @return
     * The blended value
     */
    public static float Blend(float a, float b, BlendingMode mode)
    {
        return switch (mode)
        {
            case Min -> Math.min(a, b);
            case Max -> Math.max(a, b);
            case Average -> (a + b) / 2;
            case Multiply -> a * b;
        };
    }
}
