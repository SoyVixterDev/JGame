package Project.Scripts;

import JGame.Engine.Basic.JComponent;
import JGame.Engine.Internal.Time;
import JGame.Engine.Graphics.Misc.Camera;
import JGame.Engine.Structures.Quaternion;
import JGame.Engine.Structures.Vector3D;
import JGame.Engine.Input.Input;
import org.lwjgl.glfw.GLFW;

public class DroneController extends JComponent
{

    @Override
    public void Update()
    {
        float MOVEMENT_SPEED = 5f;
        float ROTATION_SPEED = 2f;


        if(Input.GetKeyHold(GLFW.GLFW_KEY_W))
        {
            Camera.Main.transform().SetGlobalPosition(Vector3D.Add(Camera.Main.transform().GetGlobalPosition(), Camera.Main.transform().Forward().Scale((float) Time.DeltaTime() * MOVEMENT_SPEED)));
        }
        if(Input.GetKeyHold(GLFW.GLFW_KEY_S))
        {
            Camera.Main.transform().SetGlobalPosition(Vector3D.Add(Camera.Main.transform().GetGlobalPosition(), Camera.Main.transform().Backward().Scale((float)Time.DeltaTime() * MOVEMENT_SPEED)));
        }
        if(Input.GetKeyHold(GLFW.GLFW_KEY_D))
        {
            Camera.Main.transform().SetGlobalPosition(Vector3D.Add(Camera.Main.transform().GetGlobalPosition(), Camera.Main.transform().Left().Scale((float)Time.DeltaTime() * MOVEMENT_SPEED)));
        }
        if(Input.GetKeyHold(GLFW.GLFW_KEY_A))
        {
            Camera.Main.transform().SetGlobalPosition(Vector3D.Add(Camera.Main.transform().GetGlobalPosition(), Camera.Main.transform().Right().Scale((float)Time.DeltaTime() * MOVEMENT_SPEED)));
        }

        if(Input.GetKeyHold(GLFW.GLFW_KEY_SPACE))
        {
            Camera.Main.transform().SetGlobalPosition(Vector3D.Add(Camera.Main.transform().GetGlobalPosition(), Camera.Main.transform().Up().Scale((float)Time.DeltaTime() * MOVEMENT_SPEED)));
        }
        if(Input.GetKeyHold(GLFW.GLFW_KEY_LEFT_CONTROL))
        {
            Camera.Main.transform().SetGlobalPosition(Vector3D.Add(Camera.Main.transform().GetGlobalPosition(), Camera.Main.transform().Down().Scale((float)Time.DeltaTime() * MOVEMENT_SPEED)));
        }

        if (Input.GetKeyHold(GLFW.GLFW_KEY_L))
        {
            Camera.Main.transform().RotateAxis(Vector3D.Up, -ROTATION_SPEED * (float)Time.DeltaTime());
        }
        if (Input.GetKeyHold(GLFW.GLFW_KEY_J))
        {
            Camera.Main.transform().RotateAxis(Vector3D.Up, ROTATION_SPEED * (float)Time.DeltaTime());
        }
        if (Input.GetKeyHold(GLFW.GLFW_KEY_K))
        {
            Camera.Main.transform().RotateAxis(Camera.Main.transform().Right(), -ROTATION_SPEED * (float)Time.DeltaTime());
        }
        if (Input.GetKeyHold(GLFW.GLFW_KEY_I))
        {
            Camera.Main.transform().RotateAxis(Camera.Main.transform().Right(), ROTATION_SPEED * (float)Time.DeltaTime());
        }


        if(Input.GetKeyDown(GLFW.GLFW_KEY_R))
        {
            Camera.Main.transform().SetGlobalRotation(Quaternion.Identity);
            Camera.Main.transform().SetGlobalPosition(Vector3D.Zero);
        }

    }
}
