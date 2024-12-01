package Project.Scripts;

import JGame.Engine.Basic.JComponent;
import JGame.Engine.Basic.JGameObject;
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
            Camera.Main.Transform().SetGlobalPosition(Vector3D.Add(Camera.Main.Transform().GetGlobalPosition(), Camera.Main.Transform().Forward().Scale((float) Time.DeltaTime() * MOVEMENT_SPEED)));
        }
        if(Input.GetKeyHold(GLFW.GLFW_KEY_S))
        {
            Camera.Main.Transform().SetGlobalPosition(Vector3D.Add(Camera.Main.Transform().GetGlobalPosition(), Camera.Main.Transform().Backward().Scale((float)Time.DeltaTime() * MOVEMENT_SPEED)));
        }
        if(Input.GetKeyHold(GLFW.GLFW_KEY_D))
        {
            Camera.Main.Transform().SetGlobalPosition(Vector3D.Add(Camera.Main.Transform().GetGlobalPosition(), Camera.Main.Transform().Left().Scale((float)Time.DeltaTime() * MOVEMENT_SPEED)));
        }
        if(Input.GetKeyHold(GLFW.GLFW_KEY_A))
        {
            Camera.Main.Transform().SetGlobalPosition(Vector3D.Add(Camera.Main.Transform().GetGlobalPosition(), Camera.Main.Transform().Right().Scale((float)Time.DeltaTime() * MOVEMENT_SPEED)));
        }

        if(Input.GetKeyHold(GLFW.GLFW_KEY_SPACE))
        {
            Camera.Main.Transform().SetGlobalPosition(Vector3D.Add(Camera.Main.Transform().GetGlobalPosition(), Camera.Main.Transform().Up().Scale((float)Time.DeltaTime() * MOVEMENT_SPEED)));
        }
        if(Input.GetKeyHold(GLFW.GLFW_KEY_LEFT_CONTROL))
        {
            Camera.Main.Transform().SetGlobalPosition(Vector3D.Add(Camera.Main.Transform().GetGlobalPosition(), Camera.Main.Transform().Down().Scale((float)Time.DeltaTime() * MOVEMENT_SPEED)));
        }

        if (Input.GetKeyHold(GLFW.GLFW_KEY_L))
        {
            Camera.Main.Transform().RotateAxis(Vector3D.Up, -ROTATION_SPEED * (float)Time.DeltaTime());
        }
        if (Input.GetKeyHold(GLFW.GLFW_KEY_J))
        {
            Camera.Main.Transform().RotateAxis(Vector3D.Up, ROTATION_SPEED * (float)Time.DeltaTime());
        }
        if (Input.GetKeyHold(GLFW.GLFW_KEY_K))
        {
            Camera.Main.Transform().RotateAxis(Camera.Main.Transform().Right(), -ROTATION_SPEED * (float)Time.DeltaTime());
        }
        if (Input.GetKeyHold(GLFW.GLFW_KEY_I))
        {
            Camera.Main.Transform().RotateAxis(Camera.Main.Transform().Right(), ROTATION_SPEED * (float)Time.DeltaTime());
        }


        if(Input.GetKeyDown(GLFW.GLFW_KEY_R))
        {
            Camera.Main.Transform().SetGlobalRotation(Quaternion.Identity);
            Camera.Main.Transform().SetGlobalPosition(Vector3D.Zero);
        }

    }
}
