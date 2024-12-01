package JGame.Engine.Utilities;

import JGame.Engine.Structures.Matrix4x4;
import JGame.Engine.Structures.Vector3D;

/**
 * Utilities class for handling Calculations for the camera
 */
public class CameraMatrixUtilities
{
    /**
     * Calculates the orthographic projection from the components
     * @param left
     * Left component of the projection
     * @param right
     * Right component of the projection
     * @param bottom
     * Bottom component of the projection
     * @param top
     * Top component of the projection
     * @param nearPlane
     * Near plane component of the projection
     * @param farPlane
     * Far plane component of the projection
     * @return
     * The transformation matrix corresponding to the Orthographic Projection
     */
    public static Matrix4x4 OrthographicProjection(float left, float right, float bottom, float top, float nearPlane, float farPlane)
    {
        float[] orthoArray =
        {
            2.0f / (right - left), 0.0f, 0.0f, -(right + left) / (right - left),
            0.0f, 2.0f / (top - bottom), 0.0f, -(top + bottom) / (top - bottom),
            0.0f, 0.0f, -2.0f / (farPlane - nearPlane), -(farPlane + nearPlane) / (farPlane - nearPlane),
            0.0f, 0.0f, 0.0f, 1.0f
        };

        return new Matrix4x4(orthoArray);
    }

    /**
     * Calculates the Perspective Projection from the components
     * @param fov
     * The Vertical Field of View component of the projection in degrees
     * @param aspect
     * The Aspect ratio component of the projection
     * @param farPlane
     * The far plane component of the projection
     * @param nearPlane
     * The near plane component of the projection
     * @return
     * The Transformation matrix representing the Perspective Projection
     */
    public static Matrix4x4 PerspectiveProjection(float fov, float aspect, float farPlane, float nearPlane)
    {
        float tanFov = (float) Math.tan(Math.toRadians(fov/ 2.0f));
        float top = nearPlane * tanFov;
        float right = top * aspect;

        float[] perspectiveMatrixValues =
        {
             nearPlane/right, 0f, 0f, 0f ,
             0f, nearPlane/top, 0f, 0f ,
             0f, 0f, -(farPlane + nearPlane) / (farPlane - nearPlane), -(2 * farPlane * nearPlane) / (farPlane - nearPlane) ,
             0f, 0f, -1f, 0f
        };

        return new Matrix4x4(perspectiveMatrixValues);
    }

    /**
     * Calculates the "LookAt" Matrix representing looking at a direction
     * @param eye
     * The position of the "camera"
     * @param lookDirection
     * The look direction of the "camera"
     * @param up
     * The up vector of the camera
     * @return
     * The matrix corresponding to the look at calculations
     */
    public static Matrix4x4 LookAt(Vector3D eye, Vector3D lookDirection, Vector3D up)
    {
        // Calculate basis vectors
        Vector3D f = lookDirection.Normalized(); // Forward vector
        Vector3D s = Vector3D.CrossProduct(f, up).Normalized(); // Right vector
        Vector3D u = Vector3D.CrossProduct(s, f); // Up vector

        // Create view matrix
        float[] viewMatrixValues =
        {
             s.x, s.y, s.z, -Vector3D.DotProduct(s, eye),
             u.x, u.y, u.z, -Vector3D.DotProduct(u, eye),
             -f.x, -f.y, -f.z, Vector3D.DotProduct(f, eye),
             0f, 0f, 0f, 1f
        };

        return new Matrix4x4(viewMatrixValues);
    }

}
