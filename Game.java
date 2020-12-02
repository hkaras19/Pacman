package Pacman;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import cs015.fnl.PacmanSupport.SquareType;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/*
 * Game class
 * pane where all of the game elements are stored
 * handles user interaction
 * main class for the app
*/

public class Game extends Pane {
	
	private Animation _animation; // animation for the game
	private Animation _ghostAnimation; // animation for the ghosts
	private SmartSquare[][] _board; // array for the smart squares
	private Pacman _pacman;  // player
	private int _xDirection; // direction pacman is heading
	private int _yDirection; // direction pacman is heading
	private ArrayList<Collidable> _boardElements; // list of elements on the board
	private int _score; // current score
	private int _lives; // current lives
	private Label _scoreLabel; // label for the score
	private Label _livesLabel; // label for the lives
	private Queue<BoardCoordinate> _coordinateQueue; // queue for BFS
	private Queue<Ghost> _ghostPenQueue; // queue to hold ghosts in the pen
	private Directions[][] _checkedArray; // direction array for BFS
	private double[] _minDistance; // holds direction and min distance for BFS
	private double _timeCounter; // counter for animation
	private double _timeSinceEnergizer; // counter for scared mode
	private GameModes _gameMode; // current ghost mode
	
	// ghost targets for chase mode
	private BoardCoordinate _redTarget; 
	private BoardCoordinate _blueTarget;
	private BoardCoordinate _pinkTarget;
	private BoardCoordinate _orangeTarget;
	
	// ghosts
	private Ghost _red; 
	private Ghost _blue;
	private Ghost _pink;
	private Ghost _orange;
	
	private Ghost[] _ghosts; // holds all the ghosts
	
	/*
	 * constructor
	 * instantiates the board
	 * adds the keyhandler to pane
	*/
	
	public Game(Label scoreLabel, Label livesLabel) {
		
		// initialize variables
		_board = new SmartSquare[Constants.BOARD_DIMENSION][Constants.BOARD_DIMENSION];
		_xDirection = 0;
		_yDirection = 0;
		_boardElements = new ArrayList<Collidable>();
		_score = 0;
		_lives = 3;
		_scoreLabel = scoreLabel;
		_livesLabel = livesLabel;
		_checkedArray = new Directions[Constants.BOARD_DIMENSION][Constants.BOARD_DIMENSION];
		_coordinateQueue = new LinkedList<BoardCoordinate>();
		_ghostPenQueue = new LinkedList<Ghost>();
		_minDistance = new double[3];
		_minDistance[0] = Math.pow(10, 10);
		_timeCounter = 0;
		_gameMode = GameModes.CHASE;
		_timeSinceEnergizer = 0;
		_ghosts = new Ghost[4];
		
		initializeBoard(); // setup board
		this.addEventHandler(KeyEvent.KEY_PRESSED, new KeyHandler()); // Observe key pressed
		this.setFocusTraversable(true); // Set the focus to the game pane
		
		this.animate(); // begin game
	}
	
	/*
	 * starts the animation for the game
	 * creates an animation for pacman and the ghosts
	*/
	
	private void animate() {
		KeyFrame kf = new KeyFrame(Duration.seconds(Constants.TIME), new TimeHandler()); // New keyframe
		_animation = new Timeline(kf); // New timeline
		_animation.setCycleCount(Animation.INDEFINITE); // Animates until told to stop
		_animation.play(); // Start animation
		
		KeyFrame kf2 = new KeyFrame(Duration.seconds(Constants.TIME), new GhostTimeHandler()); // New keyframe
		_ghostAnimation = new Timeline(kf2); // New timeline
		_ghostAnimation.setCycleCount(Animation.INDEFINITE); // Animates until told to stop
		
		Ghost releasedGhost = _ghostPenQueue.remove(); // release red ghost
		releasedGhost.setX(Constants.GHOST_START_X); // move it to the start position
		releasedGhost.setY(Constants.GHOST_START_Y);
		
		_ghostAnimation.play(); // Start animation
	}
	
	/*
	 * Handles the timeline for pacman / the game
	 * moves the current piece down
	*/
	
