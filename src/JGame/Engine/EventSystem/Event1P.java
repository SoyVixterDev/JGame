package JGame.Engine.EventSystem;

/**
 * Generic Class used for defining 1p parameter events that can be fired by an event handler
 * Create a new instance of Event and define the OnInvoke function inline
 * @param <T>
 * Parameter type for the Invoke function
 */
public abstract class Event1P<T>
{
    /**
     * The function to be invoked when the event listener gets called by the event handler
     */
    protected abstract void OnInvoke(T param);
}