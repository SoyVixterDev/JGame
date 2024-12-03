package JGame.Engine.Physics.Collision.Colliders;

import JGame.Engine.Basic.JGameObject;
import JGame.Engine.Graphics.Renderers.WireframeRenderers.WirecubeRenderer;
import JGame.Engine.Graphics.Renderers.WireframeRenderers.WireshapeRenderer;
import JGame.Engine.Graphics.Renderers.WireframeRenderers.WiresphereRenderer;
import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingBox;
import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingVolume;
import JGame.Engine.Physics.Collision.Contacts.Contact;
import JGame.Engine.Structures.Vector3D;

import java.util.List;

public class SphereCollider extends Collider
{

    /**
     * Radius of the sphere
     */
    private float radius = 0.5f;
    /**
     * Center of the collider, in local space
     */
    private Vector3D center = Vector3D.Zero;

    public float GetRadius()
    {
        return radius;
    }

    public void SetRadius(float radius)
    {
        this.radius = radius;
        ((WiresphereRenderer)colliderRenderer).SetRadius(radius);
    }

    public Vector3D GetCenter()
    {
        return center;
    }

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
        Vector3D scale =  transform().GetGlobalScale();
        float largestScale = Math.max(Math.max(scale.x, scale.y), scale.z);

        Vector3D boundHalfSize =  Vector3D.One.Scale(radius * largestScale);
        Vector3D boundCenter = center.Add(transform().GetGlobalPosition());

        return new BoundingBox(boundCenter, boundHalfSize);
    }

    @Override
    public List<Contact> GetContacts(BoxCollider boxCollider)
    {
        return List.of();
    }

    @Override
    public List<Contact> GetContacts(SphereCollider sphereCollider)
    {
        return List.of();
    }
}
