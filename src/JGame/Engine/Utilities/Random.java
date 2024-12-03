package JGame.Engine.Utilities;

import JGame.Engine.Structures.ColorRGBA;
import JGame.Engine.Structures.Vector3D;

import java.awt.*;

public class Random
{
    private final static java.util.Random rand = new java.util.Random();
    /**
     * Generates a random float, inclusive
     * @param min
     * Minimum value, inclusive
     * @param max
     * Maximum value, inclusive
     * @return
     * A random float
     */
    public static float RandomNumber(float min, float max)
    {
        return min + rand.nextFloat() * (max - min);
    }
    /**
     * Generates a random integer, exclusive
     * @param min
     * Minimum value, included
     * @param max
     * Maximum value, excluded
     * @return
     * A random integer
     */
    public static int RandomNumber(int min, int max)
    {
        if(min == max)
            return min;

        return rand.nextInt(max - min) + min;
    }

    /**
     * Generates a random Vector3D, inclusive
     * @param min
     * The minimum value, inclusive
     * @param max
     * The maximum value, inclusive
     * @return
     * A random Vector3D
     */
    public static Vector3D RandomVector3D(Vector3D min, Vector3D max)
    {
        return new Vector3D(RandomNumber(min.x, max.x), RandomNumber(min.y, max.y), RandomNumber(min.z, max.z));
    }

    /**
     * Generates a completely Random Color
     * @return
     * A random color
     */
    public static ColorRGBA RandomColorRGBA()
    {
        return RandomColorRGBA(ColorRGBA.Black, ColorRGBA.White);
    }

    /**
     * Generates a random color within the RGBA values
     * @param min
     * The minimum RGBA values, inclusive
     * @param max
     * The maximum RGBA values, inclusive
     * @return
     * The random Color
     */
    public static ColorRGBA RandomColorRGBA(ColorRGBA min, ColorRGBA max)
    {
        return new ColorRGBA(RandomNumber(min.r, max.r), RandomNumber(min.g, max.g), RandomNumber(min.b, max.b), RandomNumber(min.a, max.a));
    }
}
