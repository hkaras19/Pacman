package Pacman;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/*
 * Dot class
 * game object
 * little circles that pacman eats for points
*/

public class Dot extends Circle implements Collidable {
	
	/*
	 * constructor
	 * sets up the circle color, size, and location
	*/
	
	public Dot(double x, double y) {
		this.setFill(Color.WHITE); // color
		this.setRadius(Constants.DOT_RADIUS); // size
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
	 * returns the point value for a dot
	*/
	
	public int collided() {
		return Constants.DOT_POINTS;
	}
	
	/*
	 * returns false to tell the game that a dot is not an energizer
	*/
	
	public boolean isEnergy() {
		return false;
	}
	
	/*
	 * get the x position
	*/
	
	public double getXPos() {
		return this.getCenterX();
	}
	
	/*
	 * get the y position
	*/
	
	public double getYPos() {
		return this.getCenterY();
	}
}