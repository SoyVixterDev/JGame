package Project;

import JGame.Application.Window;
import JGame.Engine.Graphics.Lighting.*;
import JGame.Engine.Graphics.Models.Mesh;
import JGame.Engine.Input.Input;
import JGame.Engine.Internal.InternalGameInstance;
import JGame.Engine.Basic.JGameObject;
import JGame.Engine.Graphics.Misc.Camera;
import JGame.Engine.Graphics.Misc.Material;
import JGame.Engine.Graphics.Renderers.MeshRenderer;
import JGame.Engine.Graphics.Renderers.SkyboxRenderer;
import JGame.Engine.Internal.Logger;
import JGame.Engine.Internal.Time;
import JGame.Engine.Physics.Collision.Colliders.BoxCollider;
import JGame.Engine.Physics.Collision.Colliders.SphereCollider;
import JGame.Engine.Physics.Constraints.ElasticConstraint;
import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Settings;
import JGame.Engine.Structures.ColorRGBA;
import JGame.Engine.Structures.Quaternion;
import JGame.Engine.Structures.Vector3D;
import JGame.Engine.Utilities.FileUtilities;
import JGame.Engine.Utilities.MathUtilities;
import JGame.Engine.Utilities.Random;
import Project.Scripts.DroneController;
import org.lwjgl.glfw.GLFW;

/**
 * This class contains the main loop of the game, separated from the rest of the engine such that it doesn't collide with the other files,
 * add your own logic and custom classes inside the "Project" package.
 */
public class JGameInstance extends InternalGameInstance
{
    JGameObject directionalLight = JGameObject.Create("Directional Light", DroneController.class);

    JGameObject floor = JGameObject.Create("Floor",new Vector3D(0.0f, -1.6f, 0.0f));

    JGameObject Amogus;

    JGameObject blueSphere;
    JGameObject redBox;

    JGameObject spring;

    @Override
    protected void Initialize()
    {
        Window.CreateWindow(1280, 720, false,  "MyGame");
    }

    @Override
    public void Start()
    {
        spring = JGameObject.Create("Spring", Vector3D.Zero, MeshRenderer.class, ElasticConstraint.class);
        spring.GetComponent(MeshRenderer.class).mesh = Mesh.Cylinder();

        //Window.SetWindowProperty(Window.WINDOW_VSYNC, true);

        Time.timeScale = 1f;

        Settings.Debug.SetDebugView(true);
        Settings.Debug.SetDebugBVH(true);

        Camera.Create();

        Camera.Main.transform().SetGlobalPosition(new Vector3D(0,0,-3f));

        Camera.Main.object().GetComponent(SkyboxRenderer.class).skyboxMaterial.SetTint(new ColorRGBA(0.75f, 0.75f, 0.75f, 0.75f));

        blueSphere = JGameObject.Create("Blue Sphere",new Vector3D(0.0f, 10.0f, 0.0f), MeshRenderer.class, Rigidbody.class, SphereCollider.class);
        blueSphere.GetComponent(MeshRenderer.class).mesh = Mesh.Sphere();
        blueSphere.GetComponent(MeshRenderer.class).material.SetTint(ColorRGBA.Blue);
        blueSphere.GetComponent(Rigidbody.class).bodyType = Rigidbody.BodyType.Static;

        redBox = JGameObject.Create("Red Box",new Vector3D(0.0f, 5.0f, 0.0f), MeshRenderer.class, Rigidbody.class, BoxCollider.class);
        redBox.GetComponent(MeshRenderer.class).mesh = Mesh.Cube();
        redBox.GetComponent(MeshRenderer.class).material.SetTint(ColorRGBA.Red);
        redBox.GetComponent(Rigidbody.class).linearDragCoefficients.dragCoefficient = 0.01f;

        ElasticConstraint elasticConstraint = spring.GetComponent(ElasticConstraint.class);

        elasticConstraint.SetConnection
        (
            blueSphere.GetComponent(Rigidbody.class), new Vector3D(0.0f, -0.5f, 0.0f),
            redBox.GetComponent(Rigidbody.class), new Vector3D(0.0f, 0.5f, 0.0f)
        );
        elasticConstraint.restLength = 3.0f;
        elasticConstraint.springConstant = 7.5f;
        elasticConstraint.elasticType = ElasticConstraint.ElasticConstraintType.Bungee;

        Light dirLight = directionalLight.AddComponent(DirectionalLight.class);
        dirLight.transform().RotateAxis(Vector3D.Right, (float)Math.toRadians(45));
        dirLight.transform().RotateAxis(Vector3D.Up, (float)Math.toRadians(45));
        dirLight.intensity = 0.75f;

        JGameObject.Create("Ambient Light").AddComponent(AmbientLight.class).intensity = 0.25f;

        MeshRenderer renderer;

        Amogus = JGameObject.Create("Amogus 1");
        Amogus.transform().SetGlobalPosition(new Vector3D(3.0f, 0.0f, 0.0f));
        Amogus.transform().RotateAxis(Vector3D.Up, (float)Math.PI);
        renderer = Amogus.AddComponent(MeshRenderer.class);
        renderer.mesh = FileUtilities.FBXLoader.ReadFBXFromResources("/Models/Custom/AmongUs.fbx");
        renderer.material = new Material("/Textures/Custom/AmongUs.png");

        Amogus = JGameObject.Create("Amogus 2");
        Amogus.transform().SetGlobalPosition(new Vector3D(0.0f, 0.0f, -3.0f));
        Amogus.transform().RotateAxis(Vector3D.Up, (float)Math.PI);
        renderer = Amogus.AddComponent(MeshRenderer.class);
        renderer.mesh = FileUtilities.FBXLoader.ReadFBXFromResources("/Models/Custom/AmongUs.fbx");
        renderer.material = new Material("/Textures/Custom/AmongUs.png");

        Amogus = JGameObject.Create("Amogus 3");
        Amogus.transform().SetGlobalPosition(new Vector3D(0.0f, 0.0f, 3.0f));
        Amogus.transform().RotateAxis(Vector3D.Up, (float)Math.PI);
        renderer = Amogus.AddComponent(MeshRenderer.class);
        renderer.mesh = FileUtilities.FBXLoader.ReadFBXFromResources("/Models/Custom/AmongUs.fbx");
        renderer.material = new Material("/Textures/Custom/AmongUs.png");

        Amogus = JGameObject.Create("Amogus 4");
        Amogus.transform().SetGlobalPosition(new Vector3D(-3.0f, 0.0f, 0.0f));
        Amogus.transform().RotateAxis(Vector3D.Up, (float)Math.PI);
        renderer = Amogus.AddComponent(MeshRenderer.class);
        renderer.mesh = FileUtilities.FBXLoader.ReadFBXFromResources("/Models/Custom/AmongUs.fbx");
        renderer.material = new Material("/Textures/Custom/AmongUs.png");

        MeshRenderer meshRenderer = floor.AddComponent(MeshRenderer.class);
        meshRenderer.mesh = Mesh.Plane();
        floor.transform().SetGlobalScale(Vector3D.One.Scale(50f));

        CheckSphere = JGameObject.Create("Check Sphere",new Vector3D(0.0f, -0.5f, 0.0f), Quaternion.Identity, new Vector3D(0.05f, 0.05f, 0.05f));

        MeshRenderer sphereRenderer = CheckSphere.AddComponent(MeshRenderer.class);
        sphereRenderer.material.shader.SetLit(false);
        sphereRenderer.mesh = Mesh.Sphere();
        sphereRenderer.material.SetTint(ColorRGBA.Green);

        CheckSphere2 = JGameObject.Create("Check Sphere 2",new Vector3D(0.0f, -0.0f, 0.0f), Quaternion.Identity, new Vector3D(0.05f, 0.05f, 0.05f));

        MeshRenderer sphereRenderer2 = CheckSphere2.AddComponent(MeshRenderer.class);
        sphereRenderer2.material.shader.SetLit(false);
        sphereRenderer2.mesh = Mesh.Sphere();
        sphereRenderer2.material.SetTint(ColorRGBA.Green);
    }

