package edu.wpi.cs3733.D21.teamE;

import edu.wpi.cs3733.D21.teamE.database.makeConnection;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import edu.wpi.cs3733.D21.teamE.database.makeConnection;
import edu.wpi.cs3733.D21.teamE.email.sendEmail;
import edu.wpi.cs3733.D21.teamE.map.Node;
import edu.wpi.cs3733.D21.teamE.scheduler.ToDo;
import edu.wpi.cs3733.D21.teamE.views.AppBarComponent;
import edu.wpi.cs3733.D21.teamE.views.CovidSurveyObj;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.io.*;
import java.util.Scanner;

import org.apache.derby.drda.NetworkServerControl;


import javax.mail.MessagingException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Logger;


public class App extends Application {

	Logger logger = Logger.getLogger("BWH");

	/*-------------------------------------
	* 	   VARIABLES/SETTERS/GETTERS
	*------------------------------------*/

	/**The JavaFX application's primary stage. All Scenes are built upon this stage*/
	private static Stage primaryStage;

	/**Sets the visibility of the login button.
	 * See {@link AppBarComponent} for further information.*/
	public static boolean showLogin;

	/**Sets the page title.
	 * See {@link AppBarComponent} for further information.*/
	private static String pageTitle;

	/**Sets the help dialog contents.
	 * See {@link AppBarComponent} for further information.*/
	private static String helpText;

	/**Sets the stackPane used by appBar for {@link #newJFXDialogPopUp(String, String, String, StackPane)} calls.
	 * See {@link AppBarComponent} for further information.*/
	private static StackPane stackPane;

	/**Sets the visibility of the help button
	 * See {@link AppBarComponent} for further information.*/
	private static boolean showHelp = false;

	/**Sets the current searchAlgorithm for the PathFinder page.
	 * Is set to A* by default (0)*/
	private static int searchAlgo = 0;

	/**Value of currently logged in user.
	 * 0 Indicates no user logged in.*/
	public static int userID = 0;

	/**@// todo document this*/
	public static boolean noCleanSurveyYet = true;

	/**
	 * Used by pathfinder to note whether it is a guest user using pathfinder or someone with an ID
	 */
	public static boolean guestGoingToPathfinder = false;

	/**
	 * used to handle guest covid surveys because they don't have their own row within the table
	 */
	public static CovidSurveyObj guestSurvey = null;

	/**
	 *
	 */
	public static boolean guestPossibleRisk = false;

	/** Nodes and info for pathfinder to get on init
	 * No nodes on default, no emergency on default */
	private static Node startNode = null;
	private static Node endNode = null;
	private static boolean toEmergency = false;

	private static ToDo editToDo = null;
	private static LocalDate toDoDate = null;

	private static boolean lockEndPath = false;
	public static String driverURL;
	public static String nextDriverURL;

	public App() {

	}


	//setters and getters for above variables
	public static boolean isShowLogin() { return showLogin; }
	public static void setShowLogin(boolean showLogin) { App.showLogin = showLogin; }
	public static String getHelpText() { return helpText; }
	public static void setHelpText(String helpText) { App.helpText = helpText; }
	public static String getPageTitle() { return pageTitle; }
	public static void setPageTitle(String pageTitle) { App.pageTitle = pageTitle; }
	public static StackPane getStackPane() { return stackPane; }
	public static void setStackPane(StackPane stackPane) { App.stackPane = stackPane; }
	public static boolean isShowHelp() { return showHelp; }
	public static void setShowHelp(boolean showHelp) { App.showHelp = showHelp; }
	public static int getSearchAlgo() { return searchAlgo; }
	public static void setSearchAlgo(int searchAlgo) { App.searchAlgo = searchAlgo; }
	public static void setPrimaryStage(Stage primaryStage) { App.primaryStage = primaryStage; }
	public static Stage getPrimaryStage() { return primaryStage; }
	public static Node getStartNode() { return startNode; }
	public static void setStartNode(Node startNode) { App.startNode = startNode; }
	public static Node getEndNode() { return endNode; }
	public static void setEndNode(Node endNode) { App.endNode = endNode; }
	public static boolean isToEmergency() { return toEmergency; }
	public static void setToEmergency(boolean toEmergency) { App.toEmergency = toEmergency; }
	public static ToDo getToDo() { return App.editToDo; }
	public static void setToDo(ToDo todo) { App.editToDo = todo; }
	public static LocalDate getToDoDate() { return App.toDoDate; }
	public static void setToDoDate(LocalDate date) { App.toDoDate = date; }

