package myGame;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.ArrayList;
import net.java.games.input.Controller;
import net.java.games.input.*;

import ray.rage.*;
import ray.rage.game.*;
import ray.rage.rendersystem.*;
import ray.rage.rendersystem.Renderable.*;
import ray.rage.scene.*;
import ray.rage.scene.Camera.Frustum.*;
import ray.rage.scene.controllers.*;
import ray.rml.*;
import ray.rage.rendersystem.gl4.GL4RenderSystem;

import ray.rage.rendersystem.states.*;
import ray.rage.asset.texture.*;
import ray.input.*;
import ray.input.action.*;
import ray.input.action.AbstractInputAction;
import ray.rage.game.*;
import net.java.games.input.Event;

import ray.rage.rendersystem.shader.*;
import ray.rage.util.*;



public class MyGame extends VariableFrameRateGame {

	// to minimize variable allocation in update()
	GL4RenderSystem rs;
	float elapsTime = 0.0f;
	String elapsTimeStr, planetsVisitedStr, dispStr;
    int elapsTimeSec, planetsVisited = 0;
    
    // new variables
    private InputManager im;
    private Action quitGameAction, incrementCounterAction, incAmtModAct;
    private Action  moveForwardAction, moveBackwardAction, moveLeftAction, moveRightAction, moveUpAction, moveDownAction, rotateRightAction, rotateLeftAction, rotateUpAction, rotateDownAction;

    // Java Functions
    Random rand = new Random();


    public MyGame() {
        super();
        System.out.println("press options or ESC to quit");
        System.out.println("X - Backwards \nTriangle - Forwards \nSquare - Left \nO - Right");
        System.out.println("L1 - Lower \nR1 - Raise");
        System.out.println("L2 - Rotate Camera Left \nR2 - Rotate Camera Right");
        
    }

    public static void main(String[] args) {
        Game game = new MyGame();   
        try {
            game.startup();
            game.run();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            game.shutdown();
            game.exit();
        }
    }
    
	
	@Override
	protected void setupWindow(RenderSystem rs, GraphicsEnvironment ge) {
		rs.createRenderWindow(new DisplayMode(1000, 700, 24, 60), false);
	}

    @Override
    protected void setupCameras(SceneManager sm, RenderWindow rw) {
        SceneNode rootNode = sm.getRootSceneNode();
        Camera camera = sm.createCamera("MainCamera", Projection.PERSPECTIVE);
        rw.getViewport(0).setCamera(camera);
		
		camera.setRt((Vector3f)Vector3f.createFrom(1.0f, 0.0f, 0.0f));
		camera.setUp((Vector3f)Vector3f.createFrom(0.0f, 1.0f, 0.0f));
		camera.setFd((Vector3f)Vector3f.createFrom(0.0f, 0.0f, -1.0f));
		
        camera.setPo((Vector3f)Vector3f.createFrom(0.0f, 0.0f, 0.0f));

        SceneNode cameraNode = rootNode.createChildSceneNode(camera.getName() + "Node");
        cameraNode.attachObject(camera);

        // Initializes Actions 
        setupInputActions(camera);
    }
	
