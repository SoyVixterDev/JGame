package JGame.Engine.EventSystem;

import JGame.Engine.Internal.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to handle invoking two parameter events
 * @param <T1>
 * The type of the second parameter
 * @param <T2>
 * The type of the second parameter
 * @param <T3>
 * The type of the third parameter
 */
public class Event3PHandler<T1, T2, T3>
{
    /**
     * The listeners to be called when the event handler gets fired
     */
    private final List<Event3P<T1, T2, T3>> listeners = new ArrayList<>();

    public void Invoke(T1 param1, T2 param2, T3 param3)
    {
        for(Event3P<T1, T2, T3> listener : new ArrayList<>(listeners))
        {
            if(listener != null)
                listener.OnInvoke(param1, param2, param3);
            else
            {
                Logger.DebugWarning("Null listener found, deleting from list!");
                listeners.remove(listener);
            }
        }
    }

    /**
     * Adds the event listener to this event's listener lists
     * @param listener
     * The listener to add
     */
    public void Subscribe(Event3P<T1, T2, T3> listener)
    {
        if(listener == null)
            throw new IllegalArgumentException("Listener can't be null!");
        else
            listeners.add(listener);
    }
    /**
     * Remove the event listener from this event's listener lists
     * @param listener
     * The listener to remove
     */
    public void Unsubscribe(Event3P<T1, T2, T3> listener)
    {
        listeners.remove(listener);
    }
}