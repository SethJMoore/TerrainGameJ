package com.sethjmoore.terraingame;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class Game {
    public static Random rand;
    final int SCALE = 4;
    private int speed;
    private int updateFrequency = 1;

    private int mapWidth, mapHeight;
    private Terrain terrain;
    Texture terrainTexture;

    long turnNumber;

    public void start() {
        initialize();
        loadContent();

        while (!Display.isCloseRequested()) {

            while(Keyboard.next()){
                switch (Keyboard.getEventKey()){
                    case Keyboard.KEY_S:
                        if (Keyboard.getEventKeyState()) {
                            terrain.smoothLinearly();
                            terrainTexture = terrain.createTexture();
                        }
                        break;
                    case Keyboard.KEY_V:
                        if (Keyboard.getEventKeyState()) {
                            terrain.addNewRandomCritter();
                        }
                        break;
                    case Keyboard.KEY_P:
                        if (Keyboard.getEventKeyState()){
                            for (int i = 0; i < 100; i++) {
                                terrain.addNewRandomCritter();
                            }
                        }
                        break;
                    case Keyboard.KEY_ESCAPE:
                        if (Keyboard.getEventKeyState()) {
                            terrain.clearOccupants();
                            terrain.randomize();
                            terrain.smoothLinearly();
                            terrainTexture = terrain.createTexture();
                        }
                        break;
                    case Keyboard.KEY_SPACE:
                        if (Keyboard.getEventKeyState()) {
                            terrain.randomize();
                            terrain.smoothLinearly();
                            terrainTexture = terrain.createTexture();
                        }
                        break;
                    case Keyboard.KEY_EQUALS:
                    case Keyboard.KEY_ADD:
                        if (Keyboard.getEventKeyState()) {
                            if (speed > 0) {
                                speed--;
                            }
                        }
                        break;
                    case Keyboard.KEY_MINUS:
                        if (Keyboard.getEventKeyState()) {
                            speed++;
                        }
                        break;
                    case Keyboard.KEY_DOWN:
                        if (terrain.occupiedBySouthOf(terrain.getRider().getX(), terrain.getRider().getY()) != null){
                            terrain.getRider().mountCritter(terrain.occupiedBySouthOf(terrain.getRider().getX(), terrain.getRider().getY()));
                        }
                        break;
                    case Keyboard.KEY_UP:
                        if (terrain.occupiedByNorthOf(terrain.getRider().getX(), terrain.getRider().getY()) != null){
                            terrain.getRider().mountCritter(terrain.occupiedByNorthOf(terrain.getRider().getX(), terrain.getRider().getY()));
                        }
                        break;
                    case Keyboard.KEY_LEFT:
                        if (terrain.occupiedByWestOf(terrain.getRider().getX(), terrain.getRider().getY()) != null){
                            terrain.getRider().mountCritter(terrain.occupiedByWestOf(terrain.getRider().getX(), terrain.getRider().getY()));
                        }
                        break;
                    case Keyboard.KEY_RIGHT:
                        if (terrain.occupiedByEastOf(terrain.getRider().getX(), terrain.getRider().getY()) != null){
                            terrain.getRider().mountCritter(terrain.occupiedByEastOf(terrain.getRider().getX(), terrain.getRider().getY()));
                        }
                        break;
                }
            }
            update();
            
            // Clear the screen and depth buffer
            draw();

            Display.update();
        }

        Display.destroy();
    }

    private void draw() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        // set the color of the quad (R,G,B,A)
        GL11.glColor3f(0.5f, 0.5f, 1.0f);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        // draw quad
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrainTexture.getTextureHandle());
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex2f(0, 0);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex2f(mapWidth * SCALE - 0, 0);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex2f(mapWidth * SCALE - 0, mapHeight * SCALE);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex2f(0, mapHeight * SCALE);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glPointSize(1.0f * SCALE);
        GL11.glBegin(GL11.GL_POINTS);
        for (Critter c : terrain.getAllTheCritters()){
            if (c.getCritterType() == Critter.CritterType.Climber){
                GL11.glColor3f(0.0f, 0.0f, 1.0f);
            }
            else {
                GL11.glColor3f(1.0f, 0.0f, 0.0f);
            }
            GL11.glVertex2f(c.getX() * SCALE + SCALE/2, c.getY() * SCALE + SCALE/2);
        }
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glVertex2f(terrain.getRider().getX() * SCALE + SCALE/2, terrain.getRider().getY() * SCALE + SCALE/2);
        GL11.glEnd();
    }

    private void update() {
        if (turnNumber % updateFrequency == 0) {
            if (turnNumber / updateFrequency % 100 == 0){
                terrain.smoothLinearly();
                terrainTexture = terrain.createTexture();
            }
            terrain.update();
        }
        turnNumber++;

        try {
            Thread.sleep(speed);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void loadContent() {
    }

    private void initialize() {

        turnNumber = 0;
        rand = new Random();
        mapWidth = 200;
        mapHeight = 200;

        try {
            Display.setDisplayMode(new DisplayMode(mapWidth * SCALE, mapHeight * SCALE));
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        // init OpenGL
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, mapWidth * SCALE, 0, mapHeight * SCALE, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        terrain = new Terrain(mapWidth, mapHeight);
        terrain.randomize();
        for (int i = 0; i < 200; i++) {
            terrain.smoothLinearly();
        }
        terrainTexture = terrain.createTexture();

        speed = 20;

    }

    public static void main(String[] argv) {
        Game game = new Game();
        game.start();
    }
}