    @Override
    protected void setupScene(Engine eng, SceneManager sm) throws IOException {
        setupInputs();

        Entity dolphinE = sm.createEntity("myDolphin", "dolphinHighPoly.obj");
        dolphinE.setPrimitive(Primitive.TRIANGLES);

        // Attaching Dolphin Entity to Node Object
        SceneNode dolphinN = sm.getRootSceneNode().createChildSceneNode(dolphinE.getName() + "Node");
        dolphinN.attachObject(dolphinE);

          // Randomize Dolphin Position 
          if(rand.nextInt(2) == 1){
            dolphinN.setLocalPosition(-3f, 2f, -6f);
        } else { 
            dolphinN.setLocalPosition(3f, 0f, -6f);
        }
        dolphinN.setLocalScale(2f, 2f, 2f);
        // dolphinN.yaw(Degreef.createFrom(180));


        // Setting up Spherical Entity Objects
        Entity earthE = sm.createEntity("myEarth", "earth.obj");
        earthE.setPrimitive(Primitive.TRIANGLES);
        Entity marioE = sm.createEntity("myMario", "sphere.obj");
        earthE.setPrimitive(Primitive.TRIANGLES);
        Entity sunE = sm.createEntity("mySun", "sphere.obj");
        earthE.setPrimitive(Primitive.TRIANGLES);
        
        // Attaching Planets to Node Object, assigning positions, and scaling
        SceneNode earthN = sm.getRootSceneNode().createChildSceneNode(earthE.getName() + "Node");
        earthN.attachObject(earthE);
        float[] positions = new float[] {-30f, 30f, -30f, 30f, -20f, 20f};
        planetRandomPosition(earthN, positions);
        earthN.setLocalScale(2f, 2f, 2f);

        SceneNode marioN = sm.getRootSceneNode().createChildSceneNode(marioE.getName() + "Node");
        marioN.attachObject(marioE);
        positions = new float[]{-45f, 45f, -45f, 45f, -35f, 35f};
        planetRandomPosition(marioN, positions);
        marioN.setLocalScale(3f, 3f, 3f);

        SceneNode sunN = sm.getRootSceneNode().createChildSceneNode(sunE.getName() + "Node");
        sunN.attachObject(sunE);
        positions = new float[] {-0f, 2f, 0f, 5f, -75f, -50f};
        planetRandomPosition(sunN, positions);
        sunN.setLocalScale(12f, 12f, 12f);

        
        // Manually attaching textures, rather than doing so through .mtl files - can use for
        // programmatically altering object texture based on condition
        TextureManager tm = eng.getTextureManager();
        Texture marioTexture = tm.getAssetByPath("mario.jpg");
        RenderSystem rs = sm.getRenderSystem();
        TextureState state = (TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
        state.setTexture(marioTexture);
        marioE.setRenderState(state);

        tm = eng.getTextureManager();
        Texture sunTexture = tm.getAssetByPath("sun.jpg");
        rs = sm.getRenderSystem();
        state = (TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
        state.setTexture(sunTexture);
        sunE.setRenderState(state);




        // // Creating Pyramid Object Manually through
        // ManualObject pyr = makePyramid(eng, sm);

        // // Attaching Pyramid to Node object
        // SceneNode pyrN = sm.getRootSceneNode().createChildSceneNode("PyrNode");
        // pyrN.scale(0.75f, 0.75f, 0.75f);
        // pyrN.setLocalPosition(1.0f, 1.0f, -3.0f);
        // pyrN.attachObject(pyr);

        // Cause Node Objects to Rotate
        RotationController rcD = new RotationController(Vector3f.createUnitVectorY(), .01f);
        rcD.addNode(dolphinN);
        RotationController rcE = new RotationController(Vector3f.createUnitVectorY(), .02f);
        rcE.addNode(earthN);
        RotationController rcM = new RotationController(Vector3f.createUnitVectorX(), .01f);
        rcM.addNode(marioN);
        RotationController rcS = new RotationController(Vector3f.createUnitVectorY(), .005f);
        rcS.addNode(sunN);

        sm.addController(rcD);
        sm.addController(rcE);
        sm.addController(rcM);
        sm.addController(rcS);
    
        // Set up Lights
        sm.getAmbientLight().setIntensity(new Color(.3f, .3f, .3f));
		Light plight = sm.createLight("testLamp1", Light.Type.POINT);
		plight.setAmbient(new Color(.1f, .1f, .1f));
        plight.setDiffuse(new Color(.7f, .7f, .7f));
		plight.setSpecular(new Color(1.0f, 1.0f, 1.0f));
        plight.setRange(100f);
		
		SceneNode plightNode = sm.getRootSceneNode().createChildSceneNode("plightNode");
        plightNode.attachObject(plight);
        plightNode.setLocalPosition(1.0f, 1.0f, 5.0f);
    }


    protected void setupInputs(){ 
        im = new GenericInputManager();
        ArrayList<Controller> controllers = im.getControllers();

        // Check which controller is connected
        for (Controller c : controllers) {
            if (c.getType() == Controller.Type.KEYBOARD){
                keyboardControls(c);
            } else if (c.getType() == Controller.Type.GAMEPAD || c.getType() == Controller.Type.STICK) {
                gamepadControls(c);
        }
    }

        // build some action objects
        quitGameAction = new QuitGameAction(this);
        incAmtModAct = new IncrementAmountModifierAction(this);

        // Send amount modifier to constructor for increment counter action  
        incrementCounterAction = new IncrementCounterAction(this, (IncrementAmountModifierAction) incAmtModAct);
    }


    @Override
    protected void update(Engine engine) {
		// build and set HUD
		rs = (GL4RenderSystem) engine.getRenderSystem();
		elapsTime += engine.getElapsedTimeMillis();
		elapsTimeSec = Math.round(elapsTime/1000.0f);
		elapsTimeStr = Integer.toString(elapsTimeSec);
		planetsVisitedStr = Integer.toString(planetsVisited);
		dispStr = "Time = " + elapsTimeStr + "   Score = " + planetsVisitedStr;
        rs.setHUD(dispStr, 15, 15);
        im.update(elapsTime);
    }





    protected ManualObject makePyramid(Engine eng, SceneManager sm) throws IOException {

        ManualObject pyr = sm.createManualObject("Pyramid");
        ManualObjectSection pyrSec = pyr.createManualSection("PyramidSection");
        pyr.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));

        float[] vertices = new float[]
            { -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f, //front
            1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, //right
            1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, //back
            -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f, //left
            -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, //LF
            1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f //RR
            };

        float[] texcoords = new float[]
            { 0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
            0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
            0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
            0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f
            };

        float[] normals = new float[]
            { 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f,
            0.0f, 1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f,
            0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f
            };


            int[] indices = new int[] { 0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17};

            FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
            FloatBuffer texBuf = BufferUtil.directFloatBuffer(texcoords);
            FloatBuffer normBuf = BufferUtil.directFloatBuffer(normals);
            IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);


