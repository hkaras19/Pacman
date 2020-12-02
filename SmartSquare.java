package Pacman;

import java.util.ArrayList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/*
 * SmartSquare class
 * square class that makes up the board
 * shows the difference between a wall and free square
 * keeps track of the objects held by the board
*/

public class SmartSquare extends Rectangle {
	
	private boolean _isWall; // is the square a wall
	private ArrayList<Objects> _elements; // holds the elements in the square
	
	/*
	 * constructor
	 * sets up the size and location
	*/
	
	public SmartSquare(boolean isWall, int x, int y) {
		_isWall = isWall; // initialize the variables
		_elements = new ArrayList<Objects>();
		
		this.setHeight(Constants.TILE_SIZE); // sizing
		this.setWidth(Constants.TILE_SIZE);
		
		this.setX(x); // positioning
		this.setY(y);
		
		this.setUpSquare(); // setup the square
	}
	
	/*
	 * sets the square visuals based on type
	*/
	
	private void setUpSquare() {
		if (_isWall) { // wall squares are hollow looking blue squares
			this.setStroke(Color.BLUE);
			this.setStrokeWidth(3);
			this.setFill(Color.BLACK);
		} else { // game square is solid black
			this.setFill(Color.BLACK);
		}
	}
	
	/*
	 * used by the game to get the elements held by the squre
	*/
	
	public ArrayList<Objects> getElements() {
		return _elements;
	}
	
	/*
	 * turn the square into a wall or game square
	*/
	
	public void setIsWall(boolean isWall) {
		_isWall = isWall;
		
		this.setUpSquare(); // reset the visuals
	}
	
	/*
	 * see if the square is a wall
	*/
	
	public boolean getIsWall() {
		return _isWall;
	}
	
	/*
	 * add an element to the square
	*/
	
	public void addElement(Objects element) {
		_elements.add(element);
	}
	
	/*
	 * return the node to add to the game
	*/
	
	public Node getNode() {
		return this;
	}
}