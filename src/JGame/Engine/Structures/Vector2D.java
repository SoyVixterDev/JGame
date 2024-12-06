package JGame.Engine.Structures;

import java.util.Objects;

/**
 * Class representing a 3D Vector with components X and Y
 */
public class Vector2D
{
    public final float x;
    public final float y;

    public Vector2D(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    private boolean calculatedNormalized = false;
    private Vector2D normalized;
    /**
     * Returns the normalized vector
     * @return
     * The normalized vector
     */
    public Vector2D Normalized()
    {
        if(!calculatedNormalized)
        {
            if(Math.abs(magnitude - 1.0f) < 0.00001f || magnitude < 0.00001f)
            {
                normalized = this;
            }
            else if(magnitude == Float.POSITIVE_INFINITY || Float.isNaN(magnitude))
            {
                normalized = null;
            }
            else
            {
                normalized = new Vector2D(x / magnitude, y / magnitude);
            }
            calculatedNormalized = true;
        }

        return normalized;
    }


    private boolean calculatedMagnitude;
    private float magnitude;

    /**
     * Gets the magnitude of the vector
     * @return
     * The magnitude of the vector
     */
    public float Magnitude()
    {
        if(!calculatedMagnitude)
        {
            magnitude = (float) Math.sqrt(SquaredMagnitude());
            calculatedMagnitude = true;
        }

        return magnitude;
    }

    private boolean calculatedSquaredMagnitude;
    private float squaredMagnitude;

    /**
     * Gets the squared magnitude of the vector
     * @return
     * The squared magnitude of the vector
     */
    public float SquaredMagnitude()
    {
        if(!calculatedSquaredMagnitude)
        {
            squaredMagnitude = x * x + y * y;
            calculatedSquaredMagnitude = true;
        }

        return squaredMagnitude;
    }

    // Basic 2D vector operations

    /**
     * Returns the Negated Vector
     * @return
     * The Negated Vector
     */
    public Vector2D Negate()
    {
        return new Vector2D(-x, -y);
    }

    /**
     * Multiplies the entire Vector by a scalar
     * @param scale
     * The scalar to be multiplied
     * @return
     * The scaled Vector
     */
    public Vector2D Scale(float scale)
    {
        return new Vector2D(x * scale, y * scale);
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2D vector = (Vector2D) o;
        return Double.compare(x, vector.x) == 0 && Double.compare(y, vector.y) == 0;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(x, y);
    }

    /**
     * Calculates and Returns the Dot product between the Vectors
     * @param a
     * Vector a
     * @param b
     * Vector b
     * @return
     * The result of the dot product
     */
    public static float DotProduct(Vector2D a, Vector2D b)
    {
        return a.x * b.x + a.y * b.y;
    }

    /**
     * Calculates and returns the Distance between the Vectors
     * @param a
     * Vector a
     * @param b
     * Vector b
     * @return
     * The distance between the two Vectors
     */
    public static float Distance(Vector2D a, Vector2D b)
    {
        return (float) Math.sqrt(DistanceSquared(a, b));
    }

    /**
     * Calculates and returns the squared distance between the Vectors
     * @param a
     * Vector a
     * @param b
     * Vector b
     * @return
     * The squared distance between the two Vectors
     */
    public static float DistanceSquared(Vector2D a, Vector2D b)
    {
        float dx = a.x - b.x;
        float dy = a.y - b.y;

        return dx * dx + dy * dy;
    }


    /**
     * Adds the two Vectors
     * @param a
     * Vector a
     * @param b
     * Vector b
     * @return
     * The result of the Vector Addition
     */
    public static Vector2D Add(Vector2D a, Vector2D b)
    {
        return new Vector2D(a.x + b.x, a.y + b.y);
    }

    /**
     * Subtracts Vector b from Vector a
     * @param a
     * Vector a
     * @param b
     * Vector b
     * @return
     * The result of the Vector Subtraction
     */
    public static Vector2D Subtract(Vector2D a, Vector2D b)
    {
        return new Vector2D(a.x - b.x, a.y - b.y);
    }

    /**
     * Multiplies two Vectors element-wise
     * @param a
     * Vector a
     * @param b
     * Vector b
     * @return
     * The result of the Vector element-wise Multiplication
     */
    public static Vector2D Multiply(Vector2D a, Vector2D b)
    {
        return new Vector2D(a.x * b.x, a.y * b.y);
    }

    /**
     * Divides Vector a by Vector b element-wise
     * @param a
     * Vector a
     * @param b
     * Vector b
     * @return
     * The result of the Vector element-wise Division
     */
    public static Vector2D Divide(Vector2D a, Vector2D b)
    {
        return new Vector2D(a.x / b.x, a.y / b.y);
    }

    /**
     * Adds this vector with another vector
     * @param other The other vector
     * @return The result of the vector addition
     */
    public Vector2D Add(Vector2D other)
    {
        return new Vector2D(this.x + other.x, this.y + other.y);
    }

    /**
     * Subtracts another vector from this vector
     * @param other The other vector
     * @return The result of the vector subtraction
     */
    public Vector2D Subtract(Vector2D other)
    {
        return new Vector2D(this.x - other.x, this.y - other.y);
    }

    /**
     * Multiplies this vector with another vector element-wise
     * @param other The other vector
     * @return The result of the element-wise multiplication
     */
    public Vector2D Multiply(Vector2D other)
    {
        return new Vector2D(this.x * other.x, this.y * other.y);
    }

    /**
     * Divides this vector by another vector element-wise
     * @param other The other vector
     * @return The result of the element-wise division
     */
    public Vector2D Divide(Vector2D other)
    {
        return new Vector2D(this.x / other.x, this.y / other.y);
    }


    /**
     * Returns the angle between Vector a and Vector b, in radians
     * @param a
     * Vector a
     * @param b
     * Vector b
     * @return
     * The angle between the vectors, in radians
     */
    public static float Angle(Vector2D a, Vector2D b)
    {
        return (float)Math.acos(DotProduct(a, b) / (a.magnitude * b.magnitude));
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
     * Returns true if there's a NaN component in the vector
     * @return
     * true if there's a NaN component in the vector
     */
    public boolean IsNaN()
    {
        return (Float.isNaN(x)) || (Float.isNaN(y));
    }

    /**
     * Returns true if all components of the vector are zero
     * @return
     * True if all components of the vector are zero
     */
    public boolean IsZero()
    {
        return this.equals(Zero);
    }


    //Factory Variables

    /**
     * Vector2D with components (0, 1)
     */
    public static final Vector2D Up = new Vector2D(0, 1);

    /**
     * Vector2D with components (0, -1)
     */
    public static final Vector2D Down = new Vector2D(0, -1);

    /**
     * Vector2D with components (1, 0)
     */
    public static final Vector2D Right = new Vector2D(1, 0);

    /**
     * Vector2D with components (-1, 0)
     */
    public static final Vector2D Left = new Vector2D(-1, 0);

    /**
     * Vector2D with all components being one
     */
    public static final Vector2D One = new Vector2D(1, 1);

    /**
     * Vector2D with all components being zero
     */
    public static final Vector2D Zero = new Vector2D(0, 0);

    /**
     * Vector2D with all components being Positive Infinity
     */
    public static final Vector2D PositiveInfinity = new Vector2D(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);

    /**
     * Vector2D with all components being Negative Infinity
     */
    public static final Vector2D NegativeInfinity = new Vector2D(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);

    public float[] ToArray()
    {
        return new float[]{x, y};
    }

    @Override
    public String toString()
    {
        return "(" + x + ", " + y + ")";
    }
}
