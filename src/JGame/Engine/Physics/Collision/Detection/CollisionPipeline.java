package JGame.Engine.Physics.Collision.Detection;

import JGame.Engine.Internal.Logger;
import JGame.Engine.Physics.Collision.Contact.Contact;
import JGame.Engine.Physics.Collision.Contact.ContactResolver;
import JGame.Engine.Physics.Collision.Contact.PotentialContact;

import java.util.List;

/**
 * Handles the complete Collision Detection, Contact Generation and Contact Resolution Process
 */
public class CollisionPipeline
{
    /**
     * Runs the collision pipeline, obtaining potential contacts, then generating actual contacts, and then finally resolving the contacts
     */
    public static void RunPipeline()
    {
        List<PotentialContact> potentialContacts = BroadCollisionDetection.GetPotentialContacts();

        List<Contact> contacts = FineCollisionDetection.GetContacts(potentialContacts);

        ContactResolver.ResolveContacts(contacts);
    }
}
