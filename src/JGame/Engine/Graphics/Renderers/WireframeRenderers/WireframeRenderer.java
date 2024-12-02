package JGame.Engine.Graphics.Renderers.WireframeRenderers;

import JGame.Engine.Graphics.Misc.Camera;
import JGame.Engine.Graphics.Misc.Shader;
import JGame.Engine.Graphics.Models.Mesh;
import JGame.Engine.Graphics.Renderers.Renderer;
import JGame.Engine.Structures.ColorRGBA;

import static org.lwjgl.opengl.GL46.*;


public class WireframeRenderer extends Renderer
{
    private ColorRGBA color = ColorRGBA.Green;
    public Mesh mesh = Mesh.Cube();

    private final Shader shader = new Shader
    (
        "/Shaders/Internal/Wireframe/wireframeVertShader.glsl",
        "/Shaders/Internal/Wireframe/wireframeFragShader.glsl"
    );

    @Override
    protected void Initialize()
    {
        super.Initialize();
        SetColor(color);
    }

    public void SetColor(ColorRGBA color)
    {
        this.color = color;
        shader.SetUniformProperty("wireframeColor", color);
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

        glBindVertexArray(mesh.VAO);
        glEnableVertexAttribArray(0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mesh.buffers[Mesh.IBO]);

        if(isBackfaceCullingEnabled)
        {
            glDisable(GL_CULL_FACE);
            isBackfaceCullingEnabled = false;
        }

        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glLineWidth(3.0f);


        glDrawElements(GL_TRIANGLES, mesh.tris.length * 3, GL_UNSIGNED_INT, 0);
        shader.Unbind();

        glLineWidth(1.0f);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }

    @Override
    protected void OnDestroy()
    {
        shader.Destroy();
        mesh.Destroy();
        super.OnDestroy();
    }
}