	public static boolean isLockEndPath() { return lockEndPath; }
	public static void setLockEndPath(boolean lockEndPath) { App.lockEndPath = lockEndPath; }
	public static void setNextDriverURL(String url){ nextDriverURL = url; }


	/*---------------------------------
	 *		JAVAFX APP FUNCTIONS
	 *--------------------------------*/

	/**
	 * Runs on Application (pre)startup and sets up the DB.
	 *
	 * Makes a connection to the DB to check if the proper tables exist in the right places.
	 * If not, it will repopulate the DB with data from {@link makeConnection#addDataForPresentation()}.
	 *
	 *
	 */
	@Override
	public void init() throws Exception {


		logger.info("Starting App Initialization");


		// reading the driverOption.txt file
		try {
			File file = new File("driverOption.txt");
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String data = scanner.nextLine();
				driverURL = data;
				nextDriverURL = data;
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			logger.warning("Could Not find driverOption.txt, " + e);
		}


		logger.finer("Connecting to the DB...");
		makeConnection connection = makeConnection.makeConnection(driverURL);
		logger.finer("DB connection established");
		File nodes = new File("CSVs/MapEAllnodes.csv");
		File edges = new File("CSVs/MapEAlledges.csv");
		boolean tablesExist = connection.allTablesThere();
		if(!tablesExist){
			logger.info("DB is missing, repopulating...");
			try {
				DB.createAllTables();
				DB.populateTable("node", nodes);
				DB.populateTable("hasEdge", edges);
				connection.addDataForPresentation();
				DB.populateAbonPainTable();
				logger.info("Tables Repopulated");
			} catch (Exception e) {
				logger.warning("Exception in creating tables. Might already be there?, " + e);
			}
		}
		logger.info("App Initialization Complete.");
	}

	/**
	 * Runs on Application startup, post-{@link #init()}.
	 * Sets up the window starting/default size and size constraints.
	 * Adds the {@link ResizeHelper} listener to the application.
	 *
	 * @param primaryStage primaryStage of the application. Will set App's {@link #primaryStage} to this value.
	 * @throws IOException thrown when the specified FXML cannot be found.
	 */
	@Override
	public void start(Stage primaryStage) throws IOException {
		//set app title
		primaryStage.setTitle("BWH Application - D21 Team E"); //todo, come up with final title for app

		//Grab FXML for and set primary stage properties.
		App.primaryStage = primaryStage;
		try {
			Parent root = FXMLLoader.load(getClass().getResource("fxml/Default.fxml")); //get the Default FXMl
			primaryStage.initStyle(StageStyle.UNDECORATED); //set undecorated
			//set scene for primaryStage
			Scene scene = new Scene(root);
			logger.finest("Scene Added");
			Image icon = new Image(getClass().getResourceAsStream("Logo.png"));
			logger.finest("Logo Retrieved");
			primaryStage.getIcons().add(icon);
			logger.finest("Icon Added");
			primaryStage.setScene(scene);
			//set default sizes
			primaryStage.setWidth(1200);
			primaryStage.setHeight(785);
			//add ResizeListener
			ResizeHelper.addResizeListener(primaryStage, 1120, 775, Double.MAX_VALUE, Double.MAX_VALUE);
			//show stage
			logger.fine("Showing Stage");
			primaryStage.show();
		} catch (IOException e) {
			logger.severe("Could not successfully start JavaFX application, force exiting...");
			e.printStackTrace();
			Platform.exit();
		}

		checkAndSendCrashReport();
	}

