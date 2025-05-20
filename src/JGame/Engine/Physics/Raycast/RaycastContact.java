package JGame.Engine.Physics.Raycast;

import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Structures.Vector3D;

public class RaycastContact
{
    public final Vector3D point;
    public final Vector3D normal;
    public final Rigidbody rigidbody;

    public RaycastContact(Vector3D point, Vector3D normal, Rigidbody rigidbody)
    {
        this.point = point;
        this.normal = normal;
        this.rigidbody = rigidbody;
    }

    @Override
    public String toString()
    {
        return "RaycastContact{" + "point=" + point + ", normal=" + normal + ", rigidbody=" + rigidbody + '}';
    }
}
