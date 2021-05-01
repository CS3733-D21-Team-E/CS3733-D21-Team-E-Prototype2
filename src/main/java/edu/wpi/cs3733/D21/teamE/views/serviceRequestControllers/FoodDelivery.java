/**
 * Sample Skeleton for 'FoodDelivery.fxml' Controller Class
 */

package edu.wpi.cs3733.D21.teamE.views.serviceRequestControllers;

import java.io.IOException;

import com.jfoenix.validation.RequiredFieldValidator;
import edu.wpi.cs3733.D21.teamE.App;
import edu.wpi.cs3733.D21.teamE.DB;
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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class FoodDelivery extends ServiceRequestFormComponents {

	ObservableList<String> locations;
	ArrayList<String> nodeIDs = new ArrayList<>();
	ObservableList<String> names;
	ArrayList<Integer> userID = new ArrayList<>();

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
	void handleButtonCancel(ActionEvent event) {
		super.handleButtonCancel(event);
	}

	private boolean validateInput() {
		RequiredFieldValidator validator = new RequiredFieldValidator();
		validator.setMessage("Input required");

		locationInput.getValidators().add(validator);
		assigneeInput.getValidators().add(validator);
		deliveryService.getValidators().add(validator);
		orderNumber.getValidators().add(validator);
		descriptionInput.getValidators().add(validator);

		return locationInput.validate() && assigneeInput.validate() && deliveryService.validate()
				&& orderNumber.validate() && descriptionInput.validate();
	}


	@FXML
	void saveData(ActionEvent event) {
		if(validateInput()) {
			int locationIndex = locationInput.getSelectionModel().getSelectedIndex();
			int userIndex = assigneeInput.getSelectionModel().getSelectedIndex();

			String loc = nodeIDs.get(locationIndex);
			int user = userID.get(userIndex);
			String rest = deliveryService.getText();
			String number = orderNumber.getText();
			String desc = descriptionInput.getText();

			DB.addFoodDeliveryRequest(App.userID, loc, user, rest, number, desc);
			super.handleButtonSubmit(event);
		}
	}

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {

		locations = DB.getAllNodeLongNames();
		nodeIDs = DB. getListOfNodeIDS();
		//todo add proper types here
		names = DB.getAssigneeNames("Add user type");
		userID = DB.getAssigneeIDs("Add user type");

		locationInput.setItems(locations);
		assigneeInput.setItems(names);

		assert fullscreen != null : "fx:id=\"fullscreen\" was not injected: check your FXML file 'FoodDelivery.fxml'.";
		assert hide != null : "fx:id=\"hide\" was not injected: check your FXML file 'FoodDelivery.fxml'.";
		assert exit != null : "fx:id=\"exit\" was not injected: check your FXML file 'FoodDelivery.fxml'.";
		assert locationInput != null : "fx:id=\"locationInput\" was not injected: check your FXML file 'FoodDelivery.fxml'.";
		assert assigneeInput != null : "fx:id=\"assigneeInput\" was not injected: check your FXML file 'FoodDelivery.fxml'.";
		assert descriptionInput != null : "fx:id=\"descriptionInput\" was not injected: check your FXML file 'FoodDelivery.fxml'.";
		assert deliveryService != null;
		assert orderNumber != null;
		assert cancel != null : "fx:id=\"cancel\" was not injected: check your FXML file 'FoodDelivery.fxml'.";
		assert submit != null : "fx:id=\"submit\" was not injected: check your FXML file 'FoodDelivery.fxml'.";

	}

	//TODO move this to the state stuff
	/**
	 * Move to Service Request page
	 * @param e
	 */
	@FXML
	private void toAubonPainMenu(ActionEvent e) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/D21/teamE/fxml/MenuPage.fxml"));
			App.setDraggableAndChangeScene(root);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}


}
