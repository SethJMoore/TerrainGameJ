package com.sethjmoore.terraingame;

/**
 * Created by Seth on 3/28/2014.
 */
public class Critter {
    public void setCritterType(CritterType critterType) {
        this.critterType = critterType;
    }

    public enum CritterType{
        Diver, Climber
    }
    private int x;
    private int y;
    private CritterType critterType;
    private int age;
    boolean fertile;
    boolean alive;

    public Critter(){
        x = 0;
        y = 0;
        critterType = CritterType.values()[Game.rand.nextInt(CritterType.values().length)];
        age = 0;
        fertile = false;
        alive = true;
    }

    public CritterType getCritterType(){
        return critterType;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void update(Terrain terrain) {
        boolean turnDone = false;
        if (fertile){
            if (tryToGetLucky(terrain)){
                turnDone = true;
            }
        }

        if (critterType == CritterType.Diver && !turnDone){
            if (!fertile){
                goLow(terrain);
            }
            else {
                goHigh(terrain);
            }
        }
        else if (critterType == CritterType.Climber && !turnDone){
            if (!fertile){
                goHigh(terrain);
            }
            else {
                goLow(terrain);
            }
        }

        toDiePerchance(terrain);

        age++;

        if (age == 200){
            fertile = true;
        }
        if (age == 1000){
            fertile = false;
        }
    }

    private void toDiePerchance(Terrain terrain) {
        if (terrain.isOccupiedSouthOf(x, y) &&
                terrain.isOccupiedNorthOf(x, y) &&
                terrain.isOccupiedWestOf(x, y) &&
                terrain.isOccupiedEastOf(x, y)){
            if (Game.rand.nextDouble() < age * 0.001){
                Die(terrain);
            }
        }
    }

    private void Die(Terrain terrain) {
        alive = false;
        terrain.removeCritter(this);
    }

    private void goHigh(Terrain terrain) {
        //int highest = terrain.altitude(x, y); //Stays at highest point.
        int highest = 0; //Goes back and forth between highest points.
        int xMove = 0;
        int yMove = 0;

        for (int i = 0, j = Game.rand.nextInt(4); i < 4; i++)
        {
            switch (j % 4)
            {
                case 0:
                    if (y > 0 && terrain.altitudeSouthOf(x, y) > highest && !terrain.isOccupiedSouthOf(x, y))
                    {
                        highest = terrain.altitudeSouthOf(x, y);
                        yMove = -1;
                        xMove = 0;
                    }
                    break;
                case 1:
                    if (x > 0 && terrain.altitudeWestOf(x, y) > highest && !terrain.isOccupiedWestOf(x, y))
                    {
                        highest = terrain.altitudeWestOf(x, y);
                        xMove = -1;
                        yMove = 0;
                    }
                    break;
                case 2:
                    if (y < terrain.getHeight() - 1 && terrain.altitudeNorthOf(x, y) > highest && !terrain.isOccupiedNorthOf(x, y))
                    {
                        highest = terrain.altitudeNorthOf(x, y);
                        yMove = 1;
                        xMove = 0;
                    }
                    break;
                case 3:
                    if (x < terrain.getWidth() - 1 && terrain.altitudeEastOf(x, y) > highest && !terrain.isOccupiedEastOf(x, y))
                    {
                        highest = terrain.altitudeEastOf(x, y);
                        xMove = 1;
                        yMove = 0;
                    }
                    break;
                default:
                    break;
            }
            j++;
        }

        terrain.vacate(x, y);
        x += xMove;
        y += yMove;
        terrain.occupy(x, y, this);
    }

    private void goLow(Terrain terrain) {
        //int lowest = terrain.altitude(x, y); //Stays at lowest point.
        int lowest = Integer.MAX_VALUE; //Goes back and forth between lowest points.
        int xMove = 0;
        int yMove = 0;

        for (int i = 0, j = Game.rand.nextInt(4); i < 4; i++)
        {
            switch (j % 4)
            {
                case 0:
                    //if (y > 0 && terrain.altitudeSouthOf(x, y) < lowest && !terrain.isOccupied(x, y - 1))
                    if (y > 0 && terrain.altitudeSouthOf(x, y) < lowest && !terrain.isOccupiedSouthOf(x, y))
                    {
                        lowest = terrain.altitudeSouthOf(x, y);
                        yMove = -1;
                        xMove = 0;
                    }
                    break;
                case 1:
                    if (x > 0 && terrain.altitudeWestOf(x, y) < lowest && !terrain.isOccupiedWestOf(x, y))
                    {
                        lowest = terrain.altitudeWestOf(x, y);
                        xMove = -1;
                        yMove = 0;
                    }
                    break;
                case 2:
                    if (y < terrain.getHeight() - 1 && terrain.altitudeNorthOf(x, y) < lowest && !terrain.isOccupiedNorthOf(x, y))
                    {
                        lowest = terrain.altitudeNorthOf(x, y);
                        yMove = 1;
                        xMove = 0;
                    }
                    break;
                case 3:
                    if (x < terrain.getWidth() - 1 && terrain.altitudeEastOf(x, y) < lowest && !terrain.isOccupiedEastOf(x, y))
                    {
                        lowest = terrain.altitudeEastOf(x, y);
                        xMove = 1;
                        yMove = 0;
                    }
                    break;
                default:
                    break;
            }
            j++;
        }

        terrain.vacate(x, y);
        x += xMove;
        y += yMove;
        terrain.occupy(x, y, this);
    }

    private boolean tryToGetLucky(Terrain terrain) {
        boolean successful = false;
        if (terrain.occupiedBySouthOf(x, y) != null && terrain.occupiedBySouthOf(x, y).critterType != critterType){
            successful = reproduce(terrain);
        }
        return successful;
    }

    private boolean reproduce(Terrain terrain) {
        boolean successful = false;
        int r = Game.rand.nextInt(4);
        switch (r){
            case 0:
                if (!terrain.isOccupiedSouthOf(x, y))
                    terrain.addCritter(x, y + 1, cloneMe());
                if (Game.rand.nextDouble() < 0.1)
                {
                    fertile = !fertile;
                }
                successful = true;
                break;
            case 1:
                if (!terrain.isOccupiedNorthOf(x, y))
                    terrain.addCritter(x, y - 1, cloneMe());
                if (Game.rand.nextDouble() < 0.1)
                {
                    fertile = !fertile;
                }
                successful = true;
                break;
            case 2:
                if (!terrain.isOccupiedWestOf(x, y))
                    terrain.addCritter(x - 1, y, cloneMe());
                if (Game.rand.nextDouble() < 0.1)
                {
                    fertile = !fertile;
                }
                successful = true;
                break;
            case 3:
                if (!terrain.isOccupiedEastOf(x, y))
                    terrain.addCritter(x + 1, y, cloneMe());
                if (Game.rand.nextDouble() < 0.1)
                {
                    fertile = !fertile;
                }
                successful = true;
                break;
        }
        return successful;
    }

    private Critter cloneMe() {
        Critter c = new Critter();
        c.setCritterType(critterType);
        return c;
    }
}