	private class TimeHandler
	implements EventHandler<ActionEvent> {
		public void handle(ActionEvent event) {
			isGameDone(); // check if the game is done
			movePacman(); // move pacman
			
			_timeCounter += Constants.TIME; // update the time
		}
	}
	
	/*
	 * Handles the timeline for the ghosts
	 * changes the game mode based on time
	 * updates the BFS targets
	 * adds the ghosts to BFS
	 * checks if the ghosts should be released from the pen
	*/
	
	private class GhostTimeHandler
	implements EventHandler<ActionEvent> {
		public void handle(ActionEvent event) {

			_red.toFront(); // makes sure the ghosts are visible over the smart squares
			_blue.toFront();
			_pink.toFront();
			_orange.toFront();
			
			if (_timeCounter == Constants.CHASE_TIME && _gameMode == GameModes.CHASE) { // if chase mode is done, reset the timer and change to scatter mode
				_timeCounter = 0;
				_gameMode = GameModes.SCATTER;
			}
			
			if (_timeCounter == Constants.SCATTER_TIME && _gameMode == GameModes.SCATTER) { // if chase mode is done, reset the timer and change to chase mode
				_timeCounter = 0;
				_gameMode = GameModes.CHASE;
			}
			
			// update the targets to match pacmans current location
			_redTarget = new BoardCoordinate((int) _pacman.getPacmanY() / Constants.TILE_SIZE, (int) _pacman.getPacmanX() / Constants.TILE_SIZE, true);
			_blueTarget = new BoardCoordinate((int) _pacman.getPacmanY() / Constants.TILE_SIZE, (int) (_pacman.getPacmanX() / Constants.TILE_SIZE) + 2, true);
			_pinkTarget = new BoardCoordinate((int) (_pacman.getPacmanY() / Constants.TILE_SIZE) - 4, (int) _pacman.getPacmanX() / Constants.TILE_SIZE, true);
			_orangeTarget = new BoardCoordinate((int) (_pacman.getPacmanY()) + 1 / Constants.TILE_SIZE, (int) (_pacman.getPacmanX() / Constants.TILE_SIZE) - 3, true);
			
			addGhostToBFS(_red, _redTarget, Constants.RED_SCATTER_TARGET); // add the ghosts to the bfs algorithm
			addGhostToBFS(_blue, _blueTarget, Constants.BLUE_SCATTER_TARGET);
			addGhostToBFS(_pink, _pinkTarget, Constants.PINK_SCATTER_TARGET);
			addGhostToBFS(_orange, _orangeTarget, Constants.ORANGE_SCATTER_TARGET);
			 
			if (!_ghostPenQueue.isEmpty()) { // if there are ghosts in the pen, check if they should be released
				checkGhostPenRelease();
			}

			if (_gameMode == GameModes.FRIGHTENED) { // handle the scared ghosts
				if (_timeSinceEnergizer == Constants.FRIGHT_TIME) { // check if frightened mode has ended and reset to chase mode
					_timeCounter = 0;
					_gameMode = GameModes.CHASE;
					
					_red.switchImage(GameModes.CHASE); // change ghosts from blue to normal
					_blue.switchImage(GameModes.CHASE);
					_pink.switchImage(GameModes.CHASE);
					_orange.switchImage(GameModes.CHASE);
					
					return;
				}
				
				_timeSinceEnergizer += Constants.TIME; // update the time counter
			}
		}
	}
	
	/*
	 * checks if the game is over
	 * either dead pacman or an empty board
	 * displays win or game over label
	*/
	
	private void isGameDone() {
		if (_lives == 0) { // pacman died
			Label gameOver = new Label("Game Over!"); // game over label
			gameOver.setTextFill(Color.WHITE);
			gameOver.setStyle("-fx-font: 48 arial;");
			gameOver.setLayoutX((Constants.WINDOW_WIDTH / 3));
			gameOver.setLayoutY(Constants.WINDOW_HEIGHT / 5);
			
			_animation.stop(); // stop the animations
			_ghostAnimation.stop();
			
			this.getChildren().add(gameOver);
		}
		
		if (_boardElements.size() == 0) {
			Label gameOver = new Label("You Win!"); // win label
			gameOver.setTextFill(Color.WHITE);
			gameOver.setStyle("-fx-font: 48 arial;");
			gameOver.setLayoutX((Constants.WINDOW_WIDTH / 3));
			gameOver.setLayoutY(Constants.WINDOW_HEIGHT / 5);
			
			_animation.stop(); // stop the animations
			_ghostAnimation.stop();
			
			this.getChildren().add(gameOver);
		}
	}
	
