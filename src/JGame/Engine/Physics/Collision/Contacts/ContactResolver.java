package JGame.Engine.Physics.Collision.Contacts;

import JGame.Engine.Settings;

import java.util.List;

public class ContactResolver
{
    public static void ResolveContacts(List<Contact> contacts)
    {
        int iterations = 0;

        while(iterations < Settings.Physics.contactResolutionLimit)
        {


            if(contacts.size() <= iterations)
                break;

            iterations--;
        }
    }
}
