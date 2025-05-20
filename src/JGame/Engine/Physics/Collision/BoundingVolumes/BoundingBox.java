package JGame.Engine.Physics.Collision.BoundingVolumes;

import JGame.Engine.Internal.Logger;
import JGame.Engine.Structures.Vector3D;
import JGame.Engine.Utilities.MathUtilities;

/**
 * Represents a bounding box, used to specify the boundaries of a collider
 */
public class BoundingBox extends BoundingVolume
{
    Vector3D halfSize;

    public Vector3D GetHalfSize()
    {
        return halfSize;
    }

    public BoundingBox(Vector3D center, Vector3D halfSize)
    {
        this.center = center;
        this.halfSize = halfSize;
    }

    @Override
    public float GetVolume()
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
            min = MathUtilities.Min(min, box.Min());
            max = MathUtilities.Max(max, box.Max());
        }
        else if (other instanceof BoundingSphere sphere)
        {
            Vector3D sphereMin = sphere.center.Subtract(new Vector3D(sphere.radius, sphere.radius, sphere.radius));
            Vector3D sphereMax = sphere.center.Add(new Vector3D(sphere.radius, sphere.radius, sphere.radius));

            min = MathUtilities.Min(min, sphereMin);
            max = MathUtilities.Max(max, sphereMax);
        }
        else
        {
            Logger.DebugWarning("Bounding Volume type \"" + other.getClass().getName() + "\" not recognized!");
            return 0;
        }

        float originalSize = GetVolume();
        Vector3D expandedHalfSize = max.Subtract(min);
        float expandedSize = (expandedHalfSize.x) * (expandedHalfSize.y) * (expandedHalfSize.z);

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

    @Override
    public String toString()
    {
        return "BoundingBox{" + "Half Size: " + halfSize + ", Center: " + center + '}';
    }
}
