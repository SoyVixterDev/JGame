package JGame.Engine.Physics.Collision.Colliders;

import JGame.Engine.Graphics.Renderers.WireframeRenderers.WireshapeRenderer;
import JGame.Engine.Graphics.Renderers.WireframeRenderers.WiresphereRenderer;
import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingBox;
import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingVolume;
import JGame.Engine.Physics.Collision.Contacts.Contact;
import JGame.Engine.Structures.Vector3D;

public class SphereCollider extends Collider
{
    /**
     * Radius of the sphere
     */
    private float radius = 0.5f;

    public float GetRadius()
    {
        return radius;
    }

    public float GetScaledRadius()
    {
        Vector3D scale = transform().GetGlobalScale();
        float largestScale = Math.max(Math.max(scale.x, scale.y), scale.z);

        return largestScale * radius;
    }

    public void SetRadius(float radius)
    {
        this.radius = radius;
        ((WiresphereRenderer)colliderRenderer).SetRadius(radius);
    }
    @Override
    public void SetCenter(Vector3D center)
    {
        this.center = center;
        ((WiresphereRenderer)colliderRenderer).SetCenter(center);
    }

    @Override
    protected WireshapeRenderer CreateWireframe()
    {
        WiresphereRenderer sph = object().AddComponent(WiresphereRenderer.class);

        sph.SetRadius(radius);
        sph.SetCenter(center);

        return sph;
    }

    @Override
    public BoundingVolume GetBoundingVolume()
    {
        return new BoundingBox(GetCenterWorld(), Vector3D.One.Scale(GetScaledRadius()));
    }

    @Override
    public boolean CheckPoint(Vector3D point)
    {
        Vector3D localPoint = transform().GetGlobalPosition().Subtract(point);
        float scaledRadius =  GetScaledRadius();

        return localPoint.SquaredMagnitude() <= scaledRadius * scaledRadius;
    }

    @Override
    public Contact GetContactPoint(Vector3D point, Rigidbody source)
    {
        Vector3D globalPos = transform().GetGlobalPosition();
        Vector3D localPoint = globalPos.Subtract(point);
        float scaledRadius =  GetScaledRadius();
        float distanceFromCenter = localPoint.Magnitude();

        float depth = scaledRadius - distanceFromCenter;
        if(depth < 0) return null;
        Vector3D normal = distanceFromCenter == 0 ? Vector3D.Up : localPoint.Normalized();

        return new Contact(GetRigidbody(), source, point, normal, depth);
    }
}
