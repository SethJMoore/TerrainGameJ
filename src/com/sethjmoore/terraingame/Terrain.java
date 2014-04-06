package com.sethjmoore.terraingame;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by Seth on 3/27/2014.
 */
public class Terrain {
    private int[] terrainHeightMap;
    private Critter[] occupiedBy;
    private List<Critter> allTheCritters;
    private Rider rider;
    private int width;
    private int height;

    public int[] getTerrainHeightMap() {
        return terrainHeightMap;
    }

    public Rider getRider() {
        return rider;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<Critter> getAllTheCritters() {
        return allTheCritters;
    }

    public Terrain(int w, int h) {
        Game.rand = new Random();
        this.width = w;
        this.height = h;
        terrainHeightMap = new int[w * h];
        occupiedBy = new Critter[w * h];
        allTheCritters = new LinkedList<Critter>();
        rider = new Rider();
    }

    public void randomize() {
        for (int i = 0; i < terrainHeightMap.length; i++) {
            terrainHeightMap[i] = Game.rand.nextInt(255) * 256;
        }
    }

    int altitude(int x, int y){
        return terrainHeightMap[x + y * width];
    }

    int altitudeSouthOf(int x, int y){
        if (y - 1 >= 0){
            return altitude(x, y - 1);
        }
        else {
            return Integer.MAX_VALUE;
        }
    }
    int altitudeNorthOf(int x, int y){
        if (y + 1 < height){
            return altitude(x, y + 1);
        }
        else {
            return Integer.MAX_VALUE;
        }
    }
    int altitudeWestOf(int x, int y){
        if (x - 1 >= 0){
            return altitude(x - 1, y);
        }
        else {
            return Integer.MAX_VALUE;
        }
    }
    int altitudeEastOf(int x, int y){
        if (x - 1 < width){
            return altitude(x + 1, y);
        }
        else {
            return Integer.MAX_VALUE;
        }
    }

    void smoothLinearly() {
        for (int i = 0; i < terrainHeightMap.length; i++) {
            averageFromNeighbors(i);
        }
    }

    private void averageFromNeighbors(int locationToSmooth) {
        int neighborsFound = 0;
        int sum = 0;
        if (locationToSmooth / width > 0){
            sum += terrainHeightMap[locationToSmooth - width];
            neighborsFound++;
        }
        if (locationToSmooth / width < height - 1) {
            sum += terrainHeightMap[locationToSmooth + width];
            neighborsFound++;
        }
        if (locationToSmooth % width > 0) {
            sum += terrainHeightMap[locationToSmooth - 1];
            neighborsFound++;
        }
        if (locationToSmooth % width < width - 1) {
            sum += terrainHeightMap[locationToSmooth + 1];
            neighborsFound++;
        }
        terrainHeightMap[locationToSmooth] = (sum / neighborsFound);
    }

    void occupy(int x, int y, Critter critter){
        occupiedBy[x + y * width] = critter;
    }

    void vacate(int x, int y){
        occupiedBy[x + y * width] = null;
    }

    boolean isOccupied(int x, int y){
        return occupiedBy[x + y * width] != null;
    }

    void clearOccupants(){
        occupiedBy = new Critter[width * height];
        allTheCritters.clear();
    }

    void addNewRandomCritter(){
        Critter c = new Critter();
        boolean success = false;
        do {
            int x = Game.rand.nextInt(width);
            int y = Game.rand.nextInt(height);
            if (!isOccupied(x, y)){
                c.setX(x);
                c.setY(y);
                occupy(x, y, c);
                allTheCritters.add(c);
                success = true;
            }
        } while (!success);
    }

    void update(){
        Critter[] critterArray = allTheCritters.toArray(new Critter[allTheCritters.size()]);
        for (Critter c : critterArray){
            c.update(this);
        }
        rider.update(this);
    }

    boolean isOccupiedSouthOf(int x, int y) {
        if (y == 0) return true;
        return isOccupied(x, y - 1);
    }

    boolean isOccupiedNorthOf(int x, int y) {
        if (y == height - 1) return true;
        return isOccupied(x, y + 1);
    }

    boolean isOccupiedWestOf(int x, int y) {
        if (x == 0) return true;
        return isOccupied(x - 1, y);
    }

    boolean isOccupiedEastOf(int x, int y) {
        if (x == width - 1) return true;
        return isOccupied(x + 1, y);
    }

    void addCritter(int x, int y, Critter critter){
        critter.setX(x);
        critter.setY(y);
        occupy(x, y, critter);
        allTheCritters.add(critter);
    }

    void removeCritter(Critter critter){
        vacate(critter.getX(), critter.getY());
        allTheCritters.remove(critter);
    }

    Critter occupiedBySouthOf(int x, int y) {
        if (y > 0)
            return occupiedBy(x, y - 1);
        else
            return null;
    }
    Critter occupiedByNorthOf(int x, int y) {
        if (y < height - 1)
            return occupiedBy(x, y + 1);
        else
            return null;
    }
    Critter occupiedByWestOf(int x, int y) {
        if (x > 0)
            return occupiedBy(x - 1, y);
        else
            return null;
    }
    Critter occupiedByEastOf(int x, int y) {
        if (x < width - 1)
            return occupiedBy(x + 1, y);
        else
            return null;
    }

    Critter occupiedBy(int x, int y) {
        return occupiedBy[x + y * width];
    }

    Critter randomCritter(){
        if (!allTheCritters.isEmpty()){
            return allTheCritters.get(Game.rand.nextInt(allTheCritters.size()));
        }
        else {
            return null;
        }
    }

    public Texture createTexture() {
        byte[] rgbaArray = new byte[terrainHeightMap.length * 4];
        for (int i = 0; i < terrainHeightMap.length; i++) {
            //for a green map:
            rgbaArray[i*4 + 0] = 0;
            rgbaArray[i*4 + 1] = (byte)(terrainHeightMap[i] % 256);
            rgbaArray[i*4 + 2] = 0;
            rgbaArray[i*4 + 3] = (byte)255;

            //for a purplish map:
//            rgbaArray[i*4 + 0] = (byte)(terrainHeightMap[i] % 256);
//            rgbaArray[i*4 + 1] = (byte)(terrainHeightMap[i] % 256);
//            rgbaArray[i*4 + 2] = (byte)(terrainHeightMap[i] % 256);
//            rgbaArray[i*4 + 3] = (byte)255;

            //for a red map:
            //rgbaArray[i*4 + 0] = (byte)(terrainHeightMap[i] % 256);
            //rgbaArray[i*4 + 1] = 0;
            //rgbaArray[i*4 + 2] = 0;
            //rgbaArray[i*4 + 3] = (byte)255;

            //for a blue map:
            //rgbaArray[i*4 + 0] = 0;
            //rgbaArray[i*4 + 1] = 0;
            //rgbaArray[i*4 + 2] = (byte)(terrainHeightMap[i] % 256);
            //rgbaArray[i*4 + 3] = (byte)255;
        }
        Texture result = new Texture(width, height, rgbaArray);
        return result;
    }
}
