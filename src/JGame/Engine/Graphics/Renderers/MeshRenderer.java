package JGame.Engine.Graphics.Renderers;

import JGame.Engine.Basic.JGameObject;
import JGame.Engine.Graphics.Lighting.Light;
import JGame.Engine.Graphics.Misc.Shader;
import JGame.Engine.Graphics.Models.Mesh;
import JGame.Engine.Graphics.Misc.Camera;
import JGame.Engine.Graphics.Misc.Material;
import JGame.Engine.Interfaces.Graphics.ILightHandler;
import JGame.Engine.Settings;
import JGame.Engine.Structures.Matrix4x4;
import JGame.Engine.Structures.Vector3D;

import javax.naming.InitialContext;

import static org.lwjgl.opengl.GL46.*;

/**
 * This component allows an object to be rendered in screen using a mesh and a material
 */
public class MeshRenderer extends Renderer implements ILightHandler
{
    private final Light[] currentLights = new Light[Settings.Lighting.MAX_LIGHTS];

    public Mesh mesh;
    public Material material = Material.Default();
    public boolean castShadows = true;

    @Override
    public void Initialize()
    {
        super.Initialize();
        material = new Material(material);
    }

    /**
     * Calls the Override render function in all the renderers present in the Renderers list
     */
    public static void RenderOverrideAllMeshRenderers(Shader shader, boolean ignoreLight, BackfaceCullingOverride backfaceCullingOverride, Matrix4x4[] viewMatrix, Matrix4x4 perspectiveMatrix, Vector3D eyePosition)
    {
        for(Renderer renderer : allRenderers)
        {
            if(renderer instanceof MeshRenderer meshRenderer)
                meshRenderer.RenderOverride(shader, ignoreLight, backfaceCullingOverride, viewMatrix, perspectiveMatrix, eyePosition);
        }
    }

    @Override
    public Light[] GetLights()
    {
        return currentLights;
    }

    public enum BackfaceCullingOverride
    {
        OVERRIDE_CULL,
        OVERRIDE_DONT_CULL,
        DEFAULT
    }

    @Override
    public void Render()
    {
        RenderOverride(material.shader);
    }

    public void RenderOverride(Shader shader)
    {
        RenderOverride(shader, false, BackfaceCullingOverride.DEFAULT, new Matrix4x4[]{Camera.GetViewMatrix()}, Camera.GetProjectionMatrix(), Camera.Main.Transform().GetGlobalPosition());
    }

    public void RenderOverride(Shader shader, boolean ignoreLight, BackfaceCullingOverride cullingOverride, Matrix4x4[] viewMatrix, Matrix4x4 projectionMatrix, Vector3D eyePosition)
    {
        if(!IsAvailable())
            return;

        shader.Bind();

        shader.UpdateShaderMatrices
        (
                Object().Transform().GetTransformationMatrix(),
                Object().Transform().GetGlobalRotation().ToRotationMatrix(),
                viewMatrix,
                projectionMatrix
        );

        shader.SetUniformProperty("viewPosition", eyePosition, true);

        if(!ignoreLight)
        {
            UpdateLightData(shader);
        }

        glBindVertexArray(mesh.VAO);
        EnableVertexAttribArrays();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mesh.buffers[Mesh.IBO]);


        if(!isBackfaceCullingEnabled && (cullingOverride == BackfaceCullingOverride.OVERRIDE_CULL || material.cullBackFaces))
        {
            glEnable(GL_CULL_FACE);
            isBackfaceCullingEnabled = true;
        }
        else if(isBackfaceCullingEnabled && (cullingOverride == BackfaceCullingOverride.OVERRIDE_DONT_CULL || !material.cullBackFaces))
        {
            glDisable(GL_CULL_FACE);
            isBackfaceCullingEnabled = false;
        }

        BindLightShadowmaps();
        glDrawElements(GL_TRIANGLES, mesh.tris.length * 3, GL_UNSIGNED_INT, 0);
        UnbindLightShadowmaps();
        shader.Unbind();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        DisableVertexAttribArrays();
        glBindVertexArray(0);
    }

    /**
     * Enables the Vertex Attribute Arrays corresponding to the vertex position and vertex colors
     */
    private void EnableVertexAttribArrays()
    {
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);
    }
    /**
     * Disables the Vertex Attribute Arrays corresponding to the vertex position and vertex colors
     */
    private void DisableVertexAttribArrays()
    {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
    }

    @Override
    protected void OnDestroy()
    {
        mesh.Destroy();
        material.Destroy();
    }
}
