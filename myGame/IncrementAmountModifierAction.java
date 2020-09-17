package myGame;


import ray.input.action.AbstractInputAction;
import ray.rage.game.*;
import net.java.games.input.Event;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.*;
import java.util.*;

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



public class IncrementAmountModifierAction extends AbstractInputAction {

    private MyGame game;
    private int incAmt = 1;

    public IncrementAmountModifierAction(MyGame g) {
        game = g;
    }

    public void performAction(float time, Event e) {
        System.out.println("modifier action initiated");
        incAmt++;
        if(incAmt == 5){
            incAmt = 1;
        }
    }

    protected int getIncAmt() { 
        return incAmt;
    }



}