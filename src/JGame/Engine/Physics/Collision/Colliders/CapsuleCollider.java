package JGame.Engine.Physics.Collision.Colliders;

import JGame.Engine.Graphics.Renderers.WireframeRenderers.WireshapeRenderer;
import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingVolume;
import JGame.Engine.Physics.Collision.Contacts.Contact;
import JGame.Engine.Structures.Vector3D;

public class CapsuleCollider extends Collider
{

    @Override
    protected WireshapeRenderer CreateWireframe()
    {
        return null;
    }

    @Override
    public BoundingVolume GetBoundingVolume()
    {
        return null;
    }

    @Override
    public boolean CheckPoint(Vector3D point)
    {
        return false;
    }

    @Override
    public Contact GetContactPoint(Vector3D point, Rigidbody source)
    {
        return null;
    }
}
