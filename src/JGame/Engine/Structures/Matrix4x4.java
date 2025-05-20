package JGame.Engine.Structures;

import java.util.Arrays;

/**
 * A class representing a 4x4 Matrix
 */
public class Matrix4x4
{
    /**
     * The values of the matrix
     */
    public final float[] values = new float[16];

    /**
     * Creates a new Matrix with all elements equal to 0
     */
    public Matrix4x4()
    {
        FillMatrix(0.0f);
    }

    /**
     * Creates a copy of a matrix
     * @param matrix
     * The matrix to copy
     */
    public Matrix4x4(Matrix4x4 matrix)
    {
        System.arraycopy(matrix.values, 0, values, 0, 4);
    }

    /**
     * Creates a matrix based on a 2D float array
     * @param values
     * The 2D float array to use as a base
     */
    public Matrix4x4(float[] values)
    {
        int size = values.length;
        if(size != 16)
        {
            throw new IllegalArgumentException("Input Matrix should be 16 elements! Element Count: " + size);
        }

        System.arraycopy(values, 0, this.values, 0, size);
    }

    public float GetValue(int x, int y)
    {
        return values[x * 4 + y];
    }
    public void SetValue(int x, int y, float value)
    {
        values[x * 4 + y] = value;
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
    public Matrix4x4 Transpose()
    {
        Matrix4x4 transposedMatrix = new Matrix4x4();

        for (int x = 0; x < 4; x++)
        {
            for (int y = 0; y < 4; y++)
            {
                transposedMatrix.values[x * 4 + y] = values[y * 4 + x];
            }
        }

        return transposedMatrix;
    }

    /**
     * Returns a downgraded version of the matrix with all components outside the main 3x3 area being 0
     * @return
     * The downgraded version of the matrix
     */
    public Matrix4x4 Downgrade3x3()
    {
        Matrix4x4 downgradedMatrix = new Matrix4x4();

        for (int x = 0; x < 3; x++)
        {
            System.arraycopy(values, x * 4, downgradedMatrix.values, x * 4, 3);
        }

        return downgradedMatrix;
    }

    /**
     * Returns the inverse of the matrix
     * @return
     * The inverse of the matrix
     */
    public Matrix4x4 Inverse()
    {
        float[] inv = new float[16];
        float det;
        float[] mat = this.ToArray();

        inv[0] = mat[5]  * mat[10] * mat[15] -
                mat[5]  * mat[11] * mat[14] -
                mat[9]  * mat[6]  * mat[15] +
                mat[9]  * mat[7]  * mat[14] +
                mat[13] * mat[6]  * mat[11] -
                mat[13] * mat[7]  * mat[10];

        inv[4] = -mat[4]  * mat[10] * mat[15] +
                mat[4]  * mat[11] * mat[14] +
                mat[8]  * mat[6]  * mat[15] -
                mat[8]  * mat[7]  * mat[14] -
                mat[12] * mat[6]  * mat[11] +
                mat[12] * mat[7]  * mat[10];

        inv[8] = mat[4]  * mat[9] * mat[15] -
                mat[4]  * mat[11] * mat[13] -
                mat[8]  * mat[5] * mat[15] +
                mat[8]  * mat[7] * mat[13] +
                mat[12] * mat[5] * mat[11] -
                mat[12] * mat[7] * mat[9];

        inv[12] = -mat[4]  * mat[9] * mat[14] +
                mat[4]  * mat[10] * mat[13] +
                mat[8]  * mat[5] * mat[14] -
                mat[8]  * mat[6] * mat[13] -
                mat[12] * mat[5] * mat[10] +
                mat[12] * mat[6] * mat[9];

        inv[1] = -mat[1]  * mat[10] * mat[15] +
                mat[1]  * mat[11] * mat[14] +
                mat[9]  * mat[2] * mat[15] -
                mat[9]  * mat[3] * mat[14] -
                mat[13] * mat[2] * mat[11] +
                mat[13] * mat[3] * mat[10];

        inv[5] = mat[0]  * mat[10] * mat[15] -
                mat[0]  * mat[11] * mat[14] -
                mat[8]  * mat[2] * mat[15] +
                mat[8]  * mat[3] * mat[14] +
                mat[12] * mat[2] * mat[11] -
                mat[12] * mat[3] * mat[10];

        inv[9] = -mat[0]  * mat[9] * mat[15] +
                mat[0]  * mat[11] * mat[13] +
                mat[8]  * mat[1] * mat[15] -
                mat[8]  * mat[3] * mat[13] -
                mat[12] * mat[1] * mat[11] +
                mat[12] * mat[3] * mat[9];

        inv[13] = mat[0]  * mat[9] * mat[14] -
                mat[0]  * mat[10] * mat[13] -
                mat[8]  * mat[1] * mat[14] +
                mat[8]  * mat[2] * mat[13] +
                mat[12] * mat[1] * mat[10] -
                mat[12] * mat[2] * mat[9];

        inv[2] = mat[1]  * mat[6] * mat[15] -
                mat[1]  * mat[7] * mat[14] -
                mat[5]  * mat[2] * mat[15] +
                mat[5]  * mat[3] * mat[14] +
                mat[13] * mat[2] * mat[7] -
                mat[13] * mat[3] * mat[6];

        inv[6] = -mat[0]  * mat[6] * mat[15] +
                mat[0]  * mat[7] * mat[14] +
                mat[4]  * mat[2] * mat[15] -
                mat[4]  * mat[3] * mat[14] -
                mat[12] * mat[2] * mat[7] +
                mat[12] * mat[3] * mat[6];

        inv[10] = mat[0]  * mat[5] * mat[15] -
                mat[0]  * mat[7] * mat[13] -
                mat[4]  * mat[1] * mat[15] +
                mat[4]  * mat[3] * mat[13] +
                mat[12] * mat[1] * mat[7] -
                mat[12] * mat[3] * mat[5];

        inv[14] = -mat[0]  * mat[5] * mat[14] +
                mat[0]  * mat[6] * mat[13] +
                mat[4]  * mat[1] * mat[14] -
                mat[4]  * mat[2] * mat[13] -
                mat[12] * mat[1] * mat[6] +
                mat[12] * mat[2] * mat[5];

        inv[3] = -mat[1] * mat[6] * mat[11] +
                mat[1] * mat[7] * mat[10] +
                mat[5] * mat[2] * mat[11] -
                mat[5] * mat[3] * mat[10] -
                mat[9] * mat[2] * mat[7] +
                mat[9] * mat[3] * mat[6];

        inv[7] = mat[0] * mat[6] * mat[11] -
                mat[0] * mat[7] * mat[10] -
                mat[4] * mat[2] * mat[11] +
                mat[4] * mat[3] * mat[10] +
                mat[8] * mat[2] * mat[7] -
                mat[8] * mat[3] * mat[6];

        inv[11] = -mat[0] * mat[5] * mat[11] +
                mat[0] * mat[7] * mat[9] +
                mat[4] * mat[1] * mat[11] -
                mat[4] * mat[3] * mat[9] -
                mat[8] * mat[1] * mat[7] +
                mat[8] * mat[3] * mat[5];

        inv[15] = mat[0] * mat[5] * mat[10] -
                mat[0] * mat[6] * mat[9] -
                mat[4] * mat[1] * mat[10] +
                mat[4] * mat[2] * mat[9] +
                mat[8] * mat[1] * mat[6] -
                mat[8] * mat[2] * mat[5];

        det = mat[0] * inv[0] + mat[1] * inv[4] + mat[2] * inv[8] + mat[3] * inv[12];

        if (det == 0)
            throw new IllegalArgumentException("Matrix is not invertible.");

        det = 1.0f / det;

        for (int i = 0; i < 16; i++)
        {
            inv[i] = inv[i] * det;
        }

        return new Matrix4x4(inv);
    }

    /**
     * Gets the matrix with all its components being absolute values
     * @return
     * The absolute matrix
     */
    public Matrix4x4 Absolute()
    {
        float[] values = new float[16];

        for(int i = 0; i < 16; i++)
        {
            values[i] = Math.abs(this.values[i]);
        }

        return new Matrix4x4(values);
    }

    /**
     * Performs multiplication with a 4D Vector
     * @param vec
     * 4D Vector
     * @return
     * The multiplied result
     */
    public float[] Multiply(float[] vec)
    {
        if (vec.length != 4)
        {
            throw new IllegalArgumentException("Input vector must have 4 components.");
        }

        float[] result = new float[4];
        for (int row = 0; row < 4; row++)
        {
            result[row] = values[row * 4 + 0] * vec[0] +
                    values[row * 4 + 1] * vec[1] +
                    values[row * 4 + 2] * vec[2] +
                    values[row * 4 + 3] * vec[3];
        }
        return result;
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
    public static Matrix4x4 Multiply(Matrix4x4 a, Matrix4x4 b)
    {
        Matrix4x4 result = new Matrix4x4();

        for (int x = 0; x < 4; x++)
        {
            for (int y = 0; y < 4; y++)
            {
                float value = 0;
                for (int k = 0; k < 4; k++)
                {
                    value += a.values[x * 4 + k] * b.values[k * 4 + y];
                }
                result.values[x * 4 + y] = value;
            }
        }

        return result;
    }
    /**
     * Multiplies two matrices
     * @param other
     * The other matrix
     * @return
     * The result of the Multiplication
     */
    public Matrix4x4 Multiply(Matrix4x4 other)
    {
        return Multiply(this, other);
    }
    /**
     * Multiplies a Matrix4x4 and a Vector
     * @param mat
     * Matrix to multiply
     * @param vec
     * Vector to multiply
     * @return
     * The result of the matrix-vector multiplication
     */
    public static Vector3D Multiply(Matrix4x4 mat, Vector3D vec)
    {
        float[] values = mat.values;

        float x = vec.x;
        float y = vec.y;
        float z = vec.z;
        float w = 1.0f;

        float newX = values[0] * x + values[1] * y + values[2] * z + values[3] * w;
        float newY = values[4] * x + values[5] * y + values[6] * z + values[7] * w;
        float newZ = values[8] * x + values[9] * y + values[10] * z + values[11] * w;
        float newW = values[12] * x + values[13] * y + values[14] * z + values[15] * w;

        if (newW != 0)
        {
            newX /= newW;
            newY /= newW;
            newZ /= newW;
        }

        return new Vector3D(newX, newY, newZ);
    }
    /**
     * Multiplies this matrix with a vector
     * @param vector The vector
     * @return The result of the matrix-vector multiplication
     */
    public Vector3D Multiply(Vector3D vector)
    {
        return Multiply(this, vector);
    }
    /**
     * Generates and returns a transformation matrix based on the different parameters
     * @param translation
     * The translation vector for the transformation
     * @param scale
     * The scale vector for the transformation
     * @param rotation
     * The rotation quaternion for the transformation
     * @return
     * The resulting transformation matrix
     */
    public static Matrix4x4 Transformation(Vector3D translation, Vector3D scale, Quaternion rotation)
    {
        Matrix4x4 transformationMatrix = Matrix4x4.Identity();

        if(translation != null)
            transformationMatrix = transformationMatrix.Translate(translation);
        if(rotation != null)
            transformationMatrix = transformationMatrix.Rotate(rotation);
        if(scale != null)
            transformationMatrix = transformationMatrix.Scale(scale);

        return transformationMatrix;
    }

    /**
     * Generates and returns a transformation matrix based on the different parameters
     * @param translation
     * The translation vector for the transformation
     * @param scale
     * The scale vector for the transformation
     * @return
     * The resulting transformation matrix
     */
    public static Matrix4x4 Transformation(Vector3D translation, Vector3D scale)
    {
        return Transformation(translation, scale, null);
    }

    /**
     * Applies a scale transformation to the Matrix
     * @param scale
     * The scale to apply
     * @return
     * The scaled matrix
     */
    public Matrix4x4 Scale(Vector3D scale)
    {
        Matrix4x4 scaleMatrix = scale.ToScaleMatrix();
        return Matrix4x4.Multiply(this, scaleMatrix);
    }

    /**
     * Applies a translation transformation to the Matrix
     * @param translation
     * The transformation to apply
     * @return
     * The translated matrix
     */
    public Matrix4x4 Translate(Vector3D translation)
    {
        Matrix4x4 translationMatrix = translation.ToTranslationMatrix();
        return Matrix4x4.Multiply(this, translationMatrix);
    }

    /**
     * Applies a rotation transformation to the Matrix
     * @param rotation
     * The rotation to apply
     * @return
     * The rotated matrix
     */
    public Matrix4x4 Rotate(Quaternion rotation)
    {
        Matrix4x4 rotationMatrix = rotation.ToRotationMatrix();
        return Matrix4x4.Multiply(this, rotationMatrix);
    }

    /**
     * Calculates the determinant of the matrix
     * @return
     * The determinant of the matrix
     */
    public float Determinant()
    {
        return values[0] *
                (
                    values[5] * (values[10] * values[15] - values[11] * values[14]) -
                    values[6] * (values[9] * values[15] - values[11] * values[13]) +
                    values[7] * (values[9] * values[14] - values[10] * values[13])
                ) -
                values[1] *
                (
                    values[4] * (values[10] * values[15] - values[11] * values[14]) -
                    values[6] * (values[8] * values[15] - values[11] * values[12]) +
                    values[7] * (values[8] * values[14] - values[10] * values[12])
                ) +
                values[2] *
                (
                    values[4] * (values[9] * values[15] - values[11] * values[13]) -
                    values[5] * (values[8] * values[15] - values[11] * values[12]) +
                    values[7] * (values[8] * values[13] - values[9] * values[12])
                ) -
                values[3] *
                (
                    values[4] * (values[9] * values[14] - values[10] * values[13]) -
                    values[5] * (values[8] * values[14] - values[10] * values[12]) +
                    values[6] * (values[8] * values[13] - values[9] * values[12])
                );
    }


    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        for (int y = 0; y < 4; y++)
        {
            result.append("[");
            for (int x = 0; x < 4; x++)
            {
                result.append(values[x * 4 + y]).append(" ");
            }
            result.setLength(result.length() - 1); // Remove last space
            result.append("]\n");
        }
        return result.toString();
    }

    /**
     * Identity matrix
     */
    public static Matrix4x4 Identity()
    {
        return new Matrix4x4
        (
            new float[]
            {
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f
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
        return Arrays.copyOf(values, 16);
    }
}
