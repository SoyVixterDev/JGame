package JGame.Engine.Physics.Collision.Colliders;

import JGame.Engine.Graphics.Renderers.WireframeRenderers.WirecubeRenderer;
import JGame.Engine.Graphics.Renderers.WireframeRenderers.WireshapeRenderer;
import JGame.Engine.Internal.Logger;
import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingBox;
import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingVolume;
import JGame.Engine.Physics.Collision.Contact.Contact;
import JGame.Engine.Physics.Raycast.RaycastContact;
import JGame.Engine.Structures.Vector3D;
import JGame.Engine.Utilities.MathUtilities;

public class BoxCollider extends Collider
{
    /**
     * Half size of the box
     */
    private Vector3D halfSize = Vector3D.One.Scale(0.5f);

    public Vector3D GetHalfSize()
    {
        return halfSize;
    }
    public Vector3D GetScaledHalfSize()
    {
        return MathUtilities.Abs(halfSize).Multiply(transform().GetGlobalScale());
    }

    public void SetHalfSize(Vector3D halfSize)
    {
        this.halfSize = halfSize;
        if(colliderRenderer != null)
            ((WirecubeRenderer)colliderRenderer).SetHalfSize(MathUtilities.Abs(halfSize));
    }
    @Override
    public void SetCenter(Vector3D center)
    {
        this.center = center;
        if(colliderRenderer != null)
            ((WirecubeRenderer)colliderRenderer).SetCenter(center);
    }
    @Override
    public RaycastContact Raycast(Vector3D origin, Vector3D direction, float maxDistance)
    {
        Vector3D localOrigin = transform().WorldToLocalSpace(origin);
        Vector3D localDirection = transform().WorldToLocalSpace(origin.Add(direction)).Subtract(localOrigin).Normalized();

        Vector3D halfSize = GetScaledHalfSize();

        float tMin = 0.0f;
        float tMax = maxDistance;

        for (int i = 0; i < 3; i++)
        {
            float originCoord = (i == 0) ? localOrigin.x : (i == 1) ? localOrigin.y : localOrigin.z;
            float directionCoord = (i == 0) ? localDirection.x : (i == 1) ? localDirection.y : localDirection.z;
            float minBound = (i == 0) ? -halfSize.x : (i == 1) ? -halfSize.y : -halfSize.z;
            float maxBound = (i == 0) ? halfSize.x : (i == 1) ? halfSize.y : halfSize.z;

            if (Math.abs(directionCoord) < 1e-4f) // Parallel to the slab
            {
                if (originCoord < minBound || originCoord > maxBound)
                {
                    return null; // No intersection
                }
            }
            else
            {
                float t1 = (minBound - originCoord) / directionCoord;
                float t2 = (maxBound - originCoord) / directionCoord;

                if (t1 > t2)
                {
                    float temp = t1;
                    t1 = t2;
                    t2 = temp;
                }

                tMin = Math.max(tMin, t1);
                tMax = Math.min(tMax, t2);

                if (tMin > tMax)
                {
                    return null; // No intersection
                }
            }
        }

        if (tMin < 0 || tMin > maxDistance)
        {
            return null;
        }

        Vector3D collisionPointLocal = localOrigin.Add(localDirection.Scale(tMin));

        Vector3D normalLocal = Vector3D.Zero;
        if (Math.abs(collisionPointLocal.x - halfSize.x) < 1e-4f) normalLocal = new Vector3D(1, 0, 0);
        else if (Math.abs(collisionPointLocal.x + halfSize.x) < 1e-4f) normalLocal = new Vector3D(-1, 0, 0);
        else if (Math.abs(collisionPointLocal.y - halfSize.y) < 1e-4f) normalLocal = new Vector3D(0, 1, 0);
        else if (Math.abs(collisionPointLocal.y + halfSize.y) < 1e-4f) normalLocal = new Vector3D(0, -1, 0);
        else if (Math.abs(collisionPointLocal.z - halfSize.z) < 1e-4f) normalLocal = new Vector3D(0, 0, 1);
        else if (Math.abs(collisionPointLocal.z + halfSize.z) < 1e-4f) normalLocal = new Vector3D(0, 0, -1);

        if (normalLocal.IsZero())
        {
            return null; // Handle edge case if no valid normal is found
        }

        Vector3D collisionPointWorld = transform().LocalToWorldSpace(collisionPointLocal);
        Vector3D normalWorld = transform().LocalToWorldSpace(normalLocal).Normalized();

        return new RaycastContact(collisionPointWorld, normalWorld, GetRigidbody());
    }




    @Override
    protected WireshapeRenderer CreateWireframe()
    {
        WirecubeRenderer box = object().AddComponent(WirecubeRenderer.class);

        box.SetCenter(center);
        box.SetHalfSize(halfSize);

        return box;
    }

    @Override
    public BoundingVolume GetBoundingVolume()
    {
        return new BoundingBox(GetCenterWorld(), GetScaledHalfSize().Rotate(transform().GetGlobalRotation().ToRotationMatrix().Absolute()));
    }

    @Override
    public boolean CheckPoint(Vector3D point)
    {
        Vector3D scaledHalfSize = GetScaledHalfSize();
        Vector3D localPoint = transform().WorldToLocalSpace(point);

        return !(Math.abs(localPoint.x) > scaledHalfSize.x ||
                Math.abs(localPoint.y) > scaledHalfSize.y ||
                Math.abs(localPoint.z) > scaledHalfSize.z);
    }

    @Override
    public Contact GetContactPoint(Vector3D point, Rigidbody source)
    {
        Vector3D scaledHalfSize = GetScaledHalfSize();
        Vector3D localPoint = transform().WorldToLocalSpace(point);

        float minDepth = scaledHalfSize.x - Math.abs(localPoint.x);
        if(minDepth < 0) return null;
        Vector3D normal = transform().Right().Scale(Math.signum(localPoint.x));

        float depth = scaledHalfSize.y - Math.abs(localPoint.y);
        if(depth < 0) return null;
        else if (depth < minDepth)
        {
            minDepth = depth;
            normal = transform().Right().Scale(Math.signum(localPoint.y));
        }

        depth = scaledHalfSize.z - Math.abs(localPoint.z);
        if(depth < 0) return null;
        else if (depth < minDepth)
        {
            minDepth = depth;
            normal = transform().Right().Scale(Math.signum(localPoint.z));
        }

        return new Contact(GetRigidbody(), rigidbody, point, normal, minDepth);
    }
}
