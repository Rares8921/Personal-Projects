package sample.application;
import javafx.application.Application;

	
	public class Launcher {

		/*
		 * There's a known bug with JavaFx, that causes
		 * certain area's not to be repainted when starting
		 * a new Chess game. The workaround for this bug is to 
		 * disable dirty region optimizations, This needs to
		 * be done before the application class is loaded 
		 * (Controller.class). Launcher takes
		 * care of this.
		 */
	    public static void main(String[] args) {
	        System.setProperty("prism.dirtyopts", "false");
	        Application.launch(Controller.class, args);
	    }
	}
	

