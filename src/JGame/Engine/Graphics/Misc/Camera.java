package JGame.Engine.Graphics.Misc;

import JGame.Application.Window;
import JGame.Engine.Basic.JComponent;
import JGame.Engine.Basic.JGameObject;
import JGame.Engine.EventSystem.Event;
import JGame.Engine.Internal.Logger;
import JGame.Engine.Graphics.Renderers.SkyboxRenderer;
import JGame.Engine.Structures.Matrix4x4;
import JGame.Engine.Structures.Vector2D;
import JGame.Engine.Structures.Vector3D;
import JGame.Engine.Utilities.CameraMatrixUtilities;

/**
 * The Camera component, needed for having graphical output to the window, there should only be one main camera at any point
 */
public class Camera extends JComponent
{
    /**
     * Reference to the current main camera that's being used to render to the screen
     */
    public static Camera Main;

    /**
     * Holds the projection types the camera supports
     */
    public enum ProjectionType
    {
        Perspective,
        Orthographic
    }

    private ProjectionType projectionType = ProjectionType.Perspective;

    /**
     * Vertical Field of view for Perspective Projection
     */
    private float fov = 65.0f;
    /**
     * Vertical viewport sie for Orthographic Projection
     */
    private float orthoSize = 6;
    /**
     * Near plane of the camera, should always be a pretty small value
     */
    private float nearPlane = 0.001f;
    /**
     * Far plane of the camera, should always be a fairly large value
     */
    private float farPlane = 10000.0f;

    public ProjectionType GetProjectionType()
    {
        return projectionType;
    }

    /**
     * Sets the projection type
     * @param projectionType
     * The projection type to use
     */
    public void SetProjectionType(ProjectionType projectionType)
    {
        this.projectionType = projectionType;
        CalculateProjectionMatrix();
    }

    public float GetFov()
    {
        return fov;
    }
    public void SetFov(float fov)
    {
        this.fov = fov;
        CalculateProjectionMatrix();
    }
    public float GetOrthoSize()
    {
        return orthoSize;
    }
    public void SetOrthoSize(float orthoSize)
    {
        this.orthoSize = orthoSize;
        CalculateProjectionMatrix();
    }
    public float GetNearPlane()
    {
        return nearPlane;
    }
    public void SetNearPlane(float nearPlane)
    {
        this.nearPlane = nearPlane;
        CalculateProjectionMatrix();
    }
    public float GetFarPlane()
    {
        return farPlane;
    }
    public void SetFarPlane(float farPlane)
    {
        this.farPlane = farPlane;
        CalculateProjectionMatrix();
    }

    private static Matrix4x4 mainViewMatrix = Matrix4x4.Identity();
    private static Matrix4x4 mainProjectionMatrix = Matrix4x4.Identity();


    private final Event updateViewMatrix = new Event()
    {
        @Override
        protected void OnInvoke()
        {
            CalculateViewMatrix();
        }
    };

    @Override
    protected void Initialize()
    {
        Main = this;
    }

    @Override
    protected void OnEnable()
    {
        object().transform().OnChangeTransformation.Subscribe(updateViewMatrix);
    }

    @Override
    protected void OnDisable()
    {
        object().transform().OnChangeTransformation.Unsubscribe(updateViewMatrix);
    }



    /**
     * Shortcut to create an JGameObject with a camera component and a default skybox component
     * set in the world origin, with perspective projection
     * @return
     * The Camera's EngineObject
     */
    public static JGameObject Create()
    {
        return Create(ProjectionType.Perspective);
    }

    /**
     * Shortcut to create an JGameObject with a camera component and a default skybox component set in the world origin
     * @param projectionType
     * The projection type to be used by the camera
     * @return
     * The Camera's EngineObject
     */
    public static JGameObject Create(ProjectionType projectionType)
    {
        JGameObject object = JGameObject.Create("Main Camera");
        Camera cam = object.AddComponent(Camera.class);

        cam.SetProjectionType(projectionType);
        object.AddComponent(SkyboxRenderer.class);

        return object;
    }

