package JGame.Engine.EventSystem;

/**
 * Class used for defining parameterless events that can be fired by an event handler
 * Create a new instance of Event and define the OnInvoke function inline
 */
public abstract class Event
{
    /**
     * The function to be invoked when the event listener gets called by the event handler
     */
    protected abstract void OnInvoke();
}