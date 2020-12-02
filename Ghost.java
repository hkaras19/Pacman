package Pacman;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/*
 * Ghost class
 * creates a ghost for the game
*/

public class Ghost extends ImageView {
	private Directions _currentDirection; // stores the ghost movement direction
	private Image _image; // image for ghost
	
	/*
	 * constructor
	 * sets up the ghost image, size, and location
	*/
	
	public Ghost(double x, double y, Image image) {
		_image = image;
		
		this.setFitHeight(Constants.TILE_SIZE); // sizing
		this.setFitWidth(Constants.TILE_SIZE);
		this.setX(x); // positioning
		this.setY(y);
		
		this.setImage(image); // set the image
	}
	
	/*
	 * changes the image of the ghost to and from blue based on the game mode
	*/
	
	public void switchImage(GameModes mode) {
		if (mode == GameModes.CHASE) { // normal image if the ghost is chasing
			this.setImage(_image);
		} else if (mode == GameModes.FRIGHTENED) { // blue ghost if running away
			Image scared = new Image(getClass().getResourceAsStream("scaredGhost.jpg")); // Load scared image
			this.setImage(scared);
		}
	}
	
	/*
	 * update the ghost current direction
	*/
	
	public void setDirection(Directions direction) {
		_currentDirection = direction;
	}
	
	/*
	 * get the ghost current direction
	*/
	
	public Directions getDirection() {
		return _currentDirection;
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
	
	public void setGhostX(double x) {
		this.setX(x);
	}
	
	/*
	 * used by game to set the y position
	*/
	
	public void setGhostY(double y) {
		this.setY(y);
	}
	
	/*
	 * used by the game to get the location of the ghost
	*/
	
	public BoardCoordinate getGhostLocation() {
		int i = (int) this.getY() / Constants.TILE_SIZE;
		int j = (int) this.getX() / Constants.TILE_SIZE;
		
		return new BoardCoordinate(i, j, false); // return the board coordinate of the ghost location
 	}
}