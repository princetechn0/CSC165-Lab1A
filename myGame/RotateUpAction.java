package myGame;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rage.game.*;
import ray.rml.*;
import net.java.games.input.Event;


public class RotateUpAction extends AbstractInputAction {

    private Camera camera;

    public RotateUpAction(Camera c) {
        camera = c;
    }

    public void performAction(float time, Event event) {
        System.out.println("camera rotate up initiated");
        Vector3f n = camera.getFd();
        Vector3f u = camera.getRt();
        Vector3f v = camera.getUp();

        Vector3 newV = (v.rotate(Degreef.createFrom(1.0f), u)).normalize();
        Vector3 newN = (n.rotate(Degreef.createFrom(1.0f), u)).normalize();

        camera.setUp((Vector3f) newV);
        camera.setFd((Vector3f) newN);
        
    }


    
}