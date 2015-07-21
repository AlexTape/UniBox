package de.alextape.unibox.demo;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import de.unibox.client.api.ClientProvider;
import de.unibox.client.api.IncomingMessageHandler;

/**
 * Just a minimal application demo for UniBox.
 */
public class DemoApplication extends Application {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(final String[] args) {
		
	    // init credentials
	    ClientProvider.setIp(args[0]);
	    ClientProvider.setUsername(args[1]);
	    ClientProvider.setPassword(args[2]);
		
		Application.launch(args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public final void start(final Stage primaryStage) {
		
		// to get a more qualified logging, you are able to set logging levels "true" like
		// Logger.setDebugLogging(false);
		// Logger.setInfoLogging(false);
		// Logger.setWarningsLogging(false);
		// Logger.setErrorLogging(false);
		
		// login and connect
		ClientProvider.login();
		ClientProvider.connect();

		// simple javafx application demo
		primaryStage.setTitle("Hello World!");
		Button btn = new Button();
		btn.setText("Say 'Hello to Chat and JavaClients'");
		btn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				System.out.println("Log: Sending Messages");

				// this message will be received by all clients joined the same
				// game
				ClientProvider.sendGameMessage("Hello Game Clients!");

				// this message will be displayed in the chat panel
				ClientProvider.sendChatMessage("Hello Chat!");
			}
		});

		StackPane root = new StackPane();
		root.getChildren().add(btn);
		primaryStage.setScene(new Scene(root, 300, 250));
		primaryStage.show();

		/**
		 * UniBoxClient: bind event handler for incoming messages.
		 */
		ClientProvider.bind(primaryStage, new IncomingMessageHandler() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * de.unibox.client.api.IncomingMessageHandler#handle(java.lang.
			 * String, java.lang.String)
			 */
			@Override
			public void handle(final String user, final String msg) {

				// log incoming messages to console
				System.out.println("Log: new message from:\"" + user
						+ "\" Content:\"" + msg + "\"");

			}
		});
	}

}
