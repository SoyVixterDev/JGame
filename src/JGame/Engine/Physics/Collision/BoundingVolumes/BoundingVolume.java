package JGame.Engine.Physics.Collision.BoundingVolumes;

import JGame.Engine.Internal.Logger;
import JGame.Engine.Physics.Collision.Helper.BoundingVolumeHelper;
import JGame.Engine.Structures.Vector3D;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class BoundingVolume
{
    protected Vector3D center;

    public enum BoundingType
    {
        Box,
        Sphere
    }

    /**
     * Checks if the Volume is overlapping with another
     * @param other
     * The other volume
     * @return
     * True if the volumes are overlapping
     */
    @SuppressWarnings("JavaReflectionMemberAccess")
    public final boolean Overlaps(BoundingVolume other)
    {
        try
        {
            try
            {
                var method = BoundingVolumeHelper.class.getMethod("Overlaps", this.getClass(), other.getClass());
                return (Boolean) method.invoke(null, this, other);
            }
            catch(NoSuchMethodException e1)
            {
                var method = BoundingVolumeHelper.class.getMethod("Overlaps", other.getClass(), this.getClass());
                return (Boolean) method.invoke(null, other, this);
            }
        }
        catch (NoSuchMethodException e)
        {
            throw new IllegalArgumentException("Invalid Collider type! Add the appropriate GetContacts method to this class. Type: " + other.getClass().getName(), e);
        }
        catch (Exception e)
        {
            throw new RuntimeException("An error occurred while getting contacts with collider type: " + other.getClass().getName(), e);
        }
    }


    /**
     * Generates a bounding volume encapsulating the volumes, matching the type of the first volume
     * @param volumes
     * The volumes to encapsulate
     * @return
     * The volume that encapsulates the input
     */
    public static BoundingVolume GenerateFromBounds(BoundingVolume... volumes)
    {
        return GenerateFromBounds(Arrays.stream(volumes).toList());
    }

    /**
     * Generates a bounding volume encapsulating the volumes, matching the type of the first volume
     * @param volumes
     * The volumes to encapsulate
     * @return
     * The volume that encapsulates the input
     */
    public static BoundingVolume GenerateFromBounds(List<BoundingVolume> volumes)
    {
        if (volumes == null || volumes.isEmpty())
        {
            throw new IllegalArgumentException("Volume list cannot be null or empty");
        }

        BoundingType type;

        BoundingVolume volume = volumes.stream().filter(Objects::nonNull).findFirst().orElse(null);

        if(volume != null)
        {
            if(volume instanceof BoundingBox)
            {
                type = BoundingType.Box;
            }
            else if(volume instanceof BoundingSphere)
            {
                type = BoundingType.Sphere;
            }
            else
            {
                Logger.DebugWarning("Bounding Volume type \"" + volume.getClass().getName() + "\" not recognized!");
                return null;
            }
        }
        else
        {
            return null;
        }
        return GenerateFromBounds(type, volumes);
    }
    /**
     * Generates a bounding volume of the selected type that encapsulates all the volumes in the list
     * @param type
     * The type of bounding volume to generate
     * @param volumes
     * The volumes to encapsulate
     * @return
     * The final volume
     */
    public static BoundingVolume GenerateFromBounds(BoundingType type, BoundingVolume... volumes)
    {
        return GenerateFromBounds(type, Arrays.stream(volumes).toList());
    }
    /**
     * Generates a bounding volume of the selected type that encapsulates all the volumes in the list
     * @param type
     * The type of bounding volume to generate
     * @param volumes
     * The volumes to encapsulate
     * @return
     * The final volume
     */
    public static BoundingVolume GenerateFromBounds(BoundingType type, List<BoundingVolume> volumes)
    {
        if (volumes == null || volumes.isEmpty())
        {
            throw new IllegalArgumentException("Volume list cannot be null or empty");
        }

        return switch (type)
        {
            case Box -> GenerateBoundingBox(volumes);
            case Sphere -> GenerateBoundingSphere(volumes);
        };
    }

    private static BoundingBox GenerateBoundingBox(List<BoundingVolume> volumes)
    {
        Vector3D min = new Vector3D(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        Vector3D max = new Vector3D(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);

        for (BoundingVolume volume : volumes)
        {
            if(volume == null)
                continue;

            if (volume instanceof BoundingBox box)
            {
                min = Vector3D.Min(min, box.Min());
                max = Vector3D.Max(max, box.Max());
            }
            else if (volume instanceof BoundingSphere sphere)
            {
                Vector3D sphereMin = sphere.center.Subtract(new Vector3D(sphere.radius, sphere.radius, sphere.radius));
                Vector3D sphereMax = sphere.center.Add(new Vector3D(sphere.radius, sphere.radius, sphere.radius));
                min = Vector3D.Min(min, sphereMin);
                max = Vector3D.Max(max, sphereMax);
            }
            else
            {
                Logger.DebugWarning("Bounding Volume type \"" + volume.getClass().getName() + "\" not recognized!");
            }
        }

        Vector3D center = min.Add(max).Scale(0.5f);
        Vector3D halfSize = max.Subtract(min).Scale(0.5f);

        return new BoundingBox(center, halfSize);
    }

    private static BoundingSphere GenerateBoundingSphere(List<BoundingVolume> volumes)
    {
        Vector3D center = Vector3D.Zero;
        int count = 0;

        // Calculate the average center of all volumes
        for (BoundingVolume volume : volumes)
        {
            if(volume == null)
                continue;

            if (volume instanceof BoundingBox box)
            {
                center = center.Add(box.center);
                count++;
            }
            else if (volume instanceof BoundingSphere sphere)
            {
                center = center.Add(sphere.center);
                count++;
            }
            else
            {
                Logger.DebugWarning("Bounding Volume type \"" + volume.getClass().getName() + "\" not recognized!");
            }
        }

        if (count == 0)
        {
            throw new IllegalArgumentException("No valid bounding volumes to encapsulate");
        }

        center = center.Scale(1.0f / count);

        // Calculate the radius to encompass all volumes
        float radius = 0;
        for (BoundingVolume volume : volumes)
        {
            if (volume instanceof BoundingBox box)
            {
                Vector3D boxMaxCorner = box.Max();
                radius = Math.max(radius, Vector3D.Distance(boxMaxCorner, center));
            }
            else if (volume instanceof BoundingSphere sphere)
            {
                float sphereDistance = Vector3D.Distance(sphere.center, center) + sphere.radius;
                radius = Math.max(radius, sphereDistance);
            }
        }

        return new BoundingSphere(center, radius);
    }

    public abstract float GetVolume();
    public abstract float GetGrowth(BoundingVolume volume);
    public Vector3D GetCenter() { return center; }
}
