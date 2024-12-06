package JGame.Engine.Physics.Collision.Helper;

import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingBox;
import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingSphere;
import JGame.Engine.Structures.Vector3D;

public class BoundingVolumeHelper
{
    /**
     * Checks if a Bounding Box and a Bounding Sphere overlap
     * @param box
     * Box
     * @param sphere
     * Sphere
     * @return
     * True if the volumes overlap
     */
    public static boolean Overlaps(BoundingBox box, BoundingSphere sphere)
    {
        Vector3D closestPoint = new Vector3D
                (
                        Math.max(box.Min().x, Math.min(sphere.GetCenter().x, box.Max().x)),
                        Math.max(box.Min().y, Math.min(sphere.GetCenter().y, box.Max().y)),
                        Math.max(box.Min().z, Math.min(sphere.GetCenter().z, box.Max().z))
                );

        float distSquared = Vector3D.DistanceSquared(closestPoint, sphere.GetCenter());
        return distSquared <= (sphere.GetRadius() * sphere.GetRadius());
    }

    /**
     * Checks if two bounding spheres overlap
     * @param sphereA
     * Sphere A
     * @param sphereB
     * Sphere B
     * @return
     * true if the bounding spheres overlap
     */
    public static boolean Overlaps(BoundingSphere sphereA, BoundingSphere sphereB)
    {
        float distSquared = Vector3D.DistanceSquared(sphereA.GetCenter(), sphereB.GetCenter());
        return distSquared < (sphereA.GetRadius() + sphereB.GetRadius()) * (sphereA.GetRadius() + sphereB.GetRadius());
    }

    /**
     * Checks if two bounding boxes overlap
     * @param boxA
     * Box A
     * @param boxB
     * Box B
     * @return
     * True if the bounding boxes overlap
     */
    public static boolean Overlaps(BoundingBox boxA, BoundingBox boxB)
    {
        Vector3D minA = boxA.Min();
        Vector3D maxA = boxA.Max();
        Vector3D minB = boxB.Min();
        Vector3D maxB = boxB.Max();

        return (maxA.x >= minB.x && minA.x <= maxB.x) &&
                (maxA.y >= minB.y && minA.y <= maxB.y) &&
                (maxA.z >= minB.z && minA.z <= maxB.z);
    }
}
