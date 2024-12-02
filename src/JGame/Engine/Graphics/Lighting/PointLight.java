package JGame.Engine.Graphics.Lighting;

import JGame.Engine.Graphics.Misc.Shader;
import JGame.Engine.Graphics.Renderers.MeshRenderer;
import JGame.Engine.Graphics.Renderers.Renderer;
import JGame.Engine.Graphics.Textures.BaseTexture;
import JGame.Engine.Graphics.Textures.ShadowCubeMap;
import JGame.Engine.Graphics.Textures.Texture;
import JGame.Engine.Settings;
import JGame.Engine.Structures.Matrix4x4;
import JGame.Engine.Structures.Vector3D;
import JGame.Engine.Utilities.CameraMatrixUtilities;

public class PointLight extends Light
{
    /**
     * Indicates the max range of the light
     */
    protected float range = 2.0f;
    /**
     * Controls the light's falloff strength in relation to the distance, higher values indicate slower falloff
     */
    public float falloff = 1.25f;

    @Override
    protected void Initialize()
    {
        super.Initialize();
        //Change the override shader to use the shadow cubemap shader
        OverrideShader = new Shader
                (
                        "/Shaders/Internal/ShadowCubemap/shadowCubemap_vert.glsl",
                        "/Shaders/Internal/ShadowCubemap/shadowCubemap_frag.glsl",
                        "/Shaders/Internal/ShadowCubemap/shadowCubemap_geom.glsl"
                );
        OverrideShader.SetUniformProperty("farPlane", range);
    }

    @Override
    public void InitializeShadowmap()
    {
        shadowMap = new ShadowCubeMap(Settings.Shadowmap.RESOLUTION_NON_DIRECTIONAL);
    }
    @Override
    public void RenderMeshesDepth()
    {
        Matrix4x4[] lightProjections = new Matrix4x4[lightViewMatrices.length];

        for(int i = 0; i < lightViewMatrices.length; i++)
        {
            lightProjections[i] = Matrix4x4.Multiply(lightProjectionMatrix, lightViewMatrices[i]);
        }


        for(Renderer renderer : MeshRenderer.allRenderers)
        {
            if(renderer instanceof MeshRenderer meshRenderer && meshRenderer.castShadows)
            {
                meshRenderer.RenderOverride
                (
                        OverrideShader,
                        true,
                        MeshRenderer.BackfaceCullingOverride.OVERRIDE_CULL,
                        lightProjections,
                        lightProjectionMatrix,
                        transform().GetGlobalPosition()
                );
            }
        }
    }

    @Override
    public void UpdateLightValues(Shader shader, int index)
    {
        super.UpdateLightValues(shader, index);
        String lightIndex = "lights[" + index + "]";

        shader.SetUniformProperty(lightIndex + ".position", transform().GetGlobalPosition(), true);
        shader.SetUniformProperty(lightIndex + ".range", range, true);
        shader.SetUniformProperty(lightIndex + ".falloff", falloff, true);
        shader.SetUniformProperty(lightIndex + ".type", LIGHT_TYPE_POINT, true);
    }

    @Override
    protected Matrix4x4 CalculateProjectionMatrix()
    {
        return CameraMatrixUtilities.PerspectiveProjection(90f, 1, range, Settings.Shadowmap.POINTLIGHT_NEAR_PLANE);
    }

    @Override
    protected Matrix4x4[] CalculateViewMatrices()
    {
        return new Matrix4x4[]
        {
                CameraMatrixUtilities.LookAt(transform().GetGlobalPosition(), Vector3D.Right, Vector3D.Down),
                CameraMatrixUtilities.LookAt(transform().GetGlobalPosition(), Vector3D.Left, Vector3D.Down),
                CameraMatrixUtilities.LookAt(transform().GetGlobalPosition(), Vector3D.Up, Vector3D.Backward),
                CameraMatrixUtilities.LookAt(transform().GetGlobalPosition(), Vector3D.Down, Vector3D.Backward),
                CameraMatrixUtilities.LookAt(transform().GetGlobalPosition(), Vector3D.Forward, Vector3D.Down),
                CameraMatrixUtilities.LookAt(transform().GetGlobalPosition(), Vector3D.Backward, Vector3D.Down),
        };
    }

    public void SetRange(float range)
    {
        this.range = range;
        OverrideShader.SetUniformProperty("farPlane", range);
    }

    public boolean IsInRange(Vector3D targetPos)
    {
        return Vector3D.DistanceSquared(targetPos, transform().GetGlobalPosition()) <= (range * 25f) * (range * 25f);
    }


    @Override
    protected Texture GetIcon()
    {
        return new Texture
                (
                  "/Textures/Internal/Icons/pointLight_icon.png",
                        BaseTexture.TextureType.TEXTURE_2D,
                        BaseTexture.TextureFilteringType.NEAREST,
                        BaseTexture.TextureWrapMode.CLAMP
                );
    }
}
