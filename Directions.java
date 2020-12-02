package Pacman;

/* enum to hold the movement direction types
 * method that gets the opposite of a given value*/

public enum Directions {
	UP, DOWN, LEFT, RIGHT;
	
	/*
	 * returns the direction opposite of the selected case
	*/
	
	public Directions getOpposite() {
		switch (this) {
		case UP:
			return Directions.DOWN;
		case DOWN:
			return Directions.UP;
		case LEFT:
			return Directions.RIGHT;
		default:
			return Directions.LEFT;
		}
	}
}