	/*
	 * releases the ghosts from the pen 
	 * based off of time intervals of 0.5 * 5 = 2.5 seconds
	*/
	
	private void checkGhostPenRelease() {
		if (_timeCounter % 5 == 0) { // check the time 
			Ghost releasedGhost = _ghostPenQueue.remove(); // release a ghost
			releasedGhost.setX(Constants.GHOST_START_X); // move it to the start position
			releasedGhost.setY(Constants.GHOST_START_Y);
		}
	}
	
	/*
	 * use the bfs method to find which direction the ghost should move
	 * move the ghost in that direction
	*/
	
	private void addGhostToBFS(Ghost ghost, BoardCoordinate target, BoardCoordinate scatterTarget) {
		BoardCoordinate start = ghost.getGhostLocation(); // where the ghost currently is
		Directions direction = bfs(ghost, start, target, true, scatterTarget); // where the ghost should move to
		moveGhost(ghost, direction); // move the ghost there
	}

	/*
	 * returns the direction that the ghost should move in
	 * shortest path to their target
	*/
	
	private Directions bfs(Ghost ghost, BoardCoordinate startPosition, BoardCoordinate target, boolean isFirstIteration, BoardCoordinate scatterTarget) {
		
		int i = startPosition.getRow(); // i index of ghost on the board 
		int j = startPosition.getColumn(); // j index of ghost on the board 
		
		ArrayList<BoardCoordinate> nearBySquares = new ArrayList<BoardCoordinate>(); // holds the squares that the ghost can move into
		
		if (isFirstIteration) { // reset the variables at the start of bfs
			_checkedArray = new Directions[Constants.BOARD_DIMENSION][Constants.BOARD_DIMENSION];
			_coordinateQueue.clear();
			_minDistance[0] = Math.pow(10, 10); // this allows the min distance to be calculated as the minimum starts off extremely high
		}
		
		if (i - 1 >= 0) { // check if ghost can move up
			BoardCoordinate up = new BoardCoordinate(i - 1, j, false);
			nearBySquares.add(up); // add to the direction arraylist 
		}
		
		if (i + 1 <= Constants.BOARD_DIMENSION - 1) { // check if ghost can move down
			BoardCoordinate down = new BoardCoordinate(i + 1, j, false);
			nearBySquares.add(down); // add to the direction arraylist 
		}
		
		if (j - 1 >= 0) { // check if ghost can move left
			BoardCoordinate left = new BoardCoordinate(i, j - 1, false);
			nearBySquares.add(left); // add to the direction arraylist 
		}
		
		if (j + 1 <= Constants.BOARD_DIMENSION - 1) { // check if ghost can move right
			BoardCoordinate right = new BoardCoordinate(i, j + 1, false);
			nearBySquares.add(right); // add to the direction arraylist 
		}
		
		for (int k = 0; k < nearBySquares.size(); k++) { // iterate through the directions and check if the square is a valid move
			
			BoardCoordinate nextSquare = nearBySquares.get(k); // the square from the list
			Directions direction = Directions.LEFT; // initializing the direction
			
			if (isFirstIteration) { // if starting bfs, direction needs to be manually set
				if (nextSquare.getRow() > i) {
					direction = Directions.DOWN; // square below
				} else if (nextSquare.getRow() < i){
					direction = Directions.UP; // square above
				} else if (nextSquare.getColumn() > j) {
					direction = Directions.RIGHT; // square right
				} else if (nextSquare.getColumn() < j) {
					direction = Directions.LEFT; // square left
				}
			} else { // already in bfs
				direction = _checkedArray[i][j]; // direction is the current square

				if (ghost.getDirection() != null) {
					if (direction == ghost.getDirection().getOpposite()) { // prevent the ghost from turning 180
						break;
					}
				}
			}
			
			this.checkValidity(nextSquare, direction); // check if the square is a valid move for the ghost
		}
		
		while (!_coordinateQueue.isEmpty()) { // iterate through the queued squares and find the minimum distance to target
			
			BoardCoordinate dequeuedSquare = _coordinateQueue.remove(); // the dequeued square

			double minDist = 0; // initialize minimum distance
			if (_gameMode == GameModes.CHASE) { // if chasing, target should be based of pacman
				minDist = getDistance(target, dequeuedSquare); // calculate distance
			} else if (_gameMode == GameModes.SCATTER){ // if scattering, target should be based corners
				minDist = getDistance(scatterTarget, dequeuedSquare); // calculate distance
			} else { // if scared, target should be random
				int randI = (int) (Math.random() * Constants.BOARD_DIMENSION); // random i value on the board 
				int randJ = (int) (Math.random() * Constants.BOARD_DIMENSION); // random j value on the board
				BoardCoordinate scaredTarget = new BoardCoordinate(randI, randJ, true); // random target
				
				minDist = getDistance(scaredTarget, dequeuedSquare); // calculate distance
			}
			
			if (minDist < _minDistance[0]) { // update the minimum distance with it coordinates
				_minDistance[0] = minDist;
				_minDistance[1] = dequeuedSquare.getRow();
				_minDistance[2] = dequeuedSquare.getColumn();
			}
			
			bfs(ghost, dequeuedSquare, target, false, scatterTarget); // use recursion to find the shortest path for the ghost to take
		}

		return _checkedArray[(int) _minDistance[1]][(int) _minDistance[2]]; // return the direction held by the minimum distance array
	}
	
