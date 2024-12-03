package JGame.Engine.Physics.Collision.Colliders;

import JGame.Engine.Basic.JGameObject;
import JGame.Engine.Graphics.Renderers.WireframeRenderers.WirecubeRenderer;
import JGame.Engine.Graphics.Renderers.WireframeRenderers.WireshapeRenderer;
import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingBox;
import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingVolume;
import JGame.Engine.Physics.Collision.Contacts.Contact;
import JGame.Engine.Structures.Quaternion;
import JGame.Engine.Structures.Vector3D;

import java.util.List;
import java.util.Vector;

public class BoxCollider extends Collider
{
    /**
     * Center of the box, in local space
     */
    private Vector3D center = Vector3D.Zero;

    /**
     * Half size of the box
     */
    private Vector3D halfSize = Vector3D.One.Scale(0.5f);

    public Vector3D GetHalfSize()
    {
        return halfSize;
    }

    public void SetHalfSize(Vector3D halfSize)
    {
        this.halfSize = halfSize;
        ((WirecubeRenderer)colliderRenderer).SetHalfSize(halfSize);
    }

    public Vector3D GetCenter()
    {
        return center;
    }

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
        Vector3D globalPosition = transform().GetGlobalPosition();
        Quaternion globalRotation = transform().GetGlobalRotation();
        Vector3D scaledHalfSize = halfSize.Multiply(transform().GetGlobalScale());

        Vector3D boundHalfSize =  scaledHalfSize.Rotate(globalRotation.ToRotationMatrix().Absolute());
        Vector3D boundCenter = center.Add(globalPosition);

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
