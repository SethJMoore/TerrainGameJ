package com.sethjmoore.terraingame;

/**
 * Created by Seth on 3/28/2014.
 */
public class Rider {
    private Critter myMount;
    private int x, y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Rider(){
        x = 0;
        y = 0;
    }

    public void mountCritter(Critter mount){
        if (mount != null){
            myMount = mount;
            x = myMount.getX();
            y = myMount.getY();
        }
        else {
            x = 0;
            y = 0;
        }
    }

    void update(Terrain terrain) {
        if (myMount != null && myMount.alive){
            x = myMount.getX();
            y = myMount.getY();
        }
        else if (terrain.isOccupied(x, y)) {
            mountCritter(terrain.occupiedBy(x, y));
        }
    }
}
