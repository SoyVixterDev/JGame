package Project;

import JGame.Application.Application;
import JGame.Application.Window;
import JGame.Engine.Basic.JGameObject;
import JGame.Engine.Graphics.Lighting.DirectionalLight;
import JGame.Engine.Graphics.Misc.Camera;
import JGame.Engine.Graphics.Models.Mesh;
import JGame.Engine.Graphics.Renderers.MeshRenderer;
import JGame.Engine.Graphics.Renderers.RayTracedSphereRenderer;
import JGame.Engine.Graphics.Renderers.RayTracingRenderer;
import JGame.Engine.Input.Input;
import JGame.Engine.Internal.InternalGameInstance;
import JGame.Engine.Internal.Time;
import JGame.Engine.Scenes.Scene;
import JGame.Engine.Structures.ColorRGB;
import JGame.Engine.Structures.ColorRGBA;
import JGame.Engine.Structures.Quaternion;
import JGame.Engine.Structures.Vector3D;
import JGame.Engine.Utilities.MathUtilities;
import Project.Scripts.RayTracingTest.EngineCameraController;
import org.lwjgl.glfw.GLFW;


/**
 * This class contains the main loop of the game, separated from the rest of the engine such that it doesn't collide with the other files,
 * add your own logic and custom classes inside the "Project" package.
 */
public class JGameInstance extends InternalGameInstance
{
    Scene spheresAndSun = new Scene()
    {
        @Override
        protected void InitScene()
        {
            Camera.Create();
            RayTracingRenderer.ResetAccumulation();

            Camera.Main.transform().SetGlobalPosition(new Vector3D(1.6f, 2, -7f));
            RayTracingRenderer.ResetAccumulation();

            Camera.Main.transform().SetGlobalRotation(Quaternion.EulerToQuaternion(new Vector3D(0,-35,0)));
            Camera.Main.SetFov(60f);

            Camera.Main.object().AddComponent(EngineCameraController.class);

            RayTracingRenderer.skyboxIntensity = 0.65f;
            sun = JGameObject.Create("Light").AddComponent(DirectionalLight.class);
            sun.intensity = 1f;
            sun.color = new ColorRGB(0.9608f   , 0.9575f, 0.6255f);

            sun.transform().SetGlobalRotation(Quaternion.FromAxisAngle(Vector3D.Right, MathUtilities.TO_RADIANS * 150f));
            sun.transform().RotateAxis(Vector3D.Up, -90);

            CreateSphere(new Vector3D(-1.25f, 1, -3.25f), Vector3D.One.Scale(2), ColorRGBA.Red, 0.0f, 0.1f, 0.1f);
            CreateSphere(new Vector3D(0, 1.25f, -1), Vector3D.One.Scale(2), ColorRGBA.Green, 0.0f, 0.1f, 0.1f);
            CreateSphere(new Vector3D(1.25f, 1.25f, 1.25f), Vector3D.One.Scale(2), ColorRGBA.Blue, 0.0f, 0.1f, 0.1f);

            CreateSphere(new Vector3D(0, -15, 0), Vector3D.One.Scale(31), ColorRGBA.Gray);
        }
    };

    @Override
    protected void Initialize()
    {
        Window.CreateWindow(1280, 720, false,  "Ray Tracing");
    }

    DirectionalLight sun;

    @Override
    public void Start()
    {
        Application.SetRaytracing(true);
        spheresAndSun.StartScene();
    }

    public JGameObject CreateSphere(Vector3D pos, Vector3D scale, ColorRGBA color)
    {
        return CreateSphere(pos, scale, color, 0.0f, 0.0f, 0.0f);
    }

    public JGameObject CreateSphere(Vector3D pos, Vector3D scale, ColorRGBA color, float emissivity, float smoothness, float specularity)
    {
        JGameObject sphere = JGameObject.Create("Sphere", pos, Quaternion.Identity, scale);

        var sphereRayTraced = sphere.AddComponent(RayTracedSphereRenderer.class);
        sphereRayTraced.material.color = color;
        sphereRayTraced.material.emissivity = emissivity;
        sphereRayTraced.material.smoothness = smoothness;
        sphereRayTraced.material.specularity = specularity;
        var sphereMesh = sphere.AddComponent(MeshRenderer.class);
        sphereMesh.mesh = Mesh.Sphere();
        sphereMesh.material.SetTint(color);

        return sphere;
    }

    float rotation = 150f;
    float rotationSpeed = 45f;
    float sunStrength = 1.0f;
    float sunStrengthChangeSpeed = 3.0f;
    @Override
    protected void Update()
    {
        boolean resetAccumulation = false;

        if(Input.GetKeyHold(GLFW.GLFW_KEY_E))
        {
            sun.transform().SetGlobalRotation(sun.transform().GetGlobalRotation().Multiply(Quaternion.FromAxisAngle(Vector3D.Right, MathUtilities.TO_RADIANS * -rotationSpeed * (float)Time.DeltaTime())));
            resetAccumulation = true;
        }
        else if (Input.GetKeyHold(GLFW.GLFW_KEY_Q))
        {
            sun.transform().SetGlobalRotation(sun.transform().GetGlobalRotation().Multiply(Quaternion.FromAxisAngle(Vector3D.Right, MathUtilities.TO_RADIANS * rotationSpeed * (float)Time.DeltaTime())));
            resetAccumulation = true;
        }

        if(Input.GetKeyHold(GLFW.GLFW_KEY_UP))
        {
            sunStrength += sunStrengthChangeSpeed * (float)Time.DeltaTime();
            sun.intensity = sunStrength;
            resetAccumulation = true;
        }
        else if(Input.GetKeyHold(GLFW.GLFW_KEY_DOWN))
        {
            sunStrength = Math.max(0.0f, sunStrength - sunStrengthChangeSpeed * (float)Time.DeltaTime());
            sun.intensity = sunStrength;
            resetAccumulation = true;
        }

        if(Input.GetKeyDown(GLFW.GLFW_KEY_TAB))
        {
            Application.SetRaytracing(!Application.GetRaytracingState());
            resetAccumulation = true;
        }

        if(Input.GetKeyDown(GLFW.GLFW_KEY_R))
        {
            spheresAndSun.StartScene();
        }

        if(Input.GetKeyDown(GLFW.GLFW_KEY_ESCAPE))
        {
            Application.Quit();
        }

        if(resetAccumulation)
            RayTracingRenderer.ResetAccumulation();

    }
}

