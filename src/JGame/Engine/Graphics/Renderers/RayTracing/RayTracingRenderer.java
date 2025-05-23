package JGame.Engine.Graphics.Renderers.RayTracing;

import JGame.Application.Window;
import JGame.Engine.Basic.JComponent;
import JGame.Engine.EventSystem.Event;
import JGame.Engine.Graphics.Lighting.DirectionalLight;
import JGame.Engine.Graphics.Misc.Camera;
import JGame.Engine.Graphics.Misc.RayTracingMaterial;
import JGame.Engine.Graphics.Misc.Shader;
import JGame.Engine.Graphics.Models.Mesh;
import JGame.Engine.Graphics.Models.Triangle;
import JGame.Engine.Graphics.Models.Vertex;
import JGame.Engine.Graphics.Textures.BaseTexture;
import JGame.Engine.Graphics.Textures.CubemapTexture;
import JGame.Engine.Graphics.Textures.FrameBufferTexture;
import JGame.Engine.Internal.Time;
import JGame.Engine.Structures.*;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL46.*;

/**
 * Base class for all renderers using RayTracing
 */
public abstract class RayTracingRenderer extends JComponent
{
    public RayTracingMaterial material = RayTracingMaterial.Default();

    private static FrameBufferTexture currentFrame;
    private static FrameBufferTexture currentRadiance;
    private static FrameBufferTexture previousFrame;

    private static long frameCount = 0;

