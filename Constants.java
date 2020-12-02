package Pacman;

/*
 * constants class 
 * holds number values for the app
*/

public class Constants {
	public static final int QUIT_HEIGHT = 50; 
	public static final int BOARD_DIMENSION = 23; 
	public static final int TILE_SIZE = 23; 
	public static final int DOT_RADIUS = 1; 
	public static final int ENERGY_RADIUS = 3; 
	public static final double PACMAN_RADIUS = 7; 
	public static final int WINDOW_HEIGHT = 23 * TILE_SIZE + QUIT_HEIGHT;
	public static final int WINDOW_WIDTH = 23 * TILE_SIZE; 
	public static final double TIME = 0.5;
	public static final double PACMAN_START_X = 264.5;
	public static final double PACMAN_START_Y = 402.5;
	
	public static final BoardCoordinate RED_SCATTER_TARGET = new BoardCoordinate(0, 0, true);
	public static final BoardCoordinate PINK_SCATTER_TARGET = new BoardCoordinate(22, 22, true);
	public static final BoardCoordinate BLUE_SCATTER_TARGET = new BoardCoordinate(0, 22, true);
	public static final BoardCoordinate ORANGE_SCATTER_TARGET = new BoardCoordinate(22, 0, true);

	public static final int GHOST_START_X = 253;
	public static final int GHOST_START_Y = 184;
	
	public static final int RED_START_X = 230;
	public static final int RED_START_Y = 253;
	
	public static final int BLUE_START_X = 253;
	public static final int BLUE_START_Y = 230;
	
	public static final int PINK_START_X = 230;
	public static final int PINK_START_Y = 230;
	
	public static final int ORANGE_START_X = 276; //
	public static final int ORANGE_START_Y = 230;
	
	public static final int CHASE_TIME = 20;
	public static final int SCATTER_TIME = 7;
	public static final int FRIGHT_TIME = 7;
	
	public static final double RIGHT_EDGE_X = 517.5;
	public static final double RIGHT_EDGE_Y = 264.5;
	
	public static final double LEFT_EDGE_X = 11.5;
	public static final double LEFT_EDGE_Y = 264.5;
	
	public static final int GHOST_POINTS = 200;
	public static final int DOT_POINTS = 10;
	public static final int ENERGY_POINTS = 200;
}