	/*
	 * checks if the ghost can move into the square at the given coordinate
	 * if valid, adds the square to the queue
	*/
	
	private void checkValidity(BoardCoordinate coordinate, Directions direction) {
		if (_board[coordinate.getRow()][coordinate.getColumn()].getIsWall()) { // check if the square is a wall
			return; // not valid move
		}
		
		if (_checkedArray[coordinate.getRow()][coordinate.getColumn()] == null) { // check if the square has already been visited
			_checkedArray[coordinate.getRow()][coordinate.getColumn()] = direction; // mark the square
			_coordinateQueue.add(coordinate); // add to queue
		}
	}
	
	/*
	 * returns the distance between two coordinates
	*/
	
	private double getDistance(BoardCoordinate target, BoardCoordinate point2) {
		double x1 = target.getColumn() * Constants.TILE_SIZE; // convert row and columns into x and y
		double y1 = target.getRow() * Constants.TILE_SIZE;
		
		double x2 = point2.getColumn() * Constants.TILE_SIZE; // convert row and columns into x and y
		double y2 = point2.getRow() * Constants.TILE_SIZE;
		
		return Math.sqrt(Math.pow((x1-x2), 2) + Math.pow((y1-y2), 2)); // pythagorean theorem
	}
	
	/*
	 * checks if the ghost has collided with pacman
	 * takes a life from pacman and updates lives label if in chase mode
	 * add the ghost to the pen and queue, updating score, if in scared mode
	*/
	
	private void detectGhostCollision() {
		BoardCoordinate pacmanCoor = new BoardCoordinate((int) _pacman.getPacmanY() / Constants.TILE_SIZE, (int) _pacman.getPacmanX() / Constants.TILE_SIZE, false); // pacman's location
		
		for (int i = 0; i < _ghosts.length; i++) { // iterate through the ghost array
			if (pacmanCoor.getRow() == _ghosts[i].getGhostLocation().getRow() && pacmanCoor.getColumn() == _ghosts[i].getGhostLocation().getColumn()) { // check if the ghost is in the same square as pacman
				if (_gameMode == GameModes.CHASE) { // if chasing, eat pacman
					_lives -= 1; // update life count
					_livesLabel.setText("Lives: " + _lives); // update life label
					resetForNextRound();  // reset to start positions
				} else if (_gameMode == GameModes.FRIGHTENED) { // if running away, get eaten
					_score += Constants.GHOST_POINTS; // update score
					_scoreLabel.setText("Score: " + _score); // update the label
					_ghosts[i].setX((Constants.BLUE_START_X - Constants.TILE_SIZE) + _ghostPenQueue.size() * Constants.TILE_SIZE); // move ghost to the pen
					_ghosts[i].setY(Constants.BLUE_START_Y);
					_ghostPenQueue.add(_ghosts[i]); // add ghost to the queue to be released
				}
			}
		}
	}
	