    private static CubemapTexture skyboxTexture = new CubemapTexture
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
        );

    private static final Event WindowResizedEvent = new Event()
    {
        @Override
        protected void OnInvoke()
        {
            InitTex();
        }
    };

    protected static final Shader rayTracingShader = new Shader("/Shaders/Internal/Misc/fullscreen_vert.glsl", "/Shaders/Internal/RayTracing/Rendering/ray_tracing_render_frag.glsl");
    protected static final Shader accumShader = new Shader("/Shaders/Internal/Misc/fullscreen_vert.glsl", "/Shaders/Internal/RayTracing/Accumulation/accumulation_frag.glsl");
    protected static final Shader blitShader = new Shader("/Shaders/Internal/Misc/fullscreen_vert.glsl", "/Shaders/Internal/RayTracing/Blit/ray_tracing_blit_frag.glsl");

    static
    {
        accumShader.Bind();
        accumShader.SetUniformProperty("lastAccumTex", 0, true);
        accumShader.SetUniformProperty("currentRadiance", 1, true);
        accumShader.Unbind();

        blitShader.SetUniformProperty("screenTex", 0);

        rayTracingShader.SetUniformProperty("skyboxTex", 0);
    }

    private static void InitTex()
    {
        Vector2D size = Window.GetWindowSize();

        if (currentFrame != null) currentFrame.Destroy();
        if (currentRadiance != null) currentRadiance.Destroy();
        if (previousFrame != null) previousFrame.Destroy();

        currentFrame = new FrameBufferTexture((int) size.x, (int) size.y);
        currentRadiance = new FrameBufferTexture((int) size.x, (int) size.y);
        previousFrame = new FrameBufferTexture((int) size.x, (int) size.y);

        ResetAccumulation();
    }

    protected static final Mesh Quad = new Mesh(
        new Vertex[]
        {
            new Vertex(new Vector3D(-1, -1, 0), new Vector2D(0, 1)),
            new Vertex(new Vector3D( 1, -1, 0), new Vector2D(1, 1)),
            new Vertex(new Vector3D( 1,  1, 0), new Vector2D(1, 0)),
            new Vertex(new Vector3D(-1,  1, 0), new Vector2D(0, 0))
        },
        new Triangle[]
        {
            new Triangle(0, 1, 2),
            new Triangle(2, 3, 0)
        }
    );

    /**
     * Enables the Vertex Attribute Arrays corresponding to the vertex position and vertex colors
     */
    private static void EnableVertexAttribArrays()
    {
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(2);
    }
    /**
     * Disables the Vertex Attribute Arrays corresponding to the vertex position and vertex colors
     */
    private static void DisableVertexAttribArrays()
    {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(2);
    }

    private static final int spheresBuffer = glGenBuffers();
    private static final int boxesBuffer = glGenBuffers();
    private static final int dirLightBuffer = glGenBuffers();

    private static void PutMaterial(FloatBuffer floatBuffer, RayTracingMaterial material)
    {
        var color = material.color;
        var specularColor = material.specularColor;

        floatBuffer.put(color.r).put(color.g).put(color.b).put(color.a);
        floatBuffer.put(specularColor.r).put(specularColor.g).put(specularColor.b);
        floatBuffer.put(material.specularity);
        floatBuffer.put(material.smoothness);
        floatBuffer.put(material.emissivity);
        floatBuffer.put(0.0f);
        floatBuffer.put(0.0f);
    }

    private static void UpdateCubeData()
    {
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer( RayTracedBoxRenderer.allRayTracedBoxes.size() * 24);

        for(RayTracedBoxRenderer boxRenderer : RayTracedBoxRenderer.allRayTracedBoxes)
        {
            Vector3D position = boxRenderer.transform().GetGlobalPosition();
            Vector3D scaledSize = boxRenderer.halfSize.Multiply(boxRenderer.transform().GetGlobalScale());
            Quaternion rotation = boxRenderer.transform().GetGlobalRotation();

            floatBuffer.put(position.x).put(position.y).put(position.z);
            floatBuffer.put(0.0f);
            floatBuffer.put(scaledSize.x).put(scaledSize.y).put(scaledSize.z);
            floatBuffer.put(0.0f);
            floatBuffer.put(rotation.x).put(rotation.y).put(rotation.z).put(rotation.w);

            PutMaterial(floatBuffer, boxRenderer.material);
        }

        floatBuffer.flip();

        glBindBuffer(GL_SHADER_STORAGE_BUFFER, boxesBuffer);
        glBufferData(GL_SHADER_STORAGE_BUFFER, floatBuffer, GL_STATIC_DRAW);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 2, boxesBuffer);

        rayTracingShader.SetUniformProperty("boxCount", RayTracedBoxRenderer.allRayTracedBoxes.size(), true);
    }


    private static void UpdateSphereData()
    {
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer( RayTracedSphereRenderer.allRayTracedSpheres.size() * 16);

        for(RayTracedSphereRenderer sphereRenderer : RayTracedSphereRenderer.allRayTracedSpheres)
        {
            Vector3D position = sphereRenderer.transform().GetGlobalPosition();
            Vector3D scale = sphereRenderer.transform().GetGlobalScale();
            float largestScale = Math.max(Math.max(scale.x, scale.y), scale.z);

            floatBuffer.put(position.x).put(position.y).put(position.z);
            floatBuffer.put(sphereRenderer.radius * largestScale);

            PutMaterial(floatBuffer, sphereRenderer.material);
        }

        floatBuffer.flip();

        glBindBuffer(GL_SHADER_STORAGE_BUFFER, spheresBuffer);
        glBufferData(GL_SHADER_STORAGE_BUFFER, floatBuffer, GL_STATIC_DRAW);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, spheresBuffer);

        rayTracingShader.SetUniformProperty("sphereCount", RayTracedSphereRenderer.allRayTracedSpheres.size(), true);
    }

    public static float focusDistance = 2f;
    public static float dofStrength = 0f;
    public static float jitterStrength = 1.5f;
    /**
     * Updates the Camera parameters for the ray tracing shader
     */
    private static void UpdateCameraParams()
    {
        float tanFov = (float) Math.tan(Math.toRadians(Camera.Main.GetFov() / 2.0f));
        float viewHeight = focusDistance * tanFov * 2.0f;
        float viewWidth = viewHeight * Window.GetWindowAspectRatio();

        rayTracingShader.SetUniformProperty("CameraParams", new Vector3D(viewWidth, viewHeight, focusDistance), true);
        rayTracingShader.SetUniformProperty("ScreenSize", Window.GetWindowSize(), true);
        rayTracingShader.SetUniformProperty("CamWorldPos", Camera.Main.transform().GetGlobalPosition(), true);
        rayTracingShader.SetUniformProperty("CamTransformationMatrix", Camera.Main.transform().GetTransformationMatrix(), true);

        rayTracingShader.SetUniformProperty("dofStrength", dofStrength, true);
        rayTracingShader.SetUniformProperty("jitterStrength", jitterStrength, true);
    }

    private static void SwapBuffers()
    {
        FrameBufferTexture temp = currentFrame;
        currentFrame = previousFrame;
        previousFrame = temp;
    }

    private static void BlitFrame()
    {
        blitShader.Bind();
        currentFrame.Bind(GL_TEXTURE0);

        glBindVertexArray(Quad.VAO);
        EnableVertexAttribArrays();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, Quad.buffers[Mesh.IBO]);

        glDrawElements(GL_TRIANGLES, Quad.tris.length * 3, GL_UNSIGNED_INT, 0);

        currentFrame.Unbind();
        blitShader.Unbind();
        DisableVertexAttribArrays();
        glBindVertexArray(0);
    }

    private static DirectionalLight directionalLight;

    private static void UpdateLightingData()
    {
        if(directionalLight == null)
        {
            directionalLight = FindComponent(DirectionalLight.class);
        }

        Vector3D forward = Vector3D.Zero;
        ColorRGB color = ColorRGB.Black;
        float intensity = 0.0f;

        if(directionalLight != null)
        {
            forward = directionalLight.transform().Forward();
            color = directionalLight.color;
            intensity = directionalLight.intensity;
        }

        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(32);

        floatBuffer.put(forward.x).put(forward.y).put(forward.z);
        floatBuffer.put(intensity * 10f);
        floatBuffer.put(color.r).put(color.g).put(color.b);
        floatBuffer.put(50.0f);

        floatBuffer.flip();

        glBindBuffer(GL_SHADER_STORAGE_BUFFER, dirLightBuffer);
        glBufferData(GL_SHADER_STORAGE_BUFFER, floatBuffer, GL_STATIC_DRAW);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 3, dirLightBuffer);
    }

    public static void RunAccumulation()
    {
        currentFrame.BindFramebuffer();

        accumShader.Bind();

        accumShader.SetUniformProperty("frame", frameCount, true);

        previousFrame.Bind(GL_TEXTURE0);
        currentRadiance.Bind(GL_TEXTURE1);

        glBindVertexArray(Quad.VAO);
        EnableVertexAttribArrays();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, Quad.buffers[Mesh.IBO]);
        glDrawElements(GL_TRIANGLES, Quad.tris.length * 3, GL_UNSIGNED_INT, 0);

        currentRadiance.Unbind();
        previousFrame.Unbind();
        accumShader.Unbind();

        DisableVertexAttribArrays();
        glBindVertexArray(0);
    }

    public static float skyboxIntensity = 0.75f;

    private static void UpdateOthers()
    {
        rayTracingShader.SetUniformProperty("frame", Time.Frame(), true);
        rayTracingShader.SetUniformProperty("skyboxIntensity", skyboxIntensity, true);
    }

    /**
     * Renders the scene using the ray tracing shader
     */
    public static void RenderAll()
    {
        currentRadiance.BindFramebuffer();
        glClear(GL_COLOR_BUFFER_BIT);

        rayTracingShader.Bind();

        UpdateCameraParams();
        UpdateSphereData();
        UpdateCubeData();
        UpdateLightingData();
        UpdateOthers();

        skyboxTexture.Bind(GL_TEXTURE0);

        glBindVertexArray(Quad.VAO);
        EnableVertexAttribArrays();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, Quad.buffers[Mesh.IBO]);
        glDrawElements(GL_TRIANGLES, Quad.tris.length * 3, GL_UNSIGNED_INT, 0);

        previousFrame.Unbind();
        skyboxTexture.Unbind();
        rayTracingShader.Unbind();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        DisableVertexAttribArrays();
        glBindVertexArray(0);

        RunAccumulation();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        BlitFrame();

        SwapBuffers();

        frameCount++;
    }

    /**
     * Resets the frame accumulation
     */
    public static void ResetAccumulation()
    {
        glClearColor(0, 0, 0, 0);

        previousFrame.BindFramebuffer();
        glClear(GL_COLOR_BUFFER_BIT);

        currentFrame.BindFramebuffer();
        glClear(GL_COLOR_BUFFER_BIT);

        frameCount = 0;

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public static void InitializeRenderer()
    {
        Window.OnChangeWindowSize.Subscribe(WindowResizedEvent);
        InitTex();
    }

    public static void CleanupRenderer()
    {
        Window.OnChangeWindowSize.Unsubscribe(WindowResizedEvent);
    }
}