    JGameObject CheckSphere;
    JGameObject CheckSphere2;

    @Override
    public void Update()
    {
        if(redBox == null) return;

        if(Input.GetKeyDown(GLFW.GLFW_KEY_P)) redBox.Destroy();

        if(redBox == null) return;


        //Logger.DebugLog("Velocity: " + box2.GetComponent(Rigidbody.class).linearVelocity + " | Acceleration: " + box.GetComponent(Rigidbody.class).GetLinearAcceleration());
        if(Input.GetKeyDown(GLFW.GLFW_KEY_ENTER))
        {
            redBox.GetComponent(Rigidbody.class).AddForce(Camera.Main.transform().Forward().Scale(5f), Rigidbody.ForceType.Impulse);
        }

        // Step 1: Calculate the direction vector between the two objects
        Vector3D direction = Vector3D.Subtract(redBox.transform().GetGlobalPosition(), blueSphere.transform().GetGlobalPosition()).Normalized();

        // Step 2: Define the initial local axis (Y-axis by default)
        Vector3D localUp = new Vector3D(0, 1, 0);

        // Step 3: Calculate the rotation axis (cross product of localUp and direction)
        Vector3D rotationAxis = Vector3D.CrossProduct(localUp, direction).Normalized();

        // Step 4: Calculate the angle (dot product gives the cosine of the angle)
        float dotProduct = Vector3D.DotProduct(localUp, direction);
        float angle = (float) Math.acos(MathUtilities.Clamp(dotProduct, -1.0f, 1.0f));

        // Step 5: Create the quaternion representing the rotation
        Quaternion rotation = Quaternion.Identity.RotateAxis(rotationAxis, angle);
        spring.transform().SetGlobalRotation(rotation);


        // Set the position to be the midpoint of box and box2 (if desired)
        Vector3D midpoint = Vector3D.Add(blueSphere.transform().GetGlobalPosition(), redBox.transform().GetGlobalPosition()).Scale(0.5f);
        spring.transform().SetGlobalPosition(midpoint);

        // Optionally, set the scale to match the distance between the two objects
        Vector3D pos1 = blueSphere.transform().LocalToWorldSpace(new Vector3D(0.0f, -0.5f, 0.0f));
        Vector3D pos2 = redBox.transform().LocalToWorldSpace(new Vector3D(0.0f, 0.5f, 0.0f));
        float distance = Vector3D.Distance(pos1, pos2);
        spring.transform().SetLocalScale(new Vector3D(0.25f, distance, 0.25f));  // Assuming the Y-axis is length
   }

}
