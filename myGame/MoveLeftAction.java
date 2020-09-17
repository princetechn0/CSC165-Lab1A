package myGame;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rage.game.*;
import ray.rml.*;
import net.java.games.input.Event;


public class MoveLeftAction extends AbstractInputAction {

    private Camera camera;

    public MoveLeftAction(Camera c) {
        camera = c;
    }

    public void performAction(float time, Event event) {
        System.out.println("camera movement left initiated");
        Vector3f n = camera.getRt();
        Vector3f p = camera.getPo();
        Vector3f p1 = (Vector3f) Vector3f.createFrom(0.05f*n.x(), 0.05f*n.y(), 0.05f*n.z());
        Vector3f p2 = (Vector3f) p.sub((Vector3f) p1);

        camera.setPo(p2);

        
    }


    
}