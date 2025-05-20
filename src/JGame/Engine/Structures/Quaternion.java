package JGame.Engine.Structures;

import JGame.Engine.Utilities.MathUtilities;

import java.util.Objects;
import java.util.Vector;

/**
 * A 4D representation of rotation, consisting of a scalar part (w) and a vector part (x, y, z)
 */
public class Quaternion
{
    public final float w;
    public final float x;
    public final float y;
    public final float z;

    public Quaternion(Quaternion quaternion)
    {
        this.w = quaternion.w;
        this.x = quaternion.x;
        this.y = quaternion.y;
        this.z = quaternion.z;
    }

    /**
     * Creates a Quaternion representing a rotation
     * @param w
     * The scalar part of the Quaternion
     * @param x
     * The x component of the Vector part
     * @param y
     * The y component of the Vector part
     * @param z
     * The z component of the Vector part
     */
    public Quaternion(float w, float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    //------Math Functions-------

    /**
     * Adds two Quaternions
     * @param a
     * Quaternion a
     * @param b
     * Quaternion b
     * @return
     * The result of the Quaternion Addition
     */
    public static Quaternion Add(Quaternion a, Quaternion b)
    {
        float w = a.w + b.w;
        float x = a.x + b.x;
        float y = a.y + b.y;
        float z = a.z + b.z;

        return new Quaternion(w, x, y, z);
    }

    /**
     * Adds this quaternion to another
     * @param other
     * The other Quaternion
     * @return
     * The result of the quaternion addition
     */
    public Quaternion Add(Quaternion other)
    {
        return Add(this, other);
    }

    /**
     * Adds a vector to this quaternion, treating it like a rotation
     * @param vector
     * The Vector, rotation
     * @return
     * The result of the addition
     */
    public Quaternion Add(Vector3D vector)
    {
        return Add(this, vector);
    }

    /**
     * Adds a vector to a quaternion, treating it like a rotation
     * @param quaternion
     * The quaternion
     * @param vector
     * The Vector, rotation
     * @return
     * The result of the addition
     */
    public static Quaternion Add(Quaternion quaternion, Vector3D vector)
    {
        Quaternion q = new Quaternion(0, vector.x, vector.y, vector.z);
        q = q.Multiply(quaternion);

        float w = quaternion.w + (q.w * 0.5f);
        float x = quaternion.x + (q.x * 0.5f);
        float y = quaternion.y + (q.y * 0.5f);
        float z = quaternion.z + (q.z * 0.5f);

        return new Quaternion(w, x, y, z).Normalized();
    }


    /**
     * Adds a scaled vector to this quaternion
     * @param vector
     * The vector representing the rotation
     * @param scale
     * The scale
     * @return
     * The result of the addition
     */
    public Quaternion AddScaledVector(Vector3D vector, float scale)
    {
        Quaternion q = new Quaternion(0, vector.x * scale, vector.y * scale, vector.z * scale);
        q = q.Multiply(this);

        float w = this.w + (q.w * 0.5f);
        float x = this.x + (q.x * 0.5f);
        float y = this.y + (q.y * 0.5f);
        float z = this.z + (q.z * 0.5f);

        return new Quaternion(w, x, y, z).Normalized();
    }

    /**
     * Scales a quaternion by a scalar value
     * @param quaternion
     * The quaternion to scale
     * @param scalar
     * The value to scale to
     * @return
     * The scaled Quaternion
     */
    public static Quaternion Scale(Quaternion quaternion, float scalar)
    {
        return new Quaternion(quaternion.w * scalar, quaternion.x * scalar, quaternion.y * scalar, quaternion.z * scalar);
    }
    /**
     * Scales this quaternion by a scalar value
     * @param scalar
     * The value to scale to
     * @return
     * The scaled Quaternion
     */
    public Quaternion Scale(float scalar)
    {
        return Scale(this, scalar);
    }

    /**
     * Subtracts Quaternion b from Quaternion a
     * @param a
     * Quaternion a
     * @param b
     * Quaternion b
     * @return
     * The result of the Quaternion subtraction
     */
    public static Quaternion Subtract(Quaternion a, Quaternion b)
    {
        float w = a.w - b.w;
        float x = a.x - b.x;
        float y = a.y - b.y;
        float z = a.z - b.z;

        return new Quaternion(w, x, y, z);
    }

    /**
     * Multiplies two Quaternions
     * @param a
     * Quaternion a
     * @param b
     * Quaternion b
     * @return
     * The result of the Quaternion multiplication
     */
    public static Quaternion Multiply(Quaternion a, Quaternion b)
    {
        float w = a.w * b.w - a.x * b.x - a.y * b.y - a.z * b.z;
        float x = a.w * b.x + a.x * b.w + a.y * b.z - a.z * b.y;
        float y = a.w * b.y + a.y * b.w + a.z * b.x - a.x * b.z;
        float z = a.w * b.z + a.z * b.w + a.x * b.y - a.y * b.x;

        if (Math.abs(w) < 1e-6) w = 0.0f;
        if (Math.abs(x) < 1e-6) x = 0.0f;
        if (Math.abs(y) < 1e-6) y = 0.0f;
        if (Math.abs(z) < 1e-6) z = 0.0f;

        return new Quaternion(w, x, y, z);
    }

    /**
     * Multiplies this quaternion with another
     * @param other
     * The other quaternion
     * @return
     * The result of the quaternion multiplication
     */
    public Quaternion Multiply(Quaternion other)
    {
        return Multiply(this, other);
    }

    /**
     * Divides Quaternion a by quaternion b
     * @param a
     * Quaternion a
     * @param b
     * Quaternion b
     * @return
     * The result of the Quaternion division
     */
    public static Quaternion Divide(Quaternion a, Quaternion b)
    {
        return Multiply(a, b.Inverse());
    }

    /**
     * Divides this quaternion by another
     * @param other
     * The other quaternion
     * @return
     * The result of the quaternion division
     */
    public Quaternion Divide(Quaternion other)
    {
        return Divide(this, other);
    }


    /**
     * Calculates and returns the dot product between two Quaternions
     * @param a
     * Quaternion a
     * @param b
     * Quaternion b
     * @return
     * The dot product between the two Quaternions
     */
    public static float DotProduct(Quaternion a, Quaternion b)
    {
        return a.w * b.w + a.x * b.x + a.y * b.y + a.z * b.z;
    }

    /**
     * Performs DotProduct between this quaternion and another
     * @param other
     * The other quaternion
     * @return
     * The result of the dot product
     */
    public float DotProduct(Quaternion other)
    {
        return DotProduct(this, other);
    }

    /**
     * Returns the negated Quaternion
     * @return
     * The negated Quaternion
     */
    public Quaternion Negate() {
        return new Quaternion(-w, -x, -y, -z);
    }
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quaternion that = (Quaternion) o;
        return Float.compare(w, that.w) == 0 && Float.compare(x, that.x) == 0 && Float.compare(y, that.y) == 0 && Float.compare(z, that.z) == 0;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(w, x, y, z);
    }

