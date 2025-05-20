package JGame.Engine.Physics.Collision.Colliders;

import JGame.Engine.Graphics.Renderers.WireframeRenderers.WireshapeRenderer;
import JGame.Engine.Graphics.Renderers.WireframeRenderers.WiresphereRenderer;
import JGame.Engine.Internal.Logger;
import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingBox;
import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingVolume;
import JGame.Engine.Physics.Collision.Contact.Contact;
import JGame.Engine.Physics.Raycast.RaycastContact;
import JGame.Engine.Structures.Vector3D;
import JGame.Engine.Utilities.MathUtilities;

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
        scale = MathUtilities.Abs(scale);
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
    public RaycastContact Raycast(Vector3D origin, Vector3D direction, float maxDistance)
    {
        Vector3D center = this.GetCenterWorld();
        float radius = this.GetScaledRadius();

        Vector3D oc = origin.Subtract(center);

        float a = direction.DotProduct(direction);
        float b = 2.0f * oc.DotProduct(direction);
        float c = oc.DotProduct(oc) - radius * radius;

        float discriminant = b * b - 4 * a * c;

        if (discriminant < 0)
        {
            return null;
        }

        float sqrtDiscriminant = (float) Math.sqrt(discriminant);
        float t1 = (-b - sqrtDiscriminant) / (2.0f * a);
        float t2 = (-b + sqrtDiscriminant) / (2.0f * a);

        float t = (t1 >= 0 && t1 <= maxDistance) ? t1 : (t2 >= 0 && t2 <= maxDistance) ? t2 : -1;

        if (t < 0)
        {
            return null; // Intersection is out of range
        }

        Vector3D hitPoint = origin.Add(direction.Scale(t));

        Vector3D normal = hitPoint.Subtract(center).Normalized();

        RaycastContact contact = new RaycastContact(hitPoint, normal, this.GetRigidbody());

        return contact;
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
