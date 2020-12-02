package Pacman;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/*
 * Energizer class
 * game object that switches from chase mode to frightened mode
*/

public class Energizer extends Circle implements Collidable {
	
	/*
	 * constructor
	 * sets up the circle color, size, and location
	*/
	
	public Energizer(double x, double y, GameModes mode) {
		this.setFill(Color.WHITE); // color
		this.setRadius(Constants.ENERGY_RADIUS); // size
		this.setCenterX(x); // positioning
		this.setCenterY(y);
	}
	
	/*
	 * return the node to add to the game
	*/
	
	public Node getNode() {
		return this;
	}
	
	/*
	 * returns the point value for an energizer
	*/
	
	public int collided() {
		return Constants.ENERGY_POINTS;
	}
	
	/*
	 * returns true to tell the game this is an energizer
	*/
	
	public boolean isEnergy() {
		return true;
	}
	
	/*
	 * get the x position
	*/
	
	public double getXPos() {
		return this.getCenterX();
	}
	
	/*
	 * get the x position
	*/
	
	public double getYPos() {
		return this.getCenterY();
	}
}