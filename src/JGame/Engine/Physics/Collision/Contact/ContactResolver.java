package JGame.Engine.Physics.Collision.Contact;

import JGame.Engine.Internal.Logger;
import JGame.Engine.Settings;
import JGame.Engine.Structures.Vector3D;

import java.util.ArrayList;
import java.util.List;

public class ContactResolver
{
    public static final float velocityEpsilon = 0.01f;
    public static final float positionEpsilon = 0.001f;

    /**
     * Performs both Interpenetration and Velocity contact resolutions
     * @param contacts
     * The contacts to resolve
     */
    public static void ResolveContacts(List<Contact> contacts)
    {
        if(contacts.isEmpty())
            return;

        PrepareContacts(contacts);

        if(contacts.isEmpty()) return;

        AdjustPositions(contacts);

        AdjustVelocities(contacts);
    }

    /**
     * Initializes contacts internal values and purges invalid ones from the list
     * @param contacts
     * The list of contacts
     */
    public static void PrepareContacts(List<Contact> contacts)
    {
        //If the contact isn't valid remove from the contacts list before resolving
        contacts.removeIf(contact ->  !contact.CalculateInternal());

    }

    static int posIterations = 0;
    /**
     * Performs the position contact resolution
     * @param contacts
     * The contacts to resolve
     */
    public static void AdjustPositions(List<Contact> contacts)
    {
        int numContacts = contacts.size();

        Vector3D[] linearChange;
        Vector3D[] angularChange;

        Vector3D deltaPos;

        posIterations = 0;
        int limit = Settings.Physics.ContactInterpenetrationResolutionLimit(numContacts);
        while(posIterations < limit)
        {
            float max = positionEpsilon;
            int index = numContacts;

            for(int i = 0; i < numContacts; i++)
            {
                //Logger.DebugLog("Penetration: " + contacts.get(i).penetration);
                if(contacts.get(i).penetration > max)
                {
                    max = contacts.get(i).penetration;
                    index = i;
                }
            }

            //Early-exit when no contact has enough penetration to be considered important
            if(index == numContacts)
            {
                //Logger.DebugLog("Early Exit! Pen: " + max);
                break;
            }

            Contact selectedContact = contacts.get(index);

            //Get the linear and angular changes and extract them
            var deltas = selectedContact.ApplyPositionChange();

            linearChange = deltas[0];
            angularChange = deltas[1];

            for(int i = 0; i < numContacts; i++)
            {
                Contact c = contacts.get(i);
                for(int b = 0; b < 2; b++) if(c.bodies[b] != null)
                {
                    for(int d = 0; d < 2; d++)
                    {
                        if(c.bodies[b] == selectedContact.bodies[d])
                        {
                            deltaPos = linearChange[b].Add(angularChange[b].CrossProduct(c.relativeContactPosition[b]));
                            c.penetration += deltaPos.DotProduct(c.contactNormal) * b == 1 ? 1 : -1;
                        }
                    }
                }
            }
            posIterations++;
        }
    }

    static int velIterations = 0;
    /**
     * Performs the velocity contact resolution
     * @param contacts
     * The contacts to resolve
     */
    public static void AdjustVelocities(List<Contact> contacts)
    {
        int numContacts = contacts.size();

        Vector3D[] linearChange;
        Vector3D[] angularChange;

        Vector3D deltaVel;

        velIterations = 0;
        int limit = Settings.Physics.ContactVelocityResolutionLimit(numContacts);
        while(velIterations < limit)
        {
            float max = velocityEpsilon;
            int index = numContacts;
            for(int i = 0; i < numContacts; i++)
            {
                if(contacts.get(i).desiredDeltaVelocity > max)
                {
                    max = contacts.get(i).desiredDeltaVelocity;
                    index = i;
                }
            }
            //Early-exit when no contact has enough penetration to be considered important
            if(index == numContacts) break;

            Contact selectedContact = contacts.get(index);

            //Get the linear and angular changes and extract them
            var deltas = selectedContact.ApplyVelocityChange();
            linearChange = deltas[0];
            angularChange = deltas[1];

            for(int i = 0; i < numContacts; i++)
            {
                Contact c = contacts.get(i);
                for(int b = 0; b < 2; b++) if(c.bodies[b] != null)
                {
                    for(int d = 0; d < 2; d++)
                    {
                        if(c.bodies[b] == selectedContact.bodies[d])
                        {
                            deltaVel = linearChange[d].Add(angularChange[d].CrossProduct(c.relativeContactPosition[b]));
                            c.contactVelocity = c.contactVelocity.Add(c.worldToContactMatrix.Multiply(deltaVel).Scale(b == 0 ? 1f : -1f));

                            c.CalculateDesiredDeltaVelocity();
                        }
                    }
                }
            }
            velIterations++;
        }
    }
}
