package JGame.Engine.Graphics.Renderers.WireframeRenderers;

import JGame.Engine.Graphics.Misc.Camera;
import JGame.Engine.Graphics.Misc.Shader;
import JGame.Engine.Graphics.Renderers.Renderer;
import JGame.Engine.Structures.ColorRGBA;

import static org.lwjgl.opengl.GL46.*;

public abstract class WireshapeRenderer extends Renderer
{
    public float lineWidth = 4.0f;
    private final Shader shader = new Shader
    (
            "/Shaders/Internal/Wireframe/wireframeVertShader.glsl",
            "/Shaders/Internal/Wireframe/wireframeFragShader.glsl"
    );

    private int VAO, VBO, EBO;

    abstract protected float[] GetVertices();
    abstract protected int[] GetEdges();

    @Override
    protected void Initialize()
    {
        super.Initialize();

        VAO = glGenVertexArrays();
        VBO = glGenBuffers();
        EBO = glGenBuffers();

        glBindVertexArray(VAO);

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, GetVertices(), GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, GetEdges(), GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        SetColor(ColorRGBA.White);
    }

    @Override
    protected void Render()
    {
        shader.Bind();

        shader.UpdateShaderMatrices
        (
            object().transform().GetTransformationMatrix(),
            object().transform().GetGlobalRotation().ToRotationMatrix(),
            Camera.GetViewMatrix(),
            Camera.GetProjectionMatrix()
        );

        glBindVertexArray(VAO);
        glEnableVertexAttribArray(0);

        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glLineWidth(lineWidth);

        glDrawElements(GL_LINES, GetEdges().length, GL_UNSIGNED_INT, 0);

        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        shader.Unbind();
    }

    protected void UpdateVertices()
    {
        float[] vertices = GetVertices();

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    @Override
    protected void OnDestroy()
    {
        glDeleteBuffers(VBO);
        glDeleteBuffers(EBO);
        glDeleteVertexArrays(VAO);

        shader.Destroy();

        super.OnDestroy();
    }

    public void SetColor(ColorRGBA color)
    {
        shader.SetUniformProperty("wireframeColor", color);
    }

}
