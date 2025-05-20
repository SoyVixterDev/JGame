package JGame.Engine.Physics.Raycast;

import JGame.Engine.Internal.Logger;
import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Physics.Collision.Colliders.BoxCollider;
import JGame.Engine.Physics.Collision.Colliders.Collider;
import JGame.Engine.Physics.Collision.Colliders.PlaneCollider;
import JGame.Engine.Physics.Collision.Colliders.SphereCollider;
import JGame.Engine.Physics.General.Physics;
import JGame.Engine.Physics.General.PhysicsObject;
import JGame.Engine.Structures.Vector3D;

public class Raycast
{
    /**
     * Casts a ray cast and returns a contact
     * @param origin
     * The origin
     * @param direction
     * The direction
     * @param maxDistance
     * THe max distance
     * @param ignoreTags
     * The tags to ignore
     * @return
     * The contact, or null if none are found
     */
    public static RaycastContact Raycast(Vector3D origin, Vector3D direction, float maxDistance, String... ignoreTags)
    {
        if (direction.Magnitude() <= 1e-6)
            throw new IllegalArgumentException("Direction must be a normalized vector.");

        direction = direction.Normalized();
        float closestDistance = maxDistance;
        RaycastContact closestContact = null;

        for (PhysicsObject obj : Physics.physicsObjects) if(obj instanceof Rigidbody rb)
        {
            RaycastContact contact = rb.Raycast(origin, direction, maxDistance, ignoreTags);
            if (contact == null) continue;

            float distance = Vector3D.Distance(origin, contact.point);
            if (distance < closestDistance)
            {
                closestDistance = distance;
                closestContact = contact;
            }
        }

        return closestContact;
    }
}
