/**
 * Sample Skeleton for 'FoodDelivery.fxml' Controller Class
 */

package edu.wpi.cs3733.D21.teamE.views.serviceRequestControllers;

import java.io.IOException;

import com.jfoenix.validation.RequiredFieldValidator;
import edu.wpi.cs3733.D21.teamE.App;
import edu.wpi.cs3733.D21.teamE.DB;
import edu.wpi.cs3733.D21.teamE.email.sendEmail;
import edu.wpi.cs3733.D21.teamE.views.serviceRequestObjects.FoodDeliveryObj;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.lang.String;
import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javax.mail.MessagingException;


public class FoodDelivery extends ServiceRequestFormComponents {


	ObservableList<String> locations;
	ArrayList<String> nodeID = new ArrayList<>();
	ObservableList<String> userNames;
	ArrayList<Integer> userID = new ArrayList<>();

	@FXML // fx:id="background"
	private ImageView background;

	@FXML // fx:id="fullscreen"
	private Rectangle fullscreen; // Value injected by FXMLLoader

	@FXML // fx:id="hide"
	private Circle hide; // Value injected by FXMLLoader

	@FXML // fx:id="exit"
	private Polygon exit; // Value injected by FXMLLoader

	@FXML // fx:id="locationInput"
	private JFXComboBox<String> locationInput; // Value injected by FXMLLoader

	@FXML // fx:id="assigneeInput"
	private JFXComboBox<String> assigneeInput; // Value injected by FXMLLoader

	@FXML // fx:id="foodInput"
	private JFXTextField deliveryService; // Value injected by FXMLLoader

	@FXML // fx:id="beveragesInput"
	private JFXTextField orderNumber; // Value injected by FXMLLoader

	@FXML // fx:id="descriptionInput"
	private JFXTextArea descriptionInput; // Value injected by FXMLLoader

	@FXML // fx:id="cancel"
	private JFXButton cancel; // Value injected by FXMLLoader

	@FXML // fx:id="submit"
	private JFXButton submit; // Value injected by FXMLLoader

	@FXML
	public AnchorPane appBarAnchorPane;

	@FXML
	private StackPane stackPane;

	@FXML
	void handleButtonCancel(ActionEvent event) {
		super.handleButtonCancel(event);
	}

	private boolean validateInput() {

		RequiredFieldValidator validator = new RequiredFieldValidator();
		validator.setMessage("Input Required");

		locationInput.getValidators().add(validator);
		assigneeInput.getValidators().add(validator);
		deliveryService.getValidators().add(validator);
		orderNumber.getValidators().add(validator);
		descriptionInput.getValidators().add(validator);

		return locationInput.validate() && assigneeInput.validate() && deliveryService.validate()
				&& orderNumber.validate() && descriptionInput.validate();
	}

	@FXML
	void saveData(ActionEvent event) throws MessagingException {
		if(validateInput()) {
			//Setting indexes for picking from the id lists
			int nodeIndex = locationInput.getSelectionModel().getSelectedIndex();
			int userIndex = assigneeInput.getSelectionModel().getSelectedIndex();
			//Setting up data to add into object
			String node = nodeID.get(nodeIndex);
			int user = userID.get(userIndex);
			String deliverer = deliveryService.getText();
			String orderNum = orderNumber.getText();
			String desc = descriptionInput.getText();
			//creating object and passing it to database
			FoodDeliveryObj object = new FoodDeliveryObj(0,App.userID,node,user,deliverer,orderNum, desc);
			DB.addFoodDeliveryRequest(object);


			//email stuff
			String email = DB.getEmail(App.userID);
			String fullName = DB.getUserName(App.userID);
			String assigneeName = userNames.get(userIndex);

			String body = "Hello " + fullName + ", \n\n" + "Thank you for making an External Patient Transport request." +
					"Here is the summary of your request: \n" +
					" - Location: " + node + "\n\n" +
					" - Assignee Name: " + assigneeName + "\n" +
					" - Delivery Service: " + deliverer + "\n" +
					" - Order Number: " + orderNum + "\n" +
					" - Description: " + desc + "\n\n" +
					"If you need to edit any details, please visit our app to do so. We look forward to seeing you soon!\n\n" +
					"- Emerald Emus BWH";

			sendEmail.sendRequestConfirmation(email, body);


			super.handleButtonSubmit(event);




		}
	}

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {

		Stage primaryStage = App.getPrimaryStage();
		Image backgroundImg = new Image("edu/wpi/cs3733/D21/teamE/hospital.jpg");
		Image backgroundImage = backgroundImg;
		background.setImage(backgroundImage);
		background.setEffect(new GaussianBlur());

		//background.setPreserveRatio(true);
		background.fitWidthProperty().bind(primaryStage.widthProperty());
		//background.fitHeightProperty().bind(primaryStage.heightProperty());

		locations = DB.getAllNodeLongNames();
		nodeID = DB.getListOfNodeIDS();
		locationInput.setItems(locations);

		userNames = DB.getAssigneeNames("nurse");
		userID = DB.getAssigneeIDs("nurse");
		assigneeInput.setItems(userNames);

		assert fullscreen != null : "fx:id=\"fullscreen\" was not injected: check your FXML file 'FoodDelivery.fxml'.";
		assert hide != null : "fx:id=\"hide\" was not injected: check your FXML file 'FoodDelivery.fxml'.";
		assert exit != null : "fx:id=\"exit\" was not injected: check your FXML file 'FoodDelivery.fxml'.";
		assert locationInput != null : "fx:id=\"locationInput\" was not injected: check your FXML file 'FoodDelivery.fxml'.";
		assert assigneeInput != null : "fx:id=\"assigneeInput\" was not injected: check your FXML file 'FoodDelivery.fxml'.";
		assert descriptionInput != null : "fx:id=\"descriptionInput\" was not injected: check your FXML file 'FoodDelivery.fxml'.";
		assert cancel != null : "fx:id=\"cancel\" was not injected: check your FXML file 'FoodDelivery.fxml'.";
		assert submit != null : "fx:id=\"submit\" was not injected: check your FXML file 'FoodDelivery.fxml'.";

		//init appBar
		javafx.scene.Node appBarComponent = null;
		try {
			App.setShowHelp(false); // show help or not
			App.setShowLogin(true); // show login or not
			App.setPageTitle("Food Delivery Request (Ashley Burke)"); //set AppBar title
			App.setHelpText(""); //set help text
			App.setStackPane(stackPane); // required for dialog boxes, otherwise set null?
			appBarComponent = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/D21/teamE/fxml/AppBarComponent.fxml"));
			appBarAnchorPane.getChildren().add(appBarComponent); //add FXML to this page's anchorPane element
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Move to Service Request page
	 * @param e
	 */
	@FXML
	private void toAubonPainMenu(ActionEvent e) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/D21/teamE/fxml/MenuPage.fxml"));
			App.changeScene(root);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}


}
