package JGame.Engine.EventSystem;

import JGame.Engine.Internal.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to handle invoking parameterless events
 */
public class EventHandler
{
    /**
     * The listeners to be called when the event handler gets fired
     */
    private final List<Event> listeners = new ArrayList<>();

    public void Invoke()
    {
        List<Integer> nullListeners = new ArrayList<>();
        int currentIndex = 0;
        for(Event listener : listeners)
        {
            if(listener != null)
                listener.OnInvoke();
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
    public void Subscribe(Event listener)
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
    public void Unsubscribe(Event listener)
    {
        listeners.remove(listener);
    }
}