package JGame.Engine.Physics.Collision.Detection;

import JGame.Engine.Basic.BaseObject;
import JGame.Engine.Physics.Collision.BoundingVolumeHierarchy.BVHManager;
import JGame.Engine.Physics.Collision.Contact.PotentialContact;

import java.util.List;

/**
 * Handles the first stage of collision detection, quickly eliminating unnecessary collision checks
 */
public class BroadCollisionDetection
{
    public final static BVHManager BVHTree = BaseObject.CreateInstance(BVHManager.class);

    public static List<PotentialContact> GetPotentialContacts()
    {
        return BVHTree.GetPotentialContacts();
    }
}
