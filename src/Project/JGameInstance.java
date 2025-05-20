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

            sun = JGameObject.Create("Light").AddComponent(DirectionalLight.class);
            sun.intensity = 5f;
            sun.color = new ColorRGB(0.9608f   , 0.8275f, 0.5255f);

            sun.transform().RotateAxis(Vector3D.Up, -90);
            sun.transform().SetGlobalRotation(Quaternion.FromAxisAngle(Vector3D.Right, MathUtilities.TO_RADIANS * 150f));

            CreateSphere(new Vector3D(-1.25f, 1, -3.25f), Vector3D.One.Scale(2), ColorRGBA.Red, 0.0f);
            CreateSphere(new Vector3D(0, 1.25f, -1), Vector3D.One.Scale(2), ColorRGBA.Green, 0.0f);
            CreateSphere(new Vector3D(1.25f, 1.25f, 1.25f), Vector3D.One.Scale(2), ColorRGBA.Blue, 0.0f);

            CreateSphere(new Vector3D(0, -15, 0), Vector3D.One.Scale(31), ColorRGBA.White, 0.0f);
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

    public JGameObject CreateSphere(Vector3D pos, Vector3D scale, ColorRGBA color, float emissivity)
    {
        JGameObject sphere = JGameObject.Create("Sphere", pos, Quaternion.Identity, scale);

        var sphereRayTraced = sphere.AddComponent(RayTracedSphereRenderer.class);
        sphereRayTraced.material.color = color;
        sphereRayTraced.material.emissivity = emissivity;
        var sphereMesh = sphere.AddComponent(MeshRenderer.class);
        sphereMesh.mesh = Mesh.Sphere();
        sphereMesh.material.SetTint(color);

        return sphere;
    }

    float rotation = 150f;
    float rotationSpeed = 45f;

    @Override
    protected void Update()
    {
        if(Input.GetKeyDown(GLFW.GLFW_KEY_E))
        {
            rotation += rotationSpeed * (float)Time.DeltaTime();
            sun.transform().SetGlobalRotation(Quaternion.FromAxisAngle(Vector3D.Right, MathUtilities.TO_RADIANS * rotation));
            RayTracingRenderer.ResetAccumulation();
        }

        if(Input.GetKeyDown(GLFW.GLFW_KEY_TAB))
        {
            Application.SetRaytracing(!Application.GetRaytracingState());

            if(Application.GetRaytracingState())
                sun.intensity = 5.0f;
            else
                sun.intensity = 1.0f;
        }

        if(Input.GetKeyDown(GLFW.GLFW_KEY_R))
        {
            spheresAndSun.StartScene();
        }

        if(Input.GetKeyDown(GLFW.GLFW_KEY_ESCAPE))
        {
            Application.Quit();
        }
    }
}

