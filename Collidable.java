package Pacman;

import javafx.scene.Node;

/**
  * Collidable interface
  * Handles the common methods for game objects
*/

public interface Collidable {
  public int collided(); // called when object collided with pacman
  public double getXPos(); // get the objects x y position
  public double getYPos(); 
  public boolean isEnergy(); // see if the object is an energizer
  public Node getNode(); // get the node to add to the pane
}
