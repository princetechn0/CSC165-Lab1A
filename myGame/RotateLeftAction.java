package myGame;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rage.game.*;
import ray.rml.*;
import net.java.games.input.Event;


public class RotateLeftAction extends AbstractInputAction {

    private Camera camera;

    public RotateLeftAction(Camera c) {
        camera = c;
    }

    public void performAction(float time, Event event) {
        System.out.println("camera rotate left initiated");
        Vector3f n = camera.getFd();
        Vector3f u = camera.getRt();
        Vector3f v = camera.getUp();

        Vector3 newU = (u.rotate(Degreef.createFrom(1.0f), v)).normalize();
        Vector3 newN = (n.rotate(Degreef.createFrom(1.0f), v)).normalize();

        camera.setRt((Vector3f) newU);
        camera.setFd((Vector3f) newN);
        
    }


    
}