package JGame.Engine.Graphics.Renderers;

import JGame.Engine.Graphics.Misc.Camera;
import JGame.Engine.Graphics.Misc.Shader;
import JGame.Engine.Graphics.Models.Mesh;
import JGame.Engine.Graphics.Textures.BaseTexture;
import JGame.Engine.Graphics.Textures.Texture;
import JGame.Engine.Structures.Matrix4x4;

import static org.lwjgl.opengl.GL46.*;

/**
 * Class that handles 2D images into the 3D world that align with the camera view
 */
public class BillboardRenderer extends Renderer
{

    private final Mesh mesh = Mesh.Quad(true);
    private final Shader shader = new Shader
    (
            "/Shaders/Internal/Billboard/billboardVertShader.glsl",
            "/Shaders/Internal/Billboard/billboardFragShader.glsl",
            new Texture("/Textures/Internal/white.png")
    );

    @Override
    protected void Initialize()
    {
        super.Initialize();
        opaque = false;
    }

    @Override
    protected void Render()
    {
        shader.Bind();

        Matrix4x4 ST_Mat = new Matrix4x4(Matrix4x4.Identity());
        Matrix4x4 T_Mat = object().transform().GetTransformationMatrix();

        ST_Mat.values[3] = T_Mat.values[3];
        ST_Mat.values[7] = T_Mat.values[7];
        ST_Mat.values[11] = T_Mat.values[11];

        ST_Mat.values[15] = T_Mat.values[15];

        ST_Mat.values[12] = T_Mat.values[12];
        ST_Mat.values[13] = T_Mat.values[13];
        ST_Mat.values[14] = T_Mat.values[14];

        shader.UpdateShaderMatrices
        (
                ST_Mat,
                null,
                Camera.GetViewMatrix(),
                Camera.GetProjectionMatrix()
        );

        glBindVertexArray(mesh.VAO);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(2);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mesh.buffers[Mesh.IBO]);

        if(isBackfaceCullingEnabled)
        {
            glDisable(GL_CULL_FACE);
            isBackfaceCullingEnabled = false;
        }

        glDrawElements(GL_TRIANGLES, mesh.tris.length * 3, GL_UNSIGNED_INT, 0);
        shader.Unbind();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);

    }

    /**
     * Sets the image displayed by the billboard
     * @param image
     * The image to be displayed
     */
    public void SetImage(BaseTexture image)
    {
        shader.SetTexture(image);
    }
}
