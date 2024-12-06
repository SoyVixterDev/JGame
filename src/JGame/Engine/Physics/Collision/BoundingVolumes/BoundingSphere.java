package JGame.Engine.Physics.Collision.BoundingVolumes;

import JGame.Engine.Internal.Logger;
import JGame.Engine.Structures.Vector3D;

/**
 * Represents a bounding sphere, used to specify the boundaries of a collider
 */
public class BoundingSphere extends BoundingVolume
{
    float radius;

    public float GetRadius()
    {
        return radius;
    }

    public BoundingSphere(Vector3D center, float radius)
    {
        this.center = center;
        this.radius = radius;
    }

    @Override
    public float GetVolume()
    {
        return (float) (4 * Math.PI / 3) * radius * radius * radius;
    }

    @Override
    public float GetGrowth(BoundingVolume other)
    {
        float expandedRadius = radius;

        if (other instanceof BoundingBox box)
        {
            Vector3D boxMaxCorner = box.Max();
            expandedRadius = Math.max(expandedRadius, Vector3D.Distance(center, boxMaxCorner));
        }
        else if (other instanceof BoundingSphere sphere)
        {
            float distanceBetweenCenters = Vector3D.Distance(center, sphere.center);
            expandedRadius = Math.max(expandedRadius, distanceBetweenCenters + sphere.radius);
        }
        else
        {
            Logger.DebugWarning("Bounding Volume type \"" + other.getClass().getName() + "\" not recognized!");
            return 0;
        }

        float originalSize = (float) (4.0 / 3.0 * Math.PI * Math.pow(radius, 3));
        float expandedSize = (float) (4.0 / 3.0 * Math.PI * Math.pow(expandedRadius, 3));

        return expandedSize - originalSize;
    }
}