	/*
	 * resets the ghosts and pacman to their starting positions
	 * add the ghosts to the pen queue
	 * reset the counters and pacman directions*/
	
	private void resetForNextRound() {
		_red.setX(Constants.RED_START_X); // reset ghost position
		_red.setY(Constants.RED_START_Y);
		
		_blue.setX(Constants.BLUE_START_X); // reset ghost position
		_blue.setY(Constants.BLUE_START_Y);
		
		_pink.setX(Constants.PINK_START_X); // reset ghost position
		_pink.setY(Constants.PINK_START_Y);
		
		_orange.setX(Constants.ORANGE_START_X); // reset ghost position
		_orange.setY(Constants.ORANGE_START_Y);

		_ghostPenQueue.clear(); // reset the ghost queue
		_ghostPenQueue.add(_red);
		_ghostPenQueue.add(_blue);
		_ghostPenQueue.add(_pink);
		_ghostPenQueue.add(_orange);
		
		_pacman.setPacmanX(Constants.PACMAN_START_X); // reset pacman position
		_pacman.setPacmanY(Constants.PACMAN_START_Y);
		
		_timeCounter = 0; // reset variables and counters
		_timeSinceEnergizer = 0;
		_xDirection = 0;
		_yDirection = 0;
	}
	
	/*
	 * moves the ghost around the board
	 * takes the direction given by bfs and turns it into motion
	*/
	
	private void moveGhost(Ghost ghost, Directions direction) {
		
		if (_ghostPenQueue.contains(ghost)) { // don't move the ghost if it is in the pen
			return;
		}
		
		int iAdd = 0; // used to distinguish between left, right, up, and down
		int jAdd = 0;
		
		switch (direction) { // set the appropriate direction using the i and j modifiers
		case UP:
			iAdd = -1;
			jAdd = 0;
			
			break;
			
		case DOWN:
			iAdd = 1;
			jAdd = 0;
			
			break;
			
		case LEFT:
			iAdd = 0;
			jAdd = -1;
			
			break;
			
		case RIGHT:
			iAdd = 0;
			jAdd = 1;
			
			break;
		}
		
		ghost.setDirection(direction); // store the movement direction in the ghost
		ghost.setX(ghost.getX() + jAdd * Constants.TILE_SIZE); // update position
		ghost.setY(ghost.getY() + iAdd * Constants.TILE_SIZE);
		
		detectGhostCollision(); // check if the ghost is hitting pacman (fixes bug where pacman and ghost cross)
	}
	
	/*
	 * check if pacman is colliding with an object
	*/
	
	private boolean checkCollision(int xDir, int yDir) {
		int j = (int) _pacman.getPacmanX() / Constants.TILE_SIZE; // pacman i and j location
		int i = (int) _pacman.getPacmanY() / Constants.TILE_SIZE;
		
		this.checkObjectCollision(i,j); // check if pacman is currently colliding with an object
		
		j += xDir; // location pacman will be moving to
		i += yDir;
		
		if (_board[i][j].getIsWall()) { // check if pacman will hit a wall by moving
			return true; // will collide
		}
		
		return false; // will not collide
	}
	
	/*
	 * detects if pacman is colliding with a game object
	 * responds accordingly
	*/
	
	private void checkObjectCollision(int i, int j) {
		for (int k = 0; k < _boardElements.size(); k++) { // iterate through the elements on the board
			Collidable element = _boardElements.get(k); // current element
			
			BoardCoordinate elementCoor = new BoardCoordinate((int) element.getYPos() / Constants.BOARD_DIMENSION, (int) element.getXPos() / Constants.BOARD_DIMENSION, false); // location of the element
			
			if (elementCoor.getRow() == i && elementCoor.getColumn() == j) { // check if pacman is colliding
				_score += element.collided(); // update the score using the elements collided method
				_scoreLabel.setText("Score: " + _score); // update the score label
				_boardElements.remove(element); // remove the element from the board arraylist
				this.getChildren().remove(element); // remove the element from the board

				if (element.isEnergy()) { // check if the element was an energy booster
					_gameMode = GameModes.FRIGHTENED; // change the game mode
					_timeSinceEnergizer = 0; // reset the energy time counter
					
					_red.switchImage(GameModes.FRIGHTENED); // change ghosts to blue
					_blue.switchImage(GameModes.FRIGHTENED);
					_pink.switchImage(GameModes.FRIGHTENED);
					_orange.switchImage(GameModes.FRIGHTENED);
				}
			}
		}
	}
	
