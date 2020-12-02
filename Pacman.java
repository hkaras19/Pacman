package Pacman;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/*
 * Pacman class
 * create the player
*/

public class Pacman extends Circle {
	
	/*
	 * constructor
	 * sets up the circle color, size, and location
	*/
	
	public Pacman(double x, double y) {
		this.setFill(Color.YELLOW); // color
		this.setRadius(Constants.PACMAN_RADIUS); // sizing
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
	 * used by game to set the x position
	*/
	
	public void setPacmanX(double x) {
		this.setCenterX(x);
	}
	
	/*
	 * used by game to set the y position
	*/
	
	public void setPacmanY(double y) {
		this.setCenterY(y);
	}
	
	/*
	 * used by game to get the x position
	*/
	
	public double getPacmanX() {
		return this.getCenterX();
	}
	
	/*
	 * used by game to get the y position
	*/
	
	public double getPacmanY() {
		return this.getCenterY();
	}
}