    /**
     * Calculates the view matrix
     */
    public static void CalculateViewMatrix()
    {
        if(Main == null)
            return;

        mainViewMatrix = CameraMatrixUtilities.LookAt(Main.transform().GetGlobalPosition(), Main.transform().Forward(), Main.transform().Up());
    }

    /**
     * Calculates the projection matrix
     */
    public static void CalculateProjectionMatrix()
    {
        if(Main == null)
            return;

        Matrix4x4 projectionMatrix;

        if(Main.projectionType == ProjectionType.Perspective)
        {
             projectionMatrix = Main.PerspectiveProjection();
        }
        else if(Main.projectionType == ProjectionType.Orthographic)
        {
            projectionMatrix = Main.OrthographicProjection();
        }
        else
        {
            Logger.DebugError("Non-Valid Projection Mode: " + Main.projectionType + "!");
            return;
        }

        mainProjectionMatrix = projectionMatrix;
    }

    /**
     * Returns this frame's View Matrix
     * @return
     * This frame's view matrix
     */
    public static Matrix4x4 GetViewMatrix()
    {
        return mainViewMatrix;
    }

    /**
     * Returns the current Projection matrix
     * @return
     * The current projection matrix
     */
    public static Matrix4x4 GetProjectionMatrix()
    {
        return mainProjectionMatrix;
    }

    /**
     * Calculates and returns the orthographic projection matrix of the camera
     * @return
     * The orthographic projection matrix of the camera
     */
    private Matrix4x4 OrthographicProjection()
    {
        float orthoHalfSizeX = orthoSize * Window.GetWindowAspectRatio() / 2f;
        float orthoHalfSizeY = orthoSize / 2f;


        float left = -orthoHalfSizeX;
        float right = orthoHalfSizeX;
        float bottom = -orthoHalfSizeY;
        float top = orthoHalfSizeY;

        return CameraMatrixUtilities.OrthographicProjection(left, right, bottom, top, nearPlane, farPlane);
    }
    /**
     * Calculates and returns the perspective projection matrix of the camera
     * @return
     * The orthographic perspective matrix of the camera
     */
    private Matrix4x4 PerspectiveProjection()
    {
        return CameraMatrixUtilities.PerspectiveProjection(fov, Window.GetWindowAspectRatio(), farPlane, nearPlane);
    }

    /**
     * Converts from screen position to world space
     * @param screenPos
     * The screen position in screen space (pixels).
     * @param depth
     * Z component for the position in clip space.
     * @return
     * Position in world space.
     */
    public Vector3D ScreenToWorldPosition(Vector2D screenPos, float depth)
    {
        // Normalize screen position to [-1, 1]
        screenPos = screenPos.Divide(Window.GetWindowSize()).Subtract(Vector2D.One.Scale(0.5f));

        // Get matrices
        Matrix4x4 projectionMatrix = GetProjectionMatrix();
        Matrix4x4 viewMatrix = GetViewMatrix();
        Matrix4x4 inverseProjection = projectionMatrix.Inverse();
        Matrix4x4 inverseView = viewMatrix.Inverse();

        // Clip space coordinates
        float x = screenPos.x;
        float y = screenPos.y;
        float[] clipSpacePos = {x, y, depth, 1.0f};

        // Convert to view space
        float[] viewSpacePos = inverseProjection.Multiply(clipSpacePos);

        // Homogeneous coordinate normalization
        float w = viewSpacePos[3];
        if (Math.abs(w) > 1e-6f) // Avoid division by near-zero
        {
            for (int i = 0; i < 4; i++)
            {
                viewSpacePos[i] /= w;
            }
        }

        // Convert to world space
        float[] worldSpacePos = inverseView.Multiply(viewSpacePos);

        // Return as Vector3D
        return new Vector3D(worldSpacePos[0], worldSpacePos[1], worldSpacePos[2]);
    }
}
