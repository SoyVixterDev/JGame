package Project;

import JGame.Application.Application;
import JGame.Application.Window;
import JGame.Engine.Basic.JGameObject;
import JGame.Engine.Graphics.Lighting.DirectionalLight;
import JGame.Engine.Graphics.Misc.Camera;
import JGame.Engine.Graphics.Models.Mesh;
import JGame.Engine.Graphics.Renderers.MeshRenderer;
import JGame.Engine.Graphics.Renderers.RayTracing.RayTracedBoxRenderer;
import JGame.Engine.Graphics.Renderers.RayTracing.RayTracedSphereRenderer;
import JGame.Engine.Graphics.Renderers.RayTracing.RayTracingRenderer;
import JGame.Engine.Input.Input;
import JGame.Engine.Internal.InternalGameInstance;
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
            Camera.Main.SetFov(60f);

            RayTracingRenderer.ResetAccumulation();

            Camera.Main.transform().SetGlobalRotation(Quaternion.EulerToQuaternion(new Vector3D(0,-35,0)));

            Camera.Main.object().AddComponent(EngineCameraController.class);

            RayTracingRenderer.skyboxIntensity = 0.65f;
            sun = JGameObject.Create("Light").AddComponent(DirectionalLight.class);
            sun.intensity = 1.5f;
            sun.color = new ColorRGB(0.9858f, 0.979f, 0.8155f);

            sun.transform().SetGlobalRotation(Quaternion.FromAxisAngle(Vector3D.Right, MathUtilities.TO_RADIANS * 150f));
            sun.transform().RotateAxis(Vector3D.Up, -90);


            CreateSphere(new Vector3D(-1.25f, 1, -3.25f), Vector3D.One.Scale(2), ColorRGBA.Red, 0.0f, 0.1f, 0.1f);
            CreateSphere(new Vector3D(0, 1.25f, -1), Vector3D.One.Scale(2), ColorRGBA.Green, 0.0f, 0.1f, 0.1f);
            CreateSphere(new Vector3D(1.25f, 1.25f, 1.25f), Vector3D.One.Scale(2), ColorRGBA.Blue, 0.0f, 0.1f, 0.1f);

            CreateSphere(new Vector3D(0, -15, 0), Vector3D.One.Scale(31), ColorRGBA.Gray);
        }
    };

    Scene smoothnessTest = new Scene()
    {
        @Override
        protected void InitScene()
        {
            Camera.Create();
            RayTracingRenderer.ResetAccumulation();

            Camera.Main.transform().SetGlobalPosition(new Vector3D(0, 0, -6f));
            Camera.Main.SetFov(60f);

            Camera.Main.object().AddComponent(EngineCameraController.class);

            RayTracingRenderer.skyboxIntensity = 0.65f;
            sun = JGameObject.Create("Light").AddComponent(DirectionalLight.class);
            sun.intensity = 1.5f;
            sun.color = new ColorRGB(0.9858f, 0.979f, 0.8155f);

            sun.transform().SetGlobalRotation(Quaternion.FromAxisAngle(Vector3D.Right, MathUtilities.TO_RADIANS * 150f));
            sun.transform().RotateAxis(Vector3D.Up, -90);


            CreateSphere(new Vector3D(-3.75f, 0, 0), Vector3D.One.Scale(2), ColorRGBA.White, 0.0f, 1.0f, 1.0f);
            CreateSphere(new Vector3D(-1.25f, 0, 0), Vector3D.One.Scale(2), ColorRGBA.White, 0.0f, 0.5f, 1.0f);
            CreateSphere(new Vector3D(1.25f, 0, 0), Vector3D.One.Scale(2), ColorRGBA.White, 0.0f, 0.25f, 1.0f);
            CreateSphere(new Vector3D(3.75f, 0, 0), Vector3D.One.Scale(2), ColorRGBA.White, 0.0f, 0.0f, 1.0f);
        }
    };

    Scene specularityTest = new Scene()
    {
        @Override
        protected void InitScene()
        {
            Camera.Create();
            RayTracingRenderer.ResetAccumulation();

            Camera.Main.transform().SetGlobalPosition(new Vector3D(0, 0, -6f));
            Camera.Main.SetFov(60f);

            Camera.Main.object().AddComponent(EngineCameraController.class);

            RayTracingRenderer.skyboxIntensity = 0.65f;
            sun = JGameObject.Create("Light").AddComponent(DirectionalLight.class);
            sun.intensity = 1.5f;
            sun.color = new ColorRGB(0.9858f, 0.979f, 0.8155f);

            sun.transform().SetGlobalRotation(Quaternion.FromAxisAngle(Vector3D.Right, MathUtilities.TO_RADIANS * 150f));
            sun.transform().RotateAxis(Vector3D.Up, -90);


            CreateSphere(new Vector3D(-3.75f, 0, 0), Vector3D.One.Scale(2), ColorRGBA.Red, 0.0f, 1.0f, 0.75f);
            CreateSphere(new Vector3D(-1.25f, 0, 0), Vector3D.One.Scale(2), ColorRGBA.Red, 0.0f, 1.0f, 0.35f);
            CreateSphere(new Vector3D(1.25f, 0, 0), Vector3D.One.Scale(2), ColorRGBA.Red, 0.0f, 1.0f, 0.125f);
            CreateSphere(new Vector3D(3.75f, 0, 0), Vector3D.One.Scale(2), ColorRGBA.Red, 0.0f, 1.0f, 0.025f);
        }
    };

    Scene dofTest = new Scene()
    {
        @Override
        protected void InitScene()
        {
            Camera.Create();
            RayTracingRenderer.ResetAccumulation();

            Camera.Main.transform().SetGlobalPosition(new Vector3D(0, 0, -6f));
            Camera.Main.SetFov(60f);

            Camera.Main.object().AddComponent(EngineCameraController.class);

            RayTracingRenderer.skyboxIntensity = 0.65f;
            sun = JGameObject.Create("Light").AddComponent(DirectionalLight.class);
            sun.intensity = 1.5f;
            sun.color = new ColorRGB(0.9858f, 0.979f, 0.8155f);

            sun.transform().SetGlobalRotation(Quaternion.FromAxisAngle(Vector3D.Right, MathUtilities.TO_RADIANS * 150f));
            sun.transform().RotateAxis(Vector3D.Up, -90);


            CreateSphere(new Vector3D(0, 0, 0), Vector3D.One.Scale(2), ColorRGBA.White, 0.0f, 0.1f, 0.05f);
            CreateSphere(new Vector3D(-7.5f, 1f, 10), Vector3D.One.Scale(4), ColorRGBA.Green, 0.0f, 0.1f, 0.05f);
            CreateSphere(new Vector3D(7.5f, 2f, 10), Vector3D.One.Scale(6), ColorRGBA.Blue, 0.0f, 0.1f, 0.05f);
            CreateSphere(new Vector3D(0f, 4f, 22.5f), Vector3D.One.Scale(10), ColorRGBA.Red, 0.0f, 0.1f, 0.05f);
        }
    };

    Scene reflectionsTest = new Scene()
    {
        @Override
        protected void InitScene()
        {
            Camera.Create();
            RayTracingRenderer.ResetAccumulation();

            Camera.Main.transform().SetGlobalPosition(new Vector3D(0, 0, -5.5f));
            Camera.Main.SetFov(90f);

            Camera.Main.object().AddComponent(EngineCameraController.class);

            RayTracingRenderer.skyboxIntensity = 0f;

            float boxLength = 12.5f;
            float wallThickness = 0.3f;

            CreateBox(new Vector3D(0,boxLength/2 - wallThickness / 2,0), new Vector3D(boxLength,wallThickness, boxLength), Quaternion.Identity, ColorRGBA.White);
            CreateBox(new Vector3D(0,boxLength/2 - wallThickness,0), new Vector3D(boxLength / 1.5f,wallThickness, boxLength / 1.5f), Quaternion.Identity, ColorRGBA.White, 1.5f, 0.0f, 0.0f);
            CreateBox(new Vector3D(0,-boxLength/2 + wallThickness / 2,0), new Vector3D(boxLength,wallThickness, boxLength), Quaternion.Identity, ColorRGBA.Red);
            CreateBox(new Vector3D(boxLength/2 - wallThickness / 2,0,0), new Vector3D(wallThickness,boxLength, boxLength), Quaternion.Identity, ColorRGBA.Green);
            CreateBox(new Vector3D(-boxLength/2 + wallThickness / 2,0,0), new Vector3D(wallThickness,boxLength, boxLength), Quaternion.Identity, ColorRGBA.Blue);
            CreateBox(new Vector3D(0,0,boxLength/2 - wallThickness / 2), new Vector3D(boxLength,boxLength, wallThickness), Quaternion.Identity, ColorRGBA.White, 0.0f, 0.99f, 1.0f);
            CreateBox(new Vector3D(0,0,-boxLength/2 + wallThickness / 2), new Vector3D(boxLength,boxLength, wallThickness), Quaternion.Identity, ColorRGBA.White, 0.0f, 0.99f, 1.0f);

            CreateSphere(new Vector3D(0, 0, 0), Vector3D.One.Scale(5f), ColorRGBA.White, 0.0f, 0.01f, 0.1f);
        }
    };

    Scene currentScene = spheresAndSun;

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

    public JGameObject CreateBox(Vector3D pos, Vector3D scale, Quaternion rotation, ColorRGBA color)
    {
        return CreateBox(pos, scale, rotation, color, 0.0f, 0.0f, 0.0f);
    }

    public JGameObject CreateBox(Vector3D pos, Vector3D scale, Quaternion rotation, ColorRGBA color, float emissivity, float smoothness, float specularity)
    {
        JGameObject box = JGameObject.Create("Box", pos, rotation, scale);

        var boxRayTraced = box.AddComponent(RayTracedBoxRenderer.class);
        boxRayTraced.material.color = color;
        boxRayTraced.material.specularColor = new ColorRGBA( 0.89f, 0.975f, 0.89f, 1.0f);
        boxRayTraced.material.emissivity = emissivity;
        boxRayTraced.material.smoothness = smoothness;
        boxRayTraced.material.specularity = specularity;
        var boxMesh = box.AddComponent(MeshRenderer.class);
        boxMesh.mesh = Mesh.Cube();
        boxMesh.material.SetTint(color);

        return box;
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

    @Override
    protected void Update()
    {
        boolean resetAccumulation = false;

        if(Input.GetKeyDown(GLFW.GLFW_KEY_1))
        {
            currentScene = spheresAndSun;
            currentScene.StartScene();
        }
        else if(Input.GetKeyDown(GLFW.GLFW_KEY_2))
        {
            currentScene = smoothnessTest;
            currentScene.StartScene();
        }
        else if(Input.GetKeyDown(GLFW.GLFW_KEY_3))
        {
            currentScene = specularityTest;
            currentScene.StartScene();
        }
        else if(Input.GetKeyDown(GLFW.GLFW_KEY_4))
        {
            currentScene = dofTest;
            currentScene.StartScene();
        }
        else if(Input.GetKeyDown(GLFW.GLFW_KEY_5))
        {
            currentScene = reflectionsTest;
            currentScene.StartScene();
        }

        if(Input.GetKeyDown(GLFW.GLFW_KEY_TAB))
        {
            Application.SetRaytracing(!Application.GetRaytracingState());
            resetAccumulation = true;
        }

        if(Input.GetKeyDown(GLFW.GLFW_KEY_R))
        {
            currentScene.StartScene();
        }

        if(Input.GetKeyDown(GLFW.GLFW_KEY_ESCAPE))
        {
            Application.Quit();
        }

        if(resetAccumulation)
            RayTracingRenderer.ResetAccumulation();

    }
}

