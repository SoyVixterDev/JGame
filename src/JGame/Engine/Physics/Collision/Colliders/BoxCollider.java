package JGame.Engine.Physics.Collision.Colliders;

import JGame.Engine.Graphics.Renderers.WireframeRenderers.WirecubeRenderer;
import JGame.Engine.Graphics.Renderers.WireframeRenderers.WireshapeRenderer;
import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingBox;
import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingVolume;
import JGame.Engine.Physics.Collision.Contacts.Contact;
import JGame.Engine.Structures.Vector3D;

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
        return halfSize.Multiply(transform().GetGlobalScale());
    }

    public void SetHalfSize(Vector3D halfSize)
    {
        this.halfSize = halfSize;
        ((WirecubeRenderer)colliderRenderer).SetHalfSize(halfSize);
    }
    @Override
    public void SetCenter(Vector3D center)
    {
        this.center = center;
        ((WirecubeRenderer)colliderRenderer).SetCenter(center);
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
