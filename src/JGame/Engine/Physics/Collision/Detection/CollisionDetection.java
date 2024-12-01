package JGame.Engine.Physics.Collision.Detection;

import JGame.Engine.Physics.Collision.Contacts.PotentialContact;

import java.util.List;

/**
 * Handles the complete Collision Detection Process
 */
public class CollisionDetection
{
    public static void HandleCollisions()
    {
        List<PotentialContact> potentialContacts = BroadCollisionDetection.GetPotentialContacts();


    }
}