            pyrSec.setVertexBuffer(vertBuf);
            pyrSec.setTextureCoordsBuffer(texBuf);
            pyrSec.setNormalsBuffer(normBuf);
            pyrSec.setIndexBuffer(indexBuf);


            // Setting texture of object
            Texture tex = eng.getTextureManager().getAssetByPath("chain-fence.jpeg");
            TextureState texState = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
            texState.setTexture(tex);
            FrontFaceState faceState = (FrontFaceState) sm.getRenderSystem().createRenderState(RenderState.Type.FRONT_FACE);

            pyr.setDataSource(DataSource.INDEX_BUFFER);
            pyr.setRenderState(texState);
            pyr.setRenderState(faceState);
            return pyr;
    }




    void setupInputActions(Camera camera) {
        moveForwardAction = new MoveForwardAction(camera);
        moveBackwardAction = new MoveBackwardAction(camera);
        moveLeftAction = new MoveLeftAction(camera);
        moveRightAction = new MoveRightAction(camera);
        moveUpAction = new MoveUpwardsAction(camera);
        moveDownAction = new MoveDownwardsAction(camera);
        rotateRightAction = new RotateRightAction(camera);
        rotateLeftAction = new RotateLeftAction(camera);
        rotateUpAction = new RotateUpAction(camera);
        rotateDownAction = new RotateDownAction(camera);

    }


    void gamepadControls(Controller gpName) {
          // attach the action objects to keyboard and gamepad components
        im.associateAction(gpName,
          net.java.games.input.Component.Identifier.Button._3,
          moveForwardAction,
          InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

      im.associateAction(gpName,
          net.java.games.input.Component.Identifier.Button._1,
          moveBackwardAction,
          InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

      im.associateAction(gpName,
          net.java.games.input.Component.Identifier.Axis.X,
          moveLeftAction,
          InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

       im.associateAction(gpName,
          net.java.games.input.Component.Identifier.Axis.Z,
          moveRightAction,
          InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

      im.associateAction(gpName,
          net.java.games.input.Component.Identifier.Button._5,
          moveUpAction,
          InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

      im.associateAction(gpName,
          net.java.games.input.Component.Identifier.Button._4,
          moveDownAction,
          InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

      im.associateAction(gpName,
          net.java.games.input.Component.Identifier.Button._9,
          quitGameAction,
          InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);

      im.associateAction(gpName,
          net.java.games.input.Component.Identifier.Button._11,
          incrementCounterAction,
          InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);

      im.associateAction(gpName,
          net.java.games.input.Component.Identifier.Button._10,
          incAmtModAct,
          InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);

      im.associateAction(gpName,
          net.java.games.input.Component.Identifier.Button._7,
          rotateRightAction,
          InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

      im.associateAction(gpName,
          net.java.games.input.Component.Identifier.Button._6 ,
          rotateLeftAction,
          InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        im.associateAction(gpName,
          net.java.games.input.Component.Identifier.Button._13 ,
          rotateUpAction,
          InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        im.associateAction(gpName,
          net.java.games.input.Component.Identifier.Button._15 ,
          rotateDownAction,
          InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);  

    }

    void keyboardControls(Controller kbName) {
          // Keyboard Controls
        im.associateAction(kbName,
            net.java.games.input.Component.Identifier.Key.A,
            moveLeftAction,
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        im.associateAction(kbName,
            net.java.games.input.Component.Identifier.Key.D,
            moveRightAction,
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        im.associateAction(kbName,
            net.java.games.input.Component.Identifier.Key.W,
            moveForwardAction,
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        im.associateAction(kbName,
            net.java.games.input.Component.Identifier.Key.S,
            moveBackwardAction,
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        im.associateAction(kbName,
            net.java.games.input.Component.Identifier.Key.LEFT,
            rotateLeftAction,
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        im.associateAction(kbName,
            net.java.games.input.Component.Identifier.Key.RIGHT,
            rotateRightAction,
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        im.associateAction(kbName,
            net.java.games.input.Component.Identifier.Key.UP,
            rotateUpAction,
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        im.associateAction(kbName,
            net.java.games.input.Component.Identifier.Key.DOWN,
            rotateDownAction,
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    }


        
    float randomPlanetPosition(float min, float max) {
        return (min + rand.nextFloat() * (max - min));
    }
    
    void planetRandomPosition(SceneNode node, float[] positions) {
        node.setLocalPosition(randomPlanetPosition(positions[0], positions[1]), randomPlanetPosition(positions[2], positions[3]), randomPlanetPosition(positions[4], positions[5]));

    }

       
    void incrementCounter(int amt) {
        planetsVisited+=amt;
    }





}



