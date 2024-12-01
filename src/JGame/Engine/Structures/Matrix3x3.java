package JGame.Engine.Structures;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * A class representing a 3x3 Matrix
 */
public class Matrix3x3
{
    /**
     * The values of the matrix
     */
    public final float[] values = new float[9];

    /**
     * Creates a new Matrix with all elements equal to 0
     */
    public Matrix3x3()
    {
        FillMatrix(0.0f);
    }

    /**
     * Creates a copy of a matrix
     * @param matrix
     * The matrix to copy
     */
    public Matrix3x3(Matrix3x3 matrix)
    {
        System.arraycopy(matrix.values, 0, values, 0, 3);
    }

    /**
     * Creates a matrix based on a 2D float array
     * @param values
     * The 2D float array to use as a base
     */
    public Matrix3x3(float[] values)
    {
        int size = values.length;
        if (size != 9)
        {
            throw new IllegalArgumentException("Input Matrix should have 9 elements! Element Count: " + size);
        }

        System.arraycopy(values, 0, this.values, 0, size);
    }

    public float GetValue(int x, int y)
    {
        return values[x * 3 + y];
    }

    public void SetValue(int x, int y, float value)
    {
        values[x * 3 + y] = value;
    }

    /**
     * Fills the entire matrix with a single value
     * @param value
     * The value to be filled
     */
    public void FillMatrix(float value)
    {
        Arrays.fill(values, value);
    }

    /**
     * Returns the transposed matrix
     * @return
     * The transposed matrix
     */
    public Matrix3x3 Transpose()
    {
        Matrix3x3 transposedMatrix = new Matrix3x3();

        for (int x = 0; x < 3; x++)
        {
            for (int y = 0; y < 3; y++)
            {
                transposedMatrix.values[x * 3 + y] = values[y * 3 + x];
            }
        }

        return transposedMatrix;
    }

    /**
     * Returns the inverse of the matrix
     * @return
     * The inverse of the matrix
     */
    public Matrix3x3 Inverse()
    {
        float determinant = Determinant();
        if (determinant == 0)
        {
            throw new IllegalArgumentException("Matrix is not invertible.");
        }
        float invDet = 1.0f / determinant;

        Matrix3x3 inverse = new Matrix3x3();
        float[] inverseValues = inverse.values;

        inverseValues[0] = (values[4] * values[8] - values[5] * values[7]) * invDet;
        inverseValues[1] = (values[2] * values[7] - values[1] * values[8]) * invDet;
        inverseValues[2] = (values[1] * values[5] - values[2] * values[4]) * invDet;

        inverseValues[3] = (values[5] * values[6] - values[3] * values[8]) * invDet;
        inverseValues[4] = (values[0] * values[8] - values[2] * values[6]) * invDet;
        inverseValues[5] = (values[2] * values[3] - values[0] * values[5]) * invDet;

        inverseValues[6] = (values[3] * values[7] - values[4] * values[6]) * invDet;
        inverseValues[7] = (values[1] * values[6] - values[0] * values[7]) * invDet;
        inverseValues[8] = (values[0] * values[4] - values[1] * values[3]) * invDet;

        return inverse;
    }


    /**
     * Multiplies two matrices
     * @param a
     * Matrix a
     * @param b
     * Matrix b
     * @return
     * The result of the Multiplication
     */
    public static Matrix3x3 Multiply(Matrix3x3 a, Matrix3x3 b)
    {
        Matrix3x3 result = new Matrix3x3();

        for (int x = 0; x < 3; x++)
        {
            for (int y = 0; y < 3; y++)
            {
                float value = 0;
                for (int k = 0; k < 3; k++)
                {
                    value += a.values[x * 3 + k] * b.values[k * 3 + y];
                }
                result.values[x * 3 + y] = value;
            }
        }

        return result;
    }

    /**
     * Multiplies this matrix with another
     * @param other
     * The other matrix
     * @return
     * The result of the matrix multiplication
     */
    public Matrix3x3 Multiply(Matrix3x3 other)
    {
        return Multiply(this, other);
    }

    /**
     * Multiplies a Matrix3x3 and a Vector
     * @param mat
     * Matrix to multiply
     * @param vec
     * Vector to multiply
     * @return
     * The result of the matrix-vector multiplication
     */
    public static Vector3D Multiply(Matrix3x3 mat, Vector3D vec)
    {
        float[] values = mat.values;

        float x = values[0] * vec.x + values[1] * vec.y + values[2] * vec.z;
        float y = values[3] * vec.x + values[4] * vec.y + values[5] * vec.z;
        float z = values[6] * vec.x + values[7] * vec.y + values[8] * vec.z;

        return new Vector3D(x, y, z);
    }

    /**
     * Multiplies this matrix with a vector
     * @param vec
     * The vector to multiply
     * @return
     * The result of the matrix-vector multiplication
     */
    public Vector3D Multiply(Vector3D vec)
    {
        return Multiply(this, vec);
    }

    /**
     * Calculates the determinant of the matrix
     * @return
     * The determinant of the matrix
     */
    public float Determinant()
    {
        float[] values = this.values;

        return values[0] * (values[4] * values[8] - values[5] * values[7]) -
                values[1] * (values[3] * values[8] - values[5] * values[6]) +
                values[2] * (values[3] * values[7] - values[4] * values[6]);
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        for (int y = 0; y < 3; y++)
        {
            result.append("[");
            for (int x = 0; x < 3; x++)
            {
                result.append(values[x * 3 + y]).append(" ");
            }
            result.setLength(result.length() - 1); // Remove last space
            result.append("]\n");
        }
        return result.toString();
    }

    /**
     * Identity matrix
     */
    public static Matrix3x3 Identity()
    {
        return new Matrix3x3
        (
            new float[]
            {
                1f, 0f, 0f,
                0f, 1f, 0f,
                0f, 0f, 1f
            }
        );
    }

    /**
     * Returns the 1D array representation of the matrix
     * @return
     * The 1D Array representation of the matrix
     */
    public float[] ToArray()
    {
        return Arrays.copyOf(values, 9);
    }

}
