package JGame.Engine.Physics.Collision.Detection;

import JGame.Engine.Physics.Collision.Contacts.Contact;
import JGame.Engine.Physics.Collision.Contacts.PotentialContact;
import JGame.Engine.Settings;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the Fine Collision Phase of Collision Detection, getting contacts from potential contacts
 */
public class FineCollisionDetection
{
    /**
     * Gets contacts from the list of potential contacts
     * @param potentialContacts
     * The potential contacts
     * @return
     * A list of contacts, or an empty list if none were found
     */
    public static List<Contact> GetContacts(List<PotentialContact> potentialContacts)
    {
        List<Contact> contacts = new ArrayList<>();

        for(PotentialContact potentialContact : potentialContacts)
        {
            contacts.addAll(potentialContact.GetContacts(Settings.Physics.fineCollisionLimit));
        }

        return contacts;
    }
}