    //-------Other Functions------

    /**
     * Gets a quaternion from an axis and an angle
     * @param axis
     * The axis
     * @param angle
     * The angle
     * @return
     * The resulting quaternion
     */
    public static Quaternion FromAxisAngle(Vector3D axis, float angle)
    {
        Vector3D normalizedAxis = axis.Normalized();
        float halfAngle = angle / 2.0f;
        float sinHalfAngle = (float) Math.sin(halfAngle);
        float cosHalfAngle = (float) Math.cos(halfAngle);

        return new Quaternion(
                cosHalfAngle,
                normalizedAxis.x * sinHalfAngle,
                normalizedAxis.y * sinHalfAngle,
                normalizedAxis.z * sinHalfAngle
        );
    }

    /**
     * Returns a Quaternion representation of a Vector3D of euler angles in radians
     * @param eulerAngles
     * The euler representation of the rotation in radians
     * @return
     * A Quaternion representation of the rotation
     */
    public static Quaternion EulerToQuaternion(Vector3D eulerAngles)
    {
        return EulerToQuaternion(eulerAngles, false);
    }

    /**
     * Returns a Quaternion representation of a Vector3D of euler angles
     * @param eulerAngles
     * The euler representation of the rotation
     * @param inRadians
     * Are the provided angles in radians?
     * @return
     * A Quaternion representation of the rotation
     */
    public static Quaternion EulerToQuaternion(Vector3D eulerAngles, boolean inRadians)
    {
        if(!inRadians)
            eulerAngles = eulerAngles.Scale(MathUtilities.TO_RADIANS);

        float cr = (float)Math.cos(eulerAngles.x * 0.5f);
        float sr = (float)Math.sin(eulerAngles.x * 0.5f);
        float cp = (float)Math.cos(eulerAngles.y * 0.5f);
        float sp = (float)Math.sin(eulerAngles.y * 0.5f);
        float cy = (float)Math.cos(eulerAngles.z * 0.5f);
        float sy = (float)Math.sin(eulerAngles.z * 0.5f);

        float w = cr * cp * cy + sr * sp * sy;
        float x = sr * cp * cy - cr * sp * sy;
        float y = cr * sp * cy + sr * cp * sy;
        float z = cr * cp * sy - sr * sp * cy;

        return new Quaternion(w, x, y, z);
    }

