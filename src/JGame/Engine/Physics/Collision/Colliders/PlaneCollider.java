package JGame.Engine.Physics.Collision.Colliders;

import JGame.Engine.Graphics.Renderers.WireframeRenderers.WireplaneRenderer;
import JGame.Engine.Graphics.Renderers.WireframeRenderers.WireshapeRenderer;
import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingBox;
import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingVolume;
import JGame.Engine.Physics.Collision.Contacts.Contact;
import JGame.Engine.Structures.Vector2D;
import JGame.Engine.Structures.Vector3D;

public class PlaneCollider extends Collider
{
    /**
     * 2D Half size of the plane, represents X and Z coordinates in local space
     */
    private Vector2D halfSize = Vector2D.One.Scale(0.5f);

    public void SetHalfSize(Vector2D halfSize)
    {
        ((WireplaneRenderer)colliderRenderer).SetHalfSize(halfSize);
        this.halfSize = halfSize;
    }
    public Vector2D GetHalfSize()
    {
        return halfSize;
    }
    public Vector2D GetScaledHalfSize()
    {
        Vector3D scale = transform().GetGlobalScale();
        return halfSize.Multiply(new Vector2D(scale.x, scale.z));
    }
    public Vector3D GetNormal()
    {
        return transform().Up();
    }

    @Override
    protected WireshapeRenderer CreateWireframe()
    {
        WireplaneRenderer wireplaneRenderer = new WireplaneRenderer();

        wireplaneRenderer.SetHalfSize(halfSize);
        wireplaneRenderer.SetCenter(center);

        return wireplaneRenderer;
    }

    @Override
    public BoundingVolume GetBoundingVolume()
    {
        Vector2D scaledHalfSize = GetScaledHalfSize();
        Vector3D scaledHalfSize3D = new Vector3D(scaledHalfSize.x, 0.01f ,scaledHalfSize.y);

        return new BoundingBox(GetCenterWorld(), scaledHalfSize3D.Rotate(transform().GetGlobalRotation().ToRotationMatrix().Absolute()));
    }

    @Override
    public boolean CheckPoint(Vector3D point)
    {
        Vector2D scaledHalfSize = GetScaledHalfSize();
        Vector3D localPoint = transform().WorldToLocalSpace(point);

        return !(Math.abs(localPoint.x) > scaledHalfSize.x ||
                Math.abs(localPoint.y) > 1e-6 ||
                Math.abs(localPoint.z) > scaledHalfSize.y);
    }

    @Override
    public Contact GetContactPoint(Vector3D point, Rigidbody source)
    {
        if(!CheckPoint(point))
            return null;

        return new Contact(GetRigidbody(), rigidbody, point, GetNormal(), 0);
    }
}
