package JGame.Engine.Structures;

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

        return new Quaternion(w, x, y, z);
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
     * Returns a Quaternion representation of a Vector3D of euler angles
     * @param eulerAngles
     * The euler representation of the rotation
     * @return
     * A Quaternion representation of the rotation
     */
    public static Quaternion EulerToQuaternion(Vector3D eulerAngles)
    {
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
        float magnitudeSquared = MagnitudeSquared();
        Quaternion conjugate = Conjugate();

        return new Quaternion(conjugate.w / magnitudeSquared, conjugate.x / magnitudeSquared,
                conjugate.y / magnitudeSquared, conjugate.z / magnitudeSquared);
    }

    /**
     * Returns a Vector3D containing the Euler representation of the quaternion
     * @return
     * A Vector3D containing the Euler representation of the quaternion
     */
    public Vector3D EulerAngles()
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

        return new Vector3D(roll, pitch, yaw);
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
        Vector3D normalizedAxis = axis.Normalized();

        float halfAngle = angle / 2.0f;
        float sinHalfAngle = (float) Math.sin(halfAngle);

        float qw = (float) Math.cos(halfAngle);
        float qx = normalizedAxis.x * sinHalfAngle;
        float qy = normalizedAxis.y * sinHalfAngle;
        float qz = normalizedAxis.z * sinHalfAngle;

        return Multiply(new Quaternion(qw, qx, qy, qz), this) ;
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

        if(magnitudeSquared == 0)
            return Identity;

        float magnitude = (float) Math.sqrt(magnitudeSquared);
        return new Quaternion(this.w / magnitude, this.x / magnitude, this.y / magnitude, this.z / magnitude);
    }

    //------Identity Quaternion-----

    /**
     * Quaternion with no rotation
     */
    public static final Quaternion Identity = new Quaternion(1.0f, 0.0f , 0.0f , 0.0f);
}