	/**
	 * Uses {@link Main#isSafeExitedLog0} & {@link Main#isSafeExitedLog1} to check for a crash.
	 * If there was a crash, asks the user if they would like to report it.
	 * @throws IOException
	 */
	private void checkAndSendCrashReport() throws IOException {

		//if both logs exited safely
		if (Main.isSafeExitedLog0 && Main.isSafeExitedLog1) {
			logger.info("No crashes were detected");
		} else { //else, a log indicates an unexpected exit
			//bad exit, prompt user to report error
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Crash Report");
			alert.setHeaderText("The application did not quit successfully");
			alert.setContentText("Would you like to send a crash report to the developers?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK){
				// user chose OK

				//get crash report
				String crashReportContents = "";
				List<String> lines = Files.readAllLines(Paths.get("BWHCrash.log"), StandardCharsets.US_ASCII);

				//read crash report contents string
				for (String line :
						lines) {
					assert crashReportContents != null;
					crashReportContents = crashReportContents.concat("\n" + line);
				}

				//send crash report email
				try {
					//prep email
					logger.fine("Preparing To Send Crash Report");


					//send email
					logger.info("Sending Crash Report Email");
					sendEmail.sendEmail("engineeringsoftware3733@gmail.com","Crash Report",crashReportContents);

				} catch (MessagingException e) {
					logger.warning("Crash Report Sending Failed, " + e);
					e.printStackTrace();
				}
			} else {
				//user chose CANCEL or closed the dialog
				logger.info("User Ignored Crash Report");
			}
		}
	}

	/**
	 * Stops the application safely.
	 */
	@Override
	public void stop() {
		logger.info("App Shutdown Requested");
		if(!driverURL.equals(nextDriverURL)){
			//ovewrite to textfile
			try {
				FileWriter myWriter = new FileWriter("driverOption.txt");
				myWriter.write(nextDriverURL);
				myWriter.close();
				logger.info("Successfully wrote to driverOption.txt");
			} catch (IOException e) {
				logger.severe("Could not successfully write to driverOption.txt on shutdown");
				e.printStackTrace();
			}
		}
		logger.info("Exiting"); //DO NOT CHANGE THIS LINE. It is used for crash reporting. DO NOT CHANGE THIS LINE
		System.exit(0);
	}


	/*-------------------------------------
	 * 	   APP-WIDE CALLABLE FUNCTIONS
	 *------------------------------------*/

	/**
	 * Creates a new JFX Dialog on the current page.
	 * @param message Message to display in the dialog box.
	 * @param stackPane stack pane needed for Dialog to appear on top of. Will be centered on this pane.
	 */
	public static void newJFXDialogPopUp(String heading, String button, String message, StackPane stackPane) {
		Logger.getLogger("BWH").finer("Dialog box posting");
		JFXDialogLayout jfxDialogLayout = new JFXDialogLayout();
		jfxDialogLayout.setHeading(new Label(heading));
		jfxDialogLayout.setBody(new Text(message));
		JFXDialog dialog = new JFXDialog(stackPane, jfxDialogLayout, JFXDialog.DialogTransition.CENTER);
		JFXButton okay = new JFXButton(button);
		okay.getStyleClass().add("dialog-accept");
		okay.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				dialog.close();

			}
		});
		jfxDialogLayout.setActions(okay);
		dialog.show();
		Logger.getLogger("BWH").finer("Dialog box posted");
	}

	public static void databaseChangePopup(String heading, String message, StackPane stackPane) {
		Logger.getLogger("BWH").finer("Dialog box posting");
		JFXDialogLayout jfxDialogLayout = new JFXDialogLayout();
		jfxDialogLayout.setHeading(new Label(heading));
		jfxDialogLayout.setBody(new Text(message));
		JFXDialog dialog = new JFXDialog(stackPane, jfxDialogLayout, JFXDialog.DialogTransition.CENTER);
		JFXButton cancelButton = new JFXButton("Ok");
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				dialog.close();

			}
		});
		jfxDialogLayout.setActions(cancelButton);
		dialog.show();
		Logger.getLogger("BWH").finer("Dialog box posted");
	}

	/**
	 * Changes the currently displayed scene.
	 * i.e. in the case of changing the "page" in the UI.
	 *
	 * @param root Typically a value retrived via an {@link FXMLLoader}, pointing to new FXML.
	 */
	public static void changeScene(Parent root) {
		primaryStage.getScene().setRoot(root);
	}
}
