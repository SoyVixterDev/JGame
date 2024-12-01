package JGame.Engine.Interfaces.Graphics;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL46.*;

public interface IMeshOpenGLHandler
{
    void GenerateVAO();
    /**
     * Generates a buffer for GL46 using the provided buffer target and usage
     * @param buffer
     * The buffer used for the generation and linking
     * @param target
     * The target buffer, EX: GL_ARRAY_BUFFER
     * @param usage
     * The usage type for the buffer, EX: GL_STATIC_DRAW
     * @param <T>
     * The type of the buffer, either FloatBuffer or IntBuffer.
     * @return
     * The ID of the generated buffer
     */
    default <T> int LinkBufferToAttribute(T buffer, int target, int usage)
    {
        return LinkBufferToAttribute(buffer, target, usage, -1, -1);
    }


    /**
     * Generates a buffer for GL46 using the provided buffer target and usage.
     * @param buffer
     * The buffer used for the generation and linking.
     * @param target
     * The target buffer, EX: GL_ARRAY_BUFFER.
     * @param usage
     * The usage type for the buffer, EX: GL_STATIC_DRAW.
     * @param index
     * The vertex attribute pointer's index. Pass -1 if not needed.
     * @param size
     * The vertex attribute pointer's size. Pass -1 if not needed.
     * @param <T>
     * The type of the buffer, either FloatBuffer or IntBuffer.
     * @return
     * The ID of the generated buffer.
     */
    default <T> int LinkBufferToAttribute(T buffer, int target, int usage, int index, int size)
    {
        int bufferID = glGenBuffers();
        glBindBuffer(target, bufferID);
        if (buffer instanceof FloatBuffer floatBuffer)
        {
            glBufferData(target, floatBuffer, usage);
            if (index >= 0 && size >= 0)
            {
                glVertexAttribPointer(index, size, GL_FLOAT, false, 0, 0);
            }
        }
        else if (buffer instanceof IntBuffer intBuffer)
        {
            glBufferData(target, intBuffer, usage);
            if (index >= 0 && size >= 0)
            {
                glVertexAttribPointer(index, size, GL_INT, false, 0, 0);
            }
        }
        else
        {
            throw new IllegalArgumentException("Unsupported buffer type: " + buffer.getClass().getName());
        }

        glBindBuffer(target, 0);

        return bufferID;
    }
}
