package JGame.Engine.Physics.Collision.BoundingVolumes;

import JGame.Engine.Basic.Transform;
import JGame.Engine.Internal.Logger;
import JGame.Engine.Structures.Vector3D;

import java.util.Vector;

/**
 * Represents a bounding box, used to specify the boundaries of a collider
 */
public class BoundingBox extends BoundingVolume
{
    Vector3D center;
    Vector3D halfSize;

    public BoundingBox(Vector3D center, Vector3D halfSize)
    {
        this.center = center;
        this.halfSize = halfSize;
    }

    @Override
    protected boolean Overlaps(BoundingBox box)
    {
        Vector3D minA = Min();
        Vector3D maxA = Max();
        Vector3D minB = box.Min();
        Vector3D maxB = box.Max();

        return (maxA.x >= minB.x && minA.x <= maxB.x) &&
                (maxA.y >= minB.y && minA.y <= maxB.y) &&
                (maxA.z >= minB.z && minA.z <= maxB.z);
    }

    @Override
    protected boolean Overlaps(BoundingSphere sphere)
    {
        Vector3D closestPoint = new Vector3D
        (
            Math.max(Min().x, Math.min(sphere.center.x, Max().x)),
            Math.max(Min().y, Math.min(sphere.center.y, Max().y)),
            Math.max(Min().z, Math.min(sphere.center.z, Max().z))
        );

        float distSquared = Vector3D.DistanceSquared(closestPoint, sphere.center);
        return distSquared <= (sphere.radius * sphere.radius);
    }

    @Override
    public float GetSize()
    {
        return (halfSize.x * 2) * (halfSize.y * 2) * (halfSize.z * 2);
    }

    @Override
    public float GetGrowth(BoundingVolume other)
    {
        Vector3D min = Min();
        Vector3D max = Max();

        if (other instanceof BoundingBox box)
        {
            min = Vector3D.Min(min, box.Min());
            max = Vector3D.Max(max, box.Max());
        }
        else if (other instanceof BoundingSphere sphere)
        {
            Vector3D sphereMin = sphere.center.Subtract(new Vector3D(sphere.radius, sphere.radius, sphere.radius));
            Vector3D sphereMax = sphere.center.Add(new Vector3D(sphere.radius, sphere.radius, sphere.radius));

            min = Vector3D.Min(min, sphereMin);
            max = Vector3D.Max(max, sphereMax);
        }
        else
        {
            Logger.DebugWarning("Bounding Volume type \"" + other.getClass().getName() + "\" not recognized!");
            return 0;
        }

        float originalSize = GetSize();
        Vector3D expandedHalfSize = max.Subtract(min).Scale(0.5f);
        float expandedSize = (expandedHalfSize.x * 2) * (expandedHalfSize.y * 2) * (expandedHalfSize.z * 2);

        return expandedSize - originalSize;
    }

    /**
     * Gets the min point in the bounds
     * @return
     * The min point in the bounds
     */
    public Vector3D Min()
    {
        return center.Subtract(halfSize);
    }


    /**
     * Gets the max point in the bounds
     * @return
     * The max point in the bounds
     */
    public Vector3D Max()
    {
        return center.Add(halfSize);
    }
}
