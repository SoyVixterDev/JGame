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
}
