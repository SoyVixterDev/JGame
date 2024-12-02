package JGame.Engine.Graphics.Renderers;

import JGame.Engine.Basic.JGameObject;
import JGame.Engine.Graphics.Models.Mesh;
import JGame.Engine.Graphics.Misc.Camera;
import JGame.Engine.Graphics.Misc.Material;
import JGame.Engine.Graphics.Textures.BaseTexture;
import JGame.Engine.Graphics.Textures.CubemapTexture;
import JGame.Engine.Interfaces.Graphics.IMeshOpenGLHandler;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL46.*;

/**
 * The skybox renderer component used for the scene, there should only be one SkyboxRenderer component active per scene
 */
public class SkyboxRenderer extends Renderer implements IMeshOpenGLHandler
{
    //Hardcoded definition for a box, because a skybox is always a box
    private static final float[] verts =
    {
       -1.0f, -1.0f,  1.0f,
        1.0f, -1.0f,  1.0f,
        1.0f, -1.0f, -1.0f,
       -1.0f, -1.0f, -1.0f,
       -1.0f,  1.0f,  1.0f,
        1.0f,  1.0f,  1.0f,
        1.0f,  1.0f, -1.0f,
       -1.0f,  1.0f, -1.0f
    };

    private static final int[] tris =
    {
        6, 2, 1,
        1, 5, 6,

        7, 4, 0,
        0, 3, 7,

        6, 5, 4,
        4, 7, 6,

        2, 3, 0,
        0, 1, 2,

        5, 1, 0,
        0, 4, 5,

        6, 7, 3,
        3, 2, 6
    };

    public Material skyboxMaterial = new Material
            (
                    "/Shaders/Internal/Skybox/skyboxVertShader.glsl",
                    "/Shaders/Internal/Skybox/skyboxFragShader.glsl",
                new CubemapTexture
                (
                        new String[]
                        {
                            "/Textures/Internal/DefaultSkybox/right.png",
                            "/Textures/Internal/DefaultSkybox/left.png",
                            "/Textures/Internal/DefaultSkybox/top.png",
                            "/Textures/Internal/DefaultSkybox/bottom.png",
                            "/Textures/Internal/DefaultSkybox/front.png",
                            "/Textures/Internal/DefaultSkybox/back.png",
                        },
                        BaseTexture.TextureFilteringType.LINEAR
                )
            );

    int VAO;

    //Buffers
    final int[] buffers = new int[2];

    @Override
    protected void Initialize()
    {
        super.Initialize();
        skyboxMaterial = new Material(skyboxMaterial);

        VAO = glGenVertexArrays();
        GenerateVAO();
    }

    /**
     * Generates the VAO or Vertex Array Object for the SkyboxRenderer
     */
    @Override
    public void GenerateVAO()
    {
        glBindVertexArray(VAO);

        FloatBuffer vertPosBuffer = MemoryUtil.memAllocFloat(verts.length);
        vertPosBuffer.put(verts).flip();
        buffers[Mesh.PBO] = LinkBufferToAttribute(vertPosBuffer, GL_ARRAY_BUFFER, GL_STATIC_DRAW, 0, 3);

        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(tris.length);
        indicesBuffer.put(tris).flip();
        buffers[Mesh.IBO] = LinkBufferToAttribute(indicesBuffer, GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW);
    }

    /**
     * Called to render the skybox every frame
     */
    protected void Render()
    {
        skyboxMaterial.shader.Bind();
        UpdateSkyboxMatrices();

        glDepthFunc(GL_LEQUAL);

        glBindVertexArray(VAO);
        glEnableVertexAttribArray(0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffers[Mesh.IBO]);

        if(!isBackfaceCullingEnabled)
        {
            glEnable(GL_CULL_FACE);
            isBackfaceCullingEnabled = true;
        }

        glDrawElements(GL_TRIANGLES, tris.length, GL_UNSIGNED_INT, 0);
        skyboxMaterial.shader.Unbind();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);


        glDepthFunc(GL_LESS);
    }


    /**
     * Updates the necessary matrices for the skybox shaders
     */
    private void UpdateSkyboxMatrices()
    {
        skyboxMaterial.shader.SetUniformProperty("uView", Camera.GetViewMatrix().Downgrade3x3(),true);
        skyboxMaterial.shader.SetUniformProperty("uProjection", Camera.GetProjectionMatrix(),true);
    }

    /**
     * Destroys the different mesh buffers and VAO from OpenGL
     */
    @Override
    public void OnDestroy()
    {
        for(int buffer : buffers)
            glDeleteBuffers(buffer);

        glDeleteVertexArrays(VAO);

        super.OnDestroy();
    }
}


