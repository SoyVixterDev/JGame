package JGame.Engine.Graphics.Lighting;

import JGame.Application.Window;
import JGame.Engine.Basic.JComponent;
import JGame.Engine.Graphics.Misc.Shader;
import JGame.Engine.Graphics.Renderers.MeshRenderer;
import JGame.Engine.Graphics.Renderers.Renderer;
import JGame.Engine.Graphics.Textures.ShadowMap;
import JGame.Engine.Interfaces.Graphics.ILightHandler;
import JGame.Engine.Settings;
import JGame.Engine.Structures.ColorRGB;
import JGame.Engine.Structures.Matrix4x4;

import static org.lwjgl.opengl.GL46.*;

public abstract class Light extends JComponent
{
    public static final int LIGHT_TYPE_DIRECTIONAL = 0;
    public static final int LIGHT_TYPE_POINT = 1;
    public static final int LIGHT_TYPE_SPOT = 2;
    public static final int LIGHT_TYPE_AMBIENT = 3;

    public float intensity = 1.0f;
    public ColorRGB color = ColorRGB.White;

    public boolean emitShadows = true;
    public boolean softShadows = true;

    public float minShadowBias = 0.0005f;
    public float maxShadowBias = 0.0025f;

    @Override
    protected void Initialize()
    {
        InitializeShadowmap();
        UpdatePerspectiveMatrix();
    }

    @Override
    public final void Update()
    {
        UpdateViewMatrix();
        InternalRenderShadowmap();

        for(Renderer renderer : Renderer.allRenderers)
        {
            if(renderer instanceof ILightHandler lightHandler)
            {
                lightHandler.TryAddToCurrentLights(this);
            }
        }
    }

    /**
     * Called for every renderer that can receive lights in the scene, override this function to update the corresponding shader variables
     * for your light type and Call super.UpdateLightValues(shader, index) to update the common variables.
     */
    public void UpdateLightValues(Shader shader, int index)
    {
        String lightIndex = "lights[" + index + "]";

        shader.SetUniformProperty(lightIndex + ".color", color, true);
        shader.SetUniformProperty(lightIndex + ".intensity", intensity, true);
        shader.SetUniformProperty(lightIndex + ".softshadows", softShadows, true);
        shader.SetUniformProperty(lightIndex + ".minshadowbias", minShadowBias, true);
        shader.SetUniformProperty(lightIndex + ".maxshadowbias", maxShadowBias, true);

        if(emitShadows)
        {
            shader.SetUniformProperty(lightIndex + ".projection",
                    Matrix4x4.Multiply(lightProjectionMatrix,
                            lightViewMatrices[0]), true);
        }
    }


    //-----Shadow Casting-----
    protected static Shader OverrideShader = new Shader("/Shaders/Internal/Default/defaultVertShader.glsl", "/Shaders/Internal/DepthOnly/depthOnlyFragShader.glsl");

    public ShadowMap shadowMap;
    Matrix4x4 lightProjectionMatrix;
    Matrix4x4[] lightViewMatrices;

    /**
     * Override this function to initialize the shadow map to the light's specific conditions, defaults
     * to initializing with non-directional resolution.
     */
    public void InitializeShadowmap()
    {
        shadowMap = new ShadowMap(Settings.Shadowmap.RESOLUTION_NON_DIRECTIONAL);
    }

    protected boolean emitShadowsLFrame = emitShadows;
    protected boolean availableLFrame = IsAvailable();

    /**
     * Used internally in the light base class to call the RenderShadowmap function only when needed
     */
    private void InternalRenderShadowmap()
    {
        if(shadowMap == null)
            return;

        if(!emitShadows || !IsAvailable())
        {
            if(emitShadowsLFrame != emitShadows || availableLFrame != IsAvailable())
            {
                glViewport(0,0, shadowMap.width, shadowMap.height);
                glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.FBO);
                glClear(GL_DEPTH_BUFFER_BIT);
                glBindFramebuffer(GL_FRAMEBUFFER, 0);
            }
            availableLFrame = IsAvailable();
            emitShadowsLFrame = emitShadows;
            return;
        }


        availableLFrame = IsAvailable();
        emitShadowsLFrame = emitShadows;

        RenderShadowMap();

        Window.ResetViewport();
    }

    /**
     * Renders the scene from the light's perspective into the shadermap texture
     */
    public void RenderShadowMap()
    {
        glEnable(GL_DEPTH_TEST);

        glViewport(0,0, shadowMap.width, shadowMap.height);
        glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.FBO);
        glClear(GL_DEPTH_BUFFER_BIT);

        RenderMeshesDepth();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    /**
     * Calls the Override render function in all the renderers present in the Renderers list
     */
    public void RenderMeshesDepth()
    {
        for(Renderer renderer : MeshRenderer.allRenderers)
        {
            if(renderer instanceof MeshRenderer meshRenderer && meshRenderer.castShadows)
            {
                meshRenderer.RenderOverride
                (
                        OverrideShader,
                        true,
                        MeshRenderer.BackfaceCullingOverride.OVERRIDE_CULL,
                        lightViewMatrices,
                        lightProjectionMatrix,
                        transform().GetGlobalPosition()
                );
            }
        }
    }

    /**
     * Should be called everytime something that changes the perspective of the light gets altered
     */
    protected final void UpdatePerspectiveMatrix()
    {
        lightProjectionMatrix = CalculateProjectionMatrix();
    }

    protected final void UpdateViewMatrix()
    {
        lightViewMatrices = CalculateViewMatrices();
    }

    /**
     * Calculates the perspective matrix of the light, override to adapt to your light's parameters
     * @return
     * The perspective matrix of the light
     */
    protected abstract Matrix4x4 CalculateProjectionMatrix();

    /**
     * Calculates the View matrix of the light, override to adapt to your light's parameters
     * @return
     * The view matrix of the light
     */
    protected abstract Matrix4x4[] CalculateViewMatrices();


    @Override
    protected void OnDestroy()
    {
        shadowMap.Destroy();
        super.OnDestroy();
    }
}
