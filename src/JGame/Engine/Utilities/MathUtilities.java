package JGame.Engine.Utilities;

import JGame.Engine.Structures.Quaternion;
import JGame.Engine.Structures.Vector2D;
import JGame.Engine.Structures.Vector3D;

public class MathUtilities
{
    public final static float TO_DEGREES =  180.0f / (float)Math.PI;
    public final static float TO_RADIANS =  (float)Math.PI / 180.0f;


    /**
     * Performs an element-wise max function
     * @param a
     * Vector A
     * @param b
     * Vector B
     * @return
     * The Vector3D resulting of the bigger components between a and b
     */
    public static Vector3D Max(Vector3D a, Vector3D b)
    {
        return new Vector3D
                (
                        Math.max(a.x, b.x),
                        Math.max(a.y, b.y),
                        Math.max(a.z, b.z)
                );
    }

    /**
     * Performs an element-wise min function
     * @param a
     * Vector A
     * @param b
     * Vector B
     * @return
     * The Vector3D resulting of the smaller components between a and b
     */
    public static Vector3D Min(Vector3D a, Vector3D b)
    {
        return new Vector3D
                (
                        Math.min(a.x, b.x),
                        Math.min(a.y, b.y),
                        Math.min(a.z, b.z)
                );
    }

    /**
     * Performs an element-wise max function
     * @param a
     * Vector A
     * @param b
     * Vector B
     * @return
     * The Vector3D resulting of the bigger components between a and b
     */
    public static Vector2D Max(Vector2D a, Vector2D b)
    {
        return new Vector2D
                (
                        Math.max(a.x, b.x),
                        Math.max(a.y, b.y)
                );
    }

    /**
     * Performs an element-wise min function
     * @param a
     * Vector A
     * @param b
     * Vector B
     * @return
     * The Vector3D resulting of the smaller components between a and b
     */
    public static Vector2D Min(Vector2D a, Vector2D b)
    {
        return new Vector2D
                (
                        Math.min(a.x, b.x),
                        Math.min(a.y, b.y)
                );
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

    public static Vector3D Abs(Vector3D vector)
    {
        return new Vector3D(Math.abs(vector.x), Math.abs(vector.y), Math.abs(vector.z));
    }

    public static Vector2D Abs(Vector2D vector)
    {
        return new Vector2D(Math.abs(vector.x), Math.abs(vector.y));
    }


    /**
     * Linearly interpolates between to values of equal type if supported
     * @param a
     * From
     * @param b
     * To
     * @param t
     * By
     * @return
     * The interpolated value
     * @param <T>
     * The type of the values
     */
    public static <T> T Lerp(T a,T b, float t)
    {
        if (a instanceof Vector3D && b instanceof Vector3D)
        {
            return (T) Lerp((Vector3D) a, (Vector3D) b, t);
        }
        else if (a instanceof Vector2D && b instanceof Vector2D)
        {
            return (T) Lerp((Vector2D) a, (Vector2D) b, t);
        }
        else if (a instanceof Quaternion && b instanceof Quaternion)
        {
            return (T) Lerp((Quaternion) a, (Quaternion) b, t);
        }
        else
        {
            throw new IllegalArgumentException("Unsupported types for linear interpolation: " + a.getClass() + " and " + b.getClass());
        }
    }


    /**
     * Linearly interpolates from vector a to a vector b by t (0 to 1)
     * @param a
     * From
     * @param b
     * To
     * @param t
     * By
     * @return
     * The interpolated vector
     */
    public static Vector2D Lerp(Vector2D a, Vector2D b, float t)
    {
        return Vector2D.Add
                (
                        a.Scale(1.0f - t),
                        b.Scale(t)
                );
    }

    /**
     * Linearly interpolates from vector a to a vector b by t (0 to 1)
     * @param a
     * From
     * @param b
     * To
     * @param t
     * By
     * @return
     * The interpolated vector
     */
    public static Vector3D Lerp(Vector3D a, Vector3D b, float t)
    {
        return Vector3D.Add
                (
                        a.Scale(1.0f - t),
                        b.Scale(t)
                );
    }

    /**
     * Linearly interpolates between 2 quaternions, by t
     * @param a
     * Quaternion A
     * @param b
     * Quaternion B
     * @param t
     * Parameter
     * @return
     * The linear interpolation result
     */
    public static Quaternion Lerp(Quaternion a, Quaternion b, float t)
    {
        if (a.DotProduct(b) < 0.0f)
        {
            b = b.Negate();
        }

        Quaternion result = a.Scale(1 - t).Add(b.Scale(t));

        return result.Normalized();
    }
}
