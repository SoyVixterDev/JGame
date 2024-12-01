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
import JGame.Engine.Internal.Time;
import JGame.Engine.Physics.Constraints.ElasticConstraint;
import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Settings;
import JGame.Engine.Structures.ColorRGBA;
import JGame.Engine.Structures.Quaternion;
import JGame.Engine.Structures.Vector3D;
import JGame.Engine.Utilities.FileUtilities;
import JGame.Engine.Utilities.MathUtilities;
import Project.Scripts.DroneController;
import org.lwjgl.glfw.GLFW;

/**
 * This class contains the main loop of the game, separated from the rest of the engine such that it doesn't collide with the other files,
 * add your own logic and custom classes inside the "Project" package.
 */
public class JGameInstance extends InternalGameInstance
{
    JGameObject directionalLight = JGameObject.Create(DroneController.class);

    JGameObject floor = JGameObject.Create(new Vector3D(0.0f, -1.6f, 0.0f));

    JGameObject Amogus;

    JGameObject box;
    JGameObject box2;

    JGameObject spring;

    @Override
    public void Initialize()
    {
        Window.CreateWindow(1280, 720, false,  "MyGame");
    }

    @Override
    public void Start()
    {

        spring = JGameObject.Create(Vector3D.Zero, MeshRenderer.class, ElasticConstraint.class);
        spring.GetComponent(MeshRenderer.class).mesh = Mesh.Cylinder();

        //Window.SetWindowProperty(Window.WINDOW_VSYNC, true);

        Time.timeScale = 1f;

        Settings.Engine.SetDebugView(true);

        Camera.Create();

        Camera.Main.Transform().SetGlobalPosition(new Vector3D(0,0,-3f));

        Camera.Main.Object().GetComponent(SkyboxRenderer.class).skyboxMaterial.SetTint(new ColorRGBA(0.75f, 0.75f, 0.75f, 0.75f));

        box = JGameObject.Create(new Vector3D(0.0f, 10.0f, 0.0f), MeshRenderer.class, Rigidbody.class);
        box.GetComponent(MeshRenderer.class).mesh = Mesh.Cube();
        box.GetComponent(MeshRenderer.class).material.SetTint(ColorRGBA.Blue);
        box.GetComponent(Rigidbody.class).interpolate = true;
        box.GetComponent(Rigidbody.class).bodyType = Rigidbody.BodyType.Static;

        box2 = JGameObject.Create(new Vector3D(0.0f, 5.0f, 0.0f), MeshRenderer.class, Rigidbody.class);
        box2.GetComponent(MeshRenderer.class).mesh = Mesh.Cube();
        box2.GetComponent(MeshRenderer.class).material.SetTint(ColorRGBA.Red);
        box2.GetComponent(Rigidbody.class).interpolate = true;
        box2.GetComponent(Rigidbody.class).linearDragCoefficients.dragCoefficient = 0.01f;

        ElasticConstraint elasticConstraint = spring.GetComponent(ElasticConstraint.class);

        elasticConstraint.SetConnection
        (
            box.GetComponent(Rigidbody.class), new Vector3D(0.0f, -0.5f, 0.0f),
            box2.GetComponent(Rigidbody.class), new Vector3D(0.0f, 0.5f, 0.0f)
        );
        elasticConstraint.restLength = 3.0f;
        elasticConstraint.springConstant = 7.5f;
        elasticConstraint.elasticType = ElasticConstraint.ElasticConstraintType.Bungee;

        Light dirLight = directionalLight.AddComponent(DirectionalLight.class);
        dirLight.Transform().RotateAxis(Vector3D.Right, (float)Math.toRadians(45));
        dirLight.Transform().RotateAxis(Vector3D.Up, (float)Math.toRadians(45));
        dirLight.intensity = 0.75f;

        JGameObject.Create().AddComponent(AmbientLight.class).intensity = 0.25f;

        MeshRenderer renderer;

        Amogus = JGameObject.Create();
        Amogus.Transform().SetGlobalPosition(new Vector3D(3.0f, 0.0f, 0.0f));
        Amogus.Transform().RotateAxis(Vector3D.Up, (float)Math.PI);
        renderer = Amogus.AddComponent(MeshRenderer.class);
        renderer.mesh = FileUtilities.FBXLoader.ReadFBXFromResources("/Models/Custom/AmongUs.fbx");
        renderer.material = new Material("/Textures/Custom/AmongUs.png");

        Amogus = JGameObject.Create();
        Amogus.Transform().SetGlobalPosition(new Vector3D(0.0f, 0.0f, -3.0f));
        Amogus.Transform().RotateAxis(Vector3D.Up, (float)Math.PI);
        renderer = Amogus.AddComponent(MeshRenderer.class);
        renderer.mesh = FileUtilities.FBXLoader.ReadFBXFromResources("/Models/Custom/AmongUs.fbx");
        renderer.material = new Material("/Textures/Custom/AmongUs.png");

        Amogus = JGameObject.Create();
        Amogus.Transform().SetGlobalPosition(new Vector3D(0.0f, 0.0f, 3.0f));
        Amogus.Transform().RotateAxis(Vector3D.Up, (float)Math.PI);
        renderer = Amogus.AddComponent(MeshRenderer.class);
        renderer.mesh = FileUtilities.FBXLoader.ReadFBXFromResources("/Models/Custom/AmongUs.fbx");
        renderer.material = new Material("/Textures/Custom/AmongUs.png");

        Amogus = JGameObject.Create();
        Amogus.Transform().SetGlobalPosition(new Vector3D(-3.0f, 0.0f, 0.0f));
        Amogus.Transform().RotateAxis(Vector3D.Up, (float)Math.PI);
        renderer = Amogus.AddComponent(MeshRenderer.class);
        renderer.mesh = FileUtilities.FBXLoader.ReadFBXFromResources("/Models/Custom/AmongUs.fbx");
        renderer.material = new Material("/Textures/Custom/AmongUs.png");

        MeshRenderer meshRenderer = floor.AddComponent(MeshRenderer.class);
        meshRenderer.mesh = Mesh.Plane();
        floor.Transform().SetGlobalScale(Vector3D.One.Scale(50f));

        CheckSphere = JGameObject.Create(new Vector3D(0.0f, -0.5f, 0.0f), Quaternion.Identity, new Vector3D(0.05f, 0.05f, 0.05f));

        MeshRenderer sphereRenderer = CheckSphere.AddComponent(MeshRenderer.class);
        sphereRenderer.material.shader.SetLit(false);
        sphereRenderer.mesh = Mesh.Sphere();
        sphereRenderer.material.SetTint(ColorRGBA.Green);

        CheckSphere2 = JGameObject.Create(new Vector3D(0.0f, -0.0f, 0.0f), Quaternion.Identity, new Vector3D(0.05f, 0.05f, 0.05f));

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
        if(Input.GetKeyDown(GLFW.GLFW_KEY_ENTER))
        {
            box2.GetComponent(Rigidbody.class).AddForce(Camera.Main.Transform().Forward().Scale(5f), Rigidbody.ForceType.Impulse);
        }

        // Step 1: Calculate the direction vector between the two objects
        Vector3D direction = Vector3D.Subtract(box2.Transform().GetGlobalPosition(), box.Transform().GetGlobalPosition()).Normalized();

        // Step 2: Define the initial local axis (Y-axis by default)
        Vector3D localUp = new Vector3D(0, 1, 0);

        // Step 3: Calculate the rotation axis (cross product of localUp and direction)
        Vector3D rotationAxis = Vector3D.CrossProduct(localUp, direction).Normalized();

        // Step 4: Calculate the angle (dot product gives the cosine of the angle)
        float dotProduct = Vector3D.DotProduct(localUp, direction);
        float angle = (float) Math.acos(MathUtilities.Clamp(dotProduct, -1.0f, 1.0f));

        // Step 5: Create the quaternion representing the rotation
        Quaternion rotation = Quaternion.Identity.RotateAxis(rotationAxis, angle);
        spring.Transform().SetGlobalRotation(rotation);


        // Set the position to be the midpoint of box and box2 (if desired)
        Vector3D midpoint = Vector3D.Add(box.Transform().GetGlobalPosition(), box2.Transform().GetGlobalPosition()).Scale(0.5f);
        spring.Transform().SetGlobalPosition(midpoint);

        // Optionally, set the scale to match the distance between the two objects
        Vector3D pos1 = box.Transform().LocalToWorldSpace(new Vector3D(0.0f, -0.5f, 0.0f));
        Vector3D pos2 = box2.Transform().LocalToWorldSpace(new Vector3D(0.0f, 0.5f, 0.0f));
        float distance = Vector3D.Distance(pos1, pos2);
        spring.Transform().SetLocalScale(new Vector3D(0.25f, distance, 0.25f));  // Assuming the Y-axis is length
   }

}
