package Server;

import java.io.IOException;

import java.util.Vector;

import gui_server.ServerFrameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ServerUI extends Application {
	final public static int DEFAULT_PORT = 5555;
	public static ServerFrameController serverFrame;
	public static CEMSserver sv;
	public static void main(String args[]) throws Exception {
		launch(args);
	} 

	@Override
	public void start(Stage primaryStage) throws Exception {

		serverFrame = new ServerFrameController(); // create Server frame controller		 
		serverFrame.start(primaryStage);
	}

	public ServerFrameController getSFC() {
		return serverFrame;
	}
	
	public static void runServer(String p,ServerFrameController serverFrame) {
		int port = 0; // Port to listen on

		try {
			port = Integer.parseInt(p); // Set port to 5555

		} catch (Throwable t) {
			serverFrame.printToTextArea("ERROR - Could not connect!");
		}

		// instead of loader - need to put object that is the gui controller...
		 sv = new CEMSserver(port,serverFrame);
		
		try {
			sv.listen(); // Start listening for connections
		} catch (Exception ex) {
			serverFrame.printToTextArea("ERROR - Could not listen for clients!");
		}
	}
	
	public static void closeServer(String p,ServerFrameController serverFrame) {
		int port = 0; // Port to listen on

		try {
			port = Integer.parseInt(p); // Set port to 5555

		} catch (Throwable t) {
			serverFrame.printToTextArea("ERROR - Wrong Port!");
		}
		
		try {
			sv.close();
		} catch (IOException e) {
			serverFrame.printToTextArea("ERROR - Could not Disconnect!");
		}

	}

}
