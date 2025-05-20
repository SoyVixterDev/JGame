package Project.Scripts.RayTracingTest;

import JGame.Application.Window;
import JGame.Engine.Basic.JComponent;
import JGame.Engine.Basic.Transform;
import JGame.Engine.Graphics.Misc.Camera;
import JGame.Engine.Graphics.Misc.RayTracingMaterial;
import JGame.Engine.Graphics.Renderers.RayTracingRenderer;
import JGame.Engine.Input.Input;
import JGame.Engine.Internal.Logger;
import JGame.Engine.Internal.Time;
import JGame.Engine.Structures.Quaternion;
import JGame.Engine.Structures.Vector2D;
import JGame.Engine.Structures.Vector3D;
import org.lwjgl.glfw.GLFW;

import java.awt.geom.RectangularShape;

public class EngineCameraController extends JComponent
{
    public float droneModeMovementSpeed = 5.0f;
    public float droneModeRotationSpeed = 0.2f;
    public float zoomSpeed = 0.5f;
    public float panSpeed = 0.01f;

    private boolean droneModeActive = false;
    private boolean panningModeActive = false;

    private boolean resetAccumulation = false;

    private Transform camTransform;
    private Vector3D defaultPosition;
    private Quaternion defaultRotation;

    private float yaw;
    private float pitch;

    @Override
    protected void Start()
    {
        camTransform = Camera.Main.transform();

        defaultPosition = camTransform.GetGlobalPosition();
        defaultRotation = camTransform.GetGlobalRotation();

        Vector3D forward = camTransform.Forward();
        yaw = (float)Math.toDegrees(Math.atan2(forward.x, forward.z));
        pitch = (float)Math.toDegrees(Math.asin(forward.y));
    }

    @Override
    protected void Update()
    {
        HandleZoom();
        HandleReset();

        if (Input.GetMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT))
        {
            Input.SetMouseLock(true);
            droneModeActive = true;
        }
        if (Input.GetMouseButtonUp(GLFW.GLFW_MOUSE_BUTTON_LEFT))
        {
            Input.SetMouseLock(false);
            Input.CenterMouse();
            droneModeActive = false;
        }

        if (droneModeActive)
        {
            HandleDroneRotation();
            HandleDroneMovement();
        }

        if (Input.GetMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_MIDDLE))
        {
            Input.SetMouseLock(true);
            panningModeActive = true;
        }
        if (Input.GetMouseButtonUp(GLFW.GLFW_MOUSE_BUTTON_MIDDLE))
        {
            Input.SetMouseLock(false);
            Input.CenterMouse();
            panningModeActive = false;
        }

        if (panningModeActive)
        {
            HandlePanning();
        }

        if (resetAccumulation)
        {
            RayTracingRenderer.ResetAccumulation();
            resetAccumulation = false;
        }
    }

    private void HandleZoom()
    {
        float scroll = Input.GetMouseScroll().y;

        if (scroll != 0)
        {
            Vector3D forward = camTransform.Forward();
            camTransform.SetGlobalPosition(camTransform.GetGlobalPosition().Add(forward.Scale(scroll * zoomSpeed)));
            resetAccumulation = true;
        }
    }

    private void HandleDroneRotation()
    {
        Vector2D delta = Input.GetMouseMovement();

        if(delta.equals(Vector2D.Zero))
            return;

        resetAccumulation = true;

        yaw -= delta.x * droneModeRotationSpeed;
        pitch += delta.y * droneModeRotationSpeed;
        pitch = Math.max(-89f, Math.min(89f, pitch));

        Quaternion rot = Quaternion.EulerToQuaternion(new Vector3D(pitch, yaw, 0));
        camTransform.SetGlobalRotation(rot);
    }

    private void HandleDroneMovement()
    {
        Vector3D direction = Vector3D.Zero;

        if (Input.GetKeyHold(GLFW.GLFW_KEY_W)) direction = direction.Add(camTransform.Forward());
        if (Input.GetKeyHold(GLFW.GLFW_KEY_S)) direction = direction.Subtract(camTransform.Forward());
        if (Input.GetKeyHold(GLFW.GLFW_KEY_D)) direction = direction.Subtract(camTransform.Right());
        if (Input.GetKeyHold(GLFW.GLFW_KEY_A)) direction = direction.Add(camTransform.Right());
        if (Input.GetKeyHold(GLFW.GLFW_KEY_LEFT_CONTROL)) direction = direction.Subtract(camTransform.Up());
        if (Input.GetKeyHold(GLFW.GLFW_KEY_SPACE)) direction = direction.Add(camTransform.Up());

        if (direction.equals(Vector3D.Zero))
            return;

        resetAccumulation = true;

        camTransform.SetGlobalPosition(camTransform.GetGlobalPosition().Add(direction.Normalized().Scale(droneModeMovementSpeed * (float)(Time.DeltaTime()))));
    }

    private void HandlePanning()
    {
        Vector2D delta = Input.GetMouseMovement();

        if(delta.equals(Vector2D.Zero))
            return;

        resetAccumulation = true;

        Vector3D offset = camTransform.Right().Scale(delta.x * panSpeed).Add(camTransform.Up().Scale(delta.y * panSpeed));
        camTransform.SetGlobalPosition(camTransform.GetGlobalPosition().Add(offset));
    }

    private void HandleReset()
    {
        if (Input.GetKeyDown(GLFW.GLFW_KEY_G))
        {
            camTransform.SetGlobalPosition(defaultPosition);
            camTransform.SetGlobalRotation(defaultRotation);

            Vector3D forward = camTransform.Forward();
            yaw = (float)Math.toDegrees(Math.atan2(forward.x, forward.z));
            pitch = (float)Math.toDegrees(Math.asin(forward.y));

            resetAccumulation = true;
        }
    }
}
