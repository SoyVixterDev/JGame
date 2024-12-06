package JGame.Engine.Structures;

import java.util.Objects;

/**
 * Class representing a 3D Vector with X, Y and Z components
 */
public class Vector3D
{
    public final float x;
    public final float y;
    public final float z;

    public Vector3D(float[] xyz)
    {
        if(xyz.length != 3) throw new IllegalArgumentException("Vector3D can only be made with arrays of 3 elements!");

        this.x = xyz[0];
        this.y = xyz[1];
        this.z = xyz[2];
    }

    public Vector3D(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    private boolean calculatedNormalized = false;
    private Vector3D normalized;

    /**
     * Returns a cloned version of this vector
     * @return
     * A cloned version of this vector
     */
    public Vector3D Clone()
    {
        return new Vector3D(x, y, z);
    }

    /**
     * Returns the normalized vector
     * @return
     * The normalized vector
     */
    public Vector3D Normalized()
    {
        if(!calculatedNormalized)
        {
            float magnitude = Magnitude();
            if(magnitude == 0f || Float.isNaN(magnitude))
            {
                normalized = Vector3D.Zero;
            }
            else
            {
                normalized = new Vector3D(x / magnitude, y / magnitude, z / magnitude);
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
            squaredMagnitude = x * x + y * y + z * z;
            calculatedSquaredMagnitude = true;
        }

        return squaredMagnitude;
    }
    /**
     * Returns the Negated Vector
     * @return
     * The Negated Vector
     */
    public Vector3D Negate()
    {
        return new Vector3D(-x, -y, -z);
    }

    /**
     * Multiplies the entire vector by a scalar
     * @param scale
     * The scalar value to multiply
     * @return
     * The scaled Vector
     */
    public Vector3D Scale(float scale)
    {
        return new Vector3D(x * scale, y * scale, z * scale);
    }

    /**
     * Applies a quaternion rotation to the Vector
     * @param rotation
     * The rotation to apply
     * @return
     * The rotated Vector
     */
    public Vector3D Rotate(Quaternion rotation)
    {
        Quaternion vectorQuat = new Quaternion(0, x, y, z);
        Quaternion rotatedQuat = Quaternion.Multiply(Quaternion.Multiply(rotation, vectorQuat), rotation.Conjugate());

        return new Vector3D(rotatedQuat.x, rotatedQuat.y, rotatedQuat.z);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3D vector = (Vector3D) o;
        return Double.compare(x, vector.x) == 0 && Double.compare(y, vector.y) == 0 && Double.compare(z, vector.z) == 0;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(x, y, z);
    }

    // 3D-specific methods

    /**
     * Performs cross product between Vector a and Vector b
     * @param a
     * Vector a
     * @param b
     * Vector b
     * @return
     * The Vector result of the cross product, perpendicular to both Vectors
     */
    public static Vector3D CrossProduct(Vector3D a, Vector3D b)
    {
        float x = a.y * b.z - a.z * b.y;
        float y = a.z * b.x - a.x * b.z;
        float z = a.x * b.y - a.y * b.x;

        return new Vector3D(x, y, z);
    }

    /**
     * Performs dot product between Vector a and Vector b
     * @param a
     * Vector a
     * @param b
     * Vector b
     * @return
     * The result of the dot product
     */
    public static float DotProduct(Vector3D a, Vector3D b)
    {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }
    /**
     * Generates and returns a scale matrix equivalent to the Vector's components as a scale transformation
     * @return
     * Scale matrix equivalent to the Vector's coordinates as a scale transformation
     */
    public Matrix4x4 ToScaleMatrix()
    {
        float[] matrix =
                {
                    x, 0, 0, 0,
                    0, y, 0, 0,
                    0, 0, z, 0,
                    0, 0, 0, 1
                };
        return new Matrix4x4(matrix);
    }

    /**
     * Generates and returns a translation matrix equivalent to the Vector's components as coordinates of a translation transformation
     * @return
     * Translation matrix equivalent to the Vector's components as coordinates of a translation transformation
     */
    public Matrix4x4 ToTranslationMatrix()
    {
        float[] matrix =
                {
                        1, 0, 0, x,
                        0, 1, 0, y,
                        0, 0, 1, z,
                        0, 0, 0, 1
                };
        return (new Matrix4x4(matrix));
    }

    /**
     * Applies a Translation Transformation Matrix
     * @param matrix
     * The translation transformation matrix
     * @return
     * The translated vector
     */
    public Vector3D Translate(Matrix4x4 matrix)
    {
        return new Vector3D
        (
                this.x + matrix.values[3],
                this.y + matrix.values[7],
                this.z + matrix.values[11]
        );
    }

    /**
     * Applies a Rotation Transformation Matrix
     * @param matrix
     * The rotation transformation matrix
     * @return
     * The rotated vector
     */
    public Vector3D Rotate(Matrix4x4 matrix)
    {
        float x = this.x * matrix.values[0] + this.y * matrix.values[1] + this.z * matrix.values[2];
        float y = this.x * matrix.values[4] + this.y * matrix.values[5] + this.z * matrix.values[6];
        float z = this.x * matrix.values[8] + this.y * matrix.values[9] + this.z * matrix.values[10];

        return new Vector3D(x, y, z);
    }

    /**
     * Applies a scale Transformation Matrix
     * @param matrix
     * The scale transformation matrix
     * @return
     * The scaled vector
     */
    public Vector3D Scale(Matrix4x4 matrix)
    {
        float x = this.x * matrix.values[0];
        float y = this.y * matrix.values[5];
        float z = this.z * matrix.values[10];

        return new Vector3D(x, y, z);
    }
    /**
     * Adds two Vectors
     * @param a
     * Vector a
     * @param b
     * Vector b
     * @return
     * The result of the vector addition
     */
    public static Vector3D Add(Vector3D a, Vector3D b)
    {
        return new Vector3D(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    /**
     * Subtracts Vector b from Vector a
     * @param a
     * Vector a
     * @param b
     * Vector b
     * @return
     * The result of the Vector subtraction
     */
    public static Vector3D Subtract(Vector3D a, Vector3D b)
    {
        return new Vector3D(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    /**
     * Multiplies two vectors element-wise
     * @param a
     * Vector a
     * @param b
     * Vector b
     * @return
     * The result of the element-wise Vector Multiplication
     */
    public static Vector3D Multiply(Vector3D a, Vector3D b)
    {
        return new Vector3D(a.x * b.x, a.y * b.y, a.z * b.z);
    }

    /**
     * Divides Vector a by Vector b element-wise
     * @param a
     * Vector a
     * @param b
     * Vector b
     * @return
     * The result of the element-wise Vector Division
     */
    public static Vector3D Divide(Vector3D a, Vector3D b)
    {
        return new Vector3D(a.x / b.x, a.y / b.y, a.z / b.z);
    }

    /**
     * Performs dot product between this and other vector
     * @param other
     * The other vector
     * @return
     * The scalar result of the dot product
     */
    public float DotProduct(Vector3D other)
    {
        return DotProduct(this, other);
    }

    /**
     * Performs cross product between this and other vector
     * @param other
     * The other vector
     * @return
     * The Vector result of the cross product, perpendicular to both Vectors
     */
    public Vector3D CrossProduct(Vector3D other)
    {
        return CrossProduct(this, other);
    }

    /**
     * Adds this vector with another vector
     * @param other The other vector
     * @return The result of the vector addition
     */
    public Vector3D Add(Vector3D other)
    {
        return new Vector3D(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    /**
     * Subtracts another vector from this vector
     * @param other The other vector
     * @return The result of the vector subtraction
     */
    public Vector3D Subtract(Vector3D other)
    {
        return new Vector3D(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    /**
     * Multiplies this vector with another vector element-wise
     * @param other The other vector
     * @return The result of the element-wise multiplication
     */
    public Vector3D Multiply(Vector3D other)
    {
        return new Vector3D(this.x * other.x, this.y * other.y, this.z * other.z);
    }

    /**
     * Divides this vector by another vector element-wise
     * @param other The other vector
     * @return The result of the element-wise division
     */
    public Vector3D Divide(Vector3D other)
    {
        return new Vector3D(this.x / other.x, this.y / other.y, this.z / other.z);
    }

    /**
     * Adds a scaled vector
     * @param other
     * The other vector
     * @param scale
     * The scale
     * @return
     * The result of the addition
     */
    public Vector3D AddScaledVector(Vector3D other, float scale)
    {
        return new Vector3D(x + other.x * scale, y + other.y * scale, z + other.z * scale);
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
    public static float Distance(Vector3D a, Vector3D b)
    {
        return (float)Math.sqrt(DistanceSquared(a, b));
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
    public static float DistanceSquared(Vector3D a, Vector3D b)
    {
        float dx = a.x - b.x;
        float dy = a.y - b.y;
        float dz = a.z - b.z;

        return dx * dx + dy * dy + dz * dz;
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
     * Returns true if there's a NaN component in the vector
     * @return
     * true if there's a NaN component in the vector
     */
    public boolean IsNaN()
    {
        return (Float.isNaN(x)) || (Float.isNaN(y)) || (Float.isNaN(z));
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

    //Factory variables
    /**
     * Vector3D with components (0, 0, 1)
     */
    public static final Vector3D Forward = new Vector3D(0, 0, 1);

    /**
     * Vector3D with components (0, 0, -1)
     */
    public static final Vector3D Backward = new Vector3D(0, 0, -1);

    /**
     * Vector3D with components (0, 1, 0)
     */
    public static final Vector3D Up = new Vector3D(0, 1, 0);

    /**
     * Vector3D with components (0, -1, 0)
     */
    public static final Vector3D Down = new Vector3D(0, -1, 0);

    /**
     * Vector3D with components (1, 0, 0)
     */
    public static final Vector3D Right = new Vector3D(1, 0, 0);

    /**
     * Vector3D with components (-1, 0, 0)
     */
    public static final Vector3D Left = new Vector3D(-1, 0, 0);

    /**
     * Vector3D with all components being one
     */
    public static final Vector3D One = new Vector3D(1, 1, 1);

    /**
     * Vector3D with all components being zero
     */
    public static final Vector3D Zero = new Vector3D(0, 0, 0);

    /**
     * Vector3D with all components being Positive Infinity
     */
    public static final Vector3D PositiveInfinity = new Vector3D(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);

    /**
     * Vector3D with all components being Negative Infinity
     */
    public static final Vector3D NegativeInfinity = new Vector3D(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);


    public float[] ToArray()
    {
        return new float[]{x, y, z};
    }

    @Override
    public String toString()
    {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