	/*
	 * moves pacman across the board
	 * checks to make sure pacman can move
	 * "teleports" pacman when moving through the side gap
	*/
	
	private void movePacman() {
		if (_pacman.getPacmanX() == Constants.LEFT_EDGE_X && _pacman.getPacmanY() == Constants.LEFT_EDGE_Y && _xDirection == -1) { // check if pacman is entering the left passage
			_pacman.setPacmanX(Constants.RIGHT_EDGE_X); // move to the right passage
			_pacman.setPacmanY(Constants.RIGHT_EDGE_Y);
			return;
		} else if (_pacman.getPacmanX() == Constants.RIGHT_EDGE_X && _pacman.getPacmanY() == Constants.RIGHT_EDGE_Y && _xDirection == 1) { // check if pacman is entering the right passage
			_pacman.setPacmanX(Constants.LEFT_EDGE_X); // move to the right passage
			_pacman.setPacmanY(Constants.LEFT_EDGE_Y);
			return;
		}
		
		if (checkCollision(_xDirection, _yDirection)) { // check if pacman is colliding with something
			return;
		}
		
		_pacman.setPacmanX(_pacman.getPacmanX() + (_xDirection * Constants.BOARD_DIMENSION)); // update pacman position
		_pacman.setPacmanY(_pacman.getPacmanY() + (_yDirection * Constants.BOARD_DIMENSION));

		_pacman.toFront(); // make sure pacman is on top
		
		this.detectGhostCollision(); // check for a ghost collision
	}
	
	/*
	 * handles the user's key inputs
	 * used to move pacman up, down, left, and right
	*/
	
	private class KeyHandler 
	implements EventHandler<KeyEvent> {
		@Override
		public void handle(KeyEvent e) {
			KeyCode keyPressed = e.getCode(); // gets which key was pressed
			
			switch (keyPressed)
			{
			case LEFT: // move left
				if (checkCollision(-1, 0) == false) { // not colliding with wall, set the move direction
					_xDirection = -1;
					_yDirection = 0;
				}
				
				break;
				
			case RIGHT: // move right
				if (checkCollision(1, 0) == false) { // not colliding with wall, set the move direction
					_xDirection = 1;
					_yDirection = 0;
				}
				
				break;
	
			case DOWN: // move down
				if (checkCollision(0, 1) == false) { // not colliding with wall, set the move direction
					_yDirection = 1;
					_xDirection = 0;
				}
				
				break;
				
			case UP: // move up
				if (checkCollision(0, -1) == false) { // not colliding with wall, set the move direction
					_yDirection = -1;
					_xDirection = 0;
				}
				
				break;
				
			default: // Default
				break;
			}

			e.consume(); // Consume the key event
		}
	}
	
	/*
	 * set up the board
	 * use the support map to load the board array
	 * add the appropriate elements to smartsquares
	*/
	
	private void initializeBoard() {
		SquareType[][] supportMap = cs015.fnl.PacmanSupport.SupportMap.getSupportMap(); // support map
		
		_board = new SmartSquare[Constants.BOARD_DIMENSION][Constants.BOARD_DIMENSION]; // initialize the board array
		_boardElements.clear(); // clear the elements from the board
		
		for (int i = 0; i < Constants.BOARD_DIMENSION; i++) { // iterate through the support board
			for (int j = 0; j < Constants.BOARD_DIMENSION; j++) {
				SmartSquare square = new SmartSquare(false, Constants.BOARD_DIMENSION * j, Constants.BOARD_DIMENSION * i); // create a smart square at the current location
				
				switch (supportMap[i][j]) { // check what is at the board position and update the smart square
				case WALL:
					square.setIsWall(true);
					break;
					
				case DOT:
					square.addElement(Objects.DOT);	
					break;
					
				case FREE:
					break;
					
				case ENERGIZER:
					square.addElement(Objects.ENERGIZER);
					break;
					
				case PACMAN_START_LOCATION:
					square.addElement(Objects.PACMAN_START_LOCATION);
					break;
					
				case GHOST_START_LOCATION:
					square.addElement(Objects.GHOST_START_LOCATION);
					break;
					
				default:
					break;
				}
				
				_board[i][j] = square; // add the smart square to the board array
			}
		}
		
		this.displayBoard(); // display the board
	}
	
