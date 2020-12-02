package Pacman;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
  * This is the  main class where your Pacman game will start.
  * The main method of this application calls the App constructor. You
  * will need to fill in the constructor to instantiate your game.
  *
  * Class comments here...
  *
  */

public class App extends Application {

    @Override
    public void start(Stage stage) {
    	PaneOrganizer paneOrganizer = new PaneOrganizer(); // new pane organizer
    	Scene scene = new Scene(paneOrganizer.getRoot()); // create scene
    	stage.setScene(scene); // set stage
    	stage.setTitle("Pacman"); // title window
    	stage.setHeight(Constants.WINDOW_HEIGHT); // sizing
    	stage.setWidth(Constants.WINDOW_WIDTH); // sizing
    	
    	stage.show(); // show app
    }

    /*
    * Here is the mainline! No need to change this.
    */
    public static void main(String[] argv) {
        // launch is a method inherited from Application
        launch(argv);
    }
}
