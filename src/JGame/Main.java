package JGame;

import JGame.Application.Application;
import JGame.Engine.Internal.Logger;

public class Main
{
    /**
     * Starts the application
     */
    public static void main(String[] args)
    {
        Logger.DebugLog("Starting Application...");
        Application app = new Application(); //Starts a window and opens the application, starts logic
    }
}

