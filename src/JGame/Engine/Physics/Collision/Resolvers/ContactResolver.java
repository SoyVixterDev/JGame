package JGame.Engine.Physics.Collision.Resolvers;

import JGame.Engine.Physics.Collision.Contacts.Contact;

/**
 * Resolver for particle contacts, only one instance needed
 */
public class ContactResolver
{
    public static final ContactResolver Instance = new ContactResolver();

    /**
     * The maximum allowed number of iterations
     */
    protected int maxIterations;

    /**
     * Debug value, showing how many iterations had actually been used
     */
    protected int usedIterations;

    protected ContactResolver() { }

    /**
     * Sets the maximum allowed number of iterations
     * @param iterations
     * The new maximum allowed number of iterations
     */
    public void SetMaxIterations(int iterations)
    {
        maxIterations = iterations;
    }

    /**
     * Resolves the contacts using the duration timeframe
     * @param contacts
     * The contacts to resolve
     * @param duration
     * The duration of the timeframe
     */
    public void ResolveContacts(Contact[] contacts, float duration)
    {
        usedIterations = 0;
        while(usedIterations <  maxIterations)
        {
            float maxClosingVel = 0;
            int maxIndex = contacts.length - 1;

            for(int i = 0; i < contacts.length; i++)
            {
                float contactVel = contacts[i].CalculateContactVelocity();
                if(contactVel < maxClosingVel)
                {
                    maxClosingVel = contactVel;
                    maxIndex = i;
                }
            }

            contacts[maxIndex].Resolve(duration);

            usedIterations++;
        }
    }

}
