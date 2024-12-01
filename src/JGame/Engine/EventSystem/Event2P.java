package JGame.Engine.EventSystem;

/**
 * Generic Class used for defining single parameter events that can be fired by an event handler
 * Create a new instance of Event and define the OnInvoke function inline
 * @param <T1>
 * The type of the fist parameter
 * @param <T2>
 * The type of the second parameter
 */
public abstract class Event2P<T1, T2>
{
    /**
     * The function to be invoked when the event listener gets called by the event handler
     */
    protected abstract void OnInvoke(T1 param1, T2 param2);
}