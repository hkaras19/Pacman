package Pacman;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/*
 * PaneOrganizer class
 * Organizes the different panes in the window
 * Top level visual organizer for app
 * Instantiates the main game scene and quit pane
*/

public class PaneOrganizer {
	private BorderPane _root; // root pane for the organizer
	
	/*
	 * constructor
	 * instantiates the game and quit panes
	 * sets up the visual of the scene
	*/
	
	public PaneOrganizer() {
		_root = new BorderPane(); // new border pane
		
		Label score = new Label("Score: 0"); // score label
		score.setTextFill(Color.WHITE); // text color
		
		Label lives = new Label("Lives: 3"); // lives label
		lives.setTextFill(Color.WHITE); // text color
		
		Game game = new Game(score, lives); // game pane
		HBox hbox = new HBox(); // hbox to organize the quit button

		Button quit = new Button("Quit"); // Creating quit button
		quit.setOnAction(new QuitHandler()); // add the quit handler
		quit.minHeight(Constants.QUIT_HEIGHT); // sizing

		hbox.getChildren().addAll(quit, score, lives); // add components to hbox
		hbox.setSpacing(20);
		
		_root.setCenter(game); // set the game pane
		_root.setBottom(hbox); // set the hbox
		_root.setStyle("-fx-background-color: black");
	}
	
	/*
	 * Gets the border pane 
	 * Used by the App class
	*/
	
	public BorderPane getRoot() {
		return _root;
	}
	
	/*
	 * QuitHandler will quit the app 
	 * Used by quit button
	*/
	
	private class QuitHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent event) {
			Platform.exit(); // quit app
		}
	}
}