	/*
	 * Iterates through the board array and adds the smart squares to the pane
	*/
	
	private void displayBoard() {
		for (int i = 0; i < Constants.BOARD_DIMENSION; i++) {
			for (int j = 0; j < Constants.BOARD_DIMENSION; j++) {
				SmartSquare square = _board[i][j]; // current square 
				
				ArrayList<Objects> elements = square.getElements(); // get the elements that the square holds
				this.getChildren().add(square); // add the square to the board
				
				for (int k = 0; k < elements.size(); k++) { // iterate through the elements in the square and add them to the board
					this.addElement(i, j, elements.get(k));
				}
			}
		}
	}
	
	/*
	 * adds the given element to the board at a specified position
	*/
	
	private void addElement(int i, int j, Objects type) {
		switch (type) {
		case DOT: // adds a dot
			Dot dot = new Dot(Constants.BOARD_DIMENSION * j + (0.5 * Constants.BOARD_DIMENSION), Constants.BOARD_DIMENSION * i + (0.5 * Constants.BOARD_DIMENSION)); // positioning
			this.getChildren().add(dot.getNode());
			_boardElements.add(dot); // store in the board elements arraylist
			
			break;
			
		case ENERGIZER: // adds an energizer
			Energizer energy = new Energizer(Constants.BOARD_DIMENSION * j + (0.5 * Constants.BOARD_DIMENSION), Constants.BOARD_DIMENSION * i + (0.5 * Constants.BOARD_DIMENSION), _gameMode); // positioning
			this.getChildren().add(energy.getNode());
			_boardElements.add(energy); // store in the board elements arraylist
			
			break;
			
		case PACMAN_START_LOCATION: // adds pacman
			Pacman pacman = new Pacman(Constants.BOARD_DIMENSION * j + (0.5 * Constants.BOARD_DIMENSION), Constants.BOARD_DIMENSION * i + (0.5 * Constants.BOARD_DIMENSION)); // positioning
			this.getChildren().add(pacman.getNode());
			_pacman = pacman;
			
			break;
			
		case GHOST_START_LOCATION: // add the ghosts
			Image redImage = new Image(getClass().getResourceAsStream("red.jpg")); // Load red image
			Ghost red = new Ghost(Constants.RED_START_X, Constants.RED_START_Y, redImage); // create red ghost
			
			Image blueImage = new Image(getClass().getResourceAsStream("blue.jpg")); // Load blue image
			Ghost blue = new Ghost(Constants.BLUE_START_X, Constants.BLUE_START_Y, blueImage); // create blue ghost
			
			Image pinkImage = new Image(getClass().getResourceAsStream("pink.jpg")); // Load pink image
			Ghost pink = new Ghost(Constants.PINK_START_X, Constants.PINK_START_Y, pinkImage ); // create the pink ghost
			
			Image orangeImage = new Image(getClass().getResourceAsStream("orange.jpg")); // Load orange image
			Ghost orange = new Ghost(Constants.ORANGE_START_X, Constants.ORANGE_START_Y, orangeImage); // create the orange ghost

			_red = red; // store the ghosts in their instance variables
			_blue = blue;
			_pink = pink;
			_orange = orange;
			
			this.getChildren().addAll(red.getNode(), blue.getNode(), pink.getNode(), orange.getNode()); // add ghosts to the pane

			_ghostPenQueue.add(_red); // add the ghosts to the queue
			_ghostPenQueue.add(_blue);
			_ghostPenQueue.add(_pink);
			_ghostPenQueue.add(_orange);
			
			_ghosts[0] = _red; // store all the ghosts in the ghost array
			_ghosts[1] = _blue; // used to detect collision and hold ghosts in one variable
			_ghosts[2] = _pink;
			_ghosts[3] = _orange;

			break;
			
		default:
			break;
		}
	}	
}