    /**
     * Returns the conjugated quaternion
     * @return
     * The conjugated quaternion
     */
    public Quaternion Conjugate()
    {
        return new Quaternion(w, -x, -y, -z);
    }

    /**
     * Returns the magnitude of the quaternion
     * @return
     * The magnitude of the quaternion
     */
    public float Magnitude()
    {
        return (float)Math.sqrt(w * w + x * x + y * y + z * z);
    }
    /**
     * Returns the squared magnitude of the quaternion
     * @return
     * The squared magnitude of the quaternion
     */
    public float MagnitudeSquared()
    {
        return (w * w + x * x + y * y + z * z);
    }

    /**
     * Returns the Inverse of the quaternion
     * @return
     * The inverse of the quaternion
     */
    public Quaternion Inverse()
    {
        Quaternion conjugate = Conjugate();
        return conjugate.Normalized();
    }

    /**
     * Returns a Vector3D containing the Euler representation of the quaternion, in degrees
     * @return
     * The Euler representation of the quaternion in degrees
     */
    public Vector3D EulerAngles()
    {
        return EulerAngles(false);
    }

    /**
     * Returns a Vector3D containing the Euler representation of the quaternion
     * @param inRadians
     * Return in radians?
     * @return
     * A Vector3D containing the Euler representation of the quaternion
     */
    public Vector3D EulerAngles(boolean inRadians)
    {
        float sinr_cosp = 2 * (w * x + y * z);
        float cosr_cosp = 1 - 2 * (x * x + y * y);
        float roll = (float) Math.atan2(sinr_cosp, cosr_cosp);

        float sinp = 2 * (w * y - z * x);
        float pitch;
        if (Math.abs(sinp) >= 1)
            pitch = (float) Math.copySign(Math.PI / 2, sinp);
        else
            pitch = (float) Math.asin(sinp);

        float siny_cosp = 2 * (w * z + x * y);
        float cosy_cosp = 1 - 2 * (y * y + z * z);
        float yaw = (float) Math.atan2(siny_cosp, cosy_cosp);

        Vector3D angles = new Vector3D(roll, pitch, yaw);

        if(!inRadians)
            angles = angles.Scale(MathUtilities.TO_DEGREES);

        return angles;
    }

    /**
     * Returns the Matrix4x4 representation of the Quaternion
     * @return
     * The Matrix4x4 representation of the Quaternion
     */
    public Matrix4x4 ToRotationMatrix()
    {
        float xx = x * x;
        float xy = x * y;
        float xz = x * z;
        float yy = y * y;
        float yz = y * z;
        float zz = z * z;
        float wx = w * x;
        float wy = w * y;
        float wz = w * z;

        float[] matrix =
        {
            1 - 2 * (yy + zz), 2 * (xy - wz), 2 * (xz + wy), 0,
            2 * (xy + wz), 1 - 2 * (xx + zz), 2 * (yz - wx), 0,
            2 * (xz - wy), 2 * (yz + wx), 1 - 2 * (xx + yy), 0,
            0, 0, 0, 1
        };

        return new Matrix4x4(matrix);
    }

    /**
     * Returns the quaternion after being applied a rotation of angle radians around the axis
     * @param axis
     * The axis at which the rotation is applied
     * @param angle
     * The amount of rotation in radians to apply
     * @return
     * The quaternion after applying the rotation around the axis
     */
    public Quaternion RotateAxis(Vector3D axis, float angle)
    {
        return Multiply(Quaternion.FromAxisAngle(axis, angle), this) ;
    }

    /**
     * Rotates given a vector and a scale
     * @param vector
     * The vector  defining the rotation
     * @param scale
     * The scale
     * @return
     * The quaternion resulting of the rotation
     */
    public Quaternion RotateByScaledVector(Vector3D vector, float scale)
    {
        Quaternion q = new Quaternion(0, vector.x * scale, vector.y * scale, vector.z * scale);

        return this.Multiply(q);
    }

    /**
     * Returns a normalized version of the quaternion
     * @return
     * The normalized quaternion
     */
    public Quaternion Normalized()
    {
        float magnitudeSquared = MagnitudeSquared();

        if (magnitudeSquared < 1e-6 || Math.abs(magnitudeSquared - 1.0f) < 1e-6)
            return this;

        float magnitude = (float) Math.sqrt(magnitudeSquared);
        return new Quaternion(this.w / magnitude, this.x / magnitude, this.y / magnitude, this.z / magnitude);
    }

    //------Identity Quaternion-----

    /**
     * Quaternion with no rotation
     */
    public static final Quaternion Identity = new Quaternion(1.0f, 0.0f , 0.0f , 0.0f);

    @Override
    public String toString()
    {
        return "(" + w + ", " + x + ", " + y + ", " + z + ")";
    }
}
