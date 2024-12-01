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
 */
public class Event2PHandler<T1, T2>
{
    /**
     * The listeners to be called when the event handler gets fired
     */
    private final List<Event2P<T1, T2>> listeners = new ArrayList<>();

    public void Invoke(T1 param1, T2 param2)
    {
        List<Integer> nullListeners = new ArrayList<>();
        int currentIndex = 0;
        for(Event2P<T1, T2> listener : listeners)
        {
            if(listener != null)
                listener.OnInvoke(param1, param2);
            else
            {
                Logger.DebugWarning("Null listener found, deleting from list!");
                nullListeners.add(currentIndex);
            }
            currentIndex++;
        }

        for(int index : nullListeners)
        {
            listeners.remove(index);
        }
    }

    /**
     * Adds the event listener to this event's listener lists
     * @param listener
     * The listener to add
     */
    public void Subscribe(Event2P<T1, T2> listener)
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
    public void Unsubscribe(Event2P<T1, T2> listener)
    {
        listeners.remove(listener);
    }
}