package edu.wpi.cs3733.D21.teamE.views.serviceRequestControllers;

/**
 * Sample Skeleton for 'Appointment.fxml' Controller Class
 */

import com.jfoenix.controls.*;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.jfoenix.validation.RequiredFieldValidator;

import edu.wpi.cs3733.D21.teamE.App;
import edu.wpi.cs3733.D21.teamE.DB;
import edu.wpi.cs3733.D21.teamE.email.SheetsAndJava;
import edu.wpi.cs3733.D21.teamE.email.sendEmail;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import javax.mail.MessagingException;

public class Appointment extends ServiceRequestFormComponents{


	RequiredFieldValidator validator = new RequiredFieldValidator();
	ObservableList<String> userNames;
	ArrayList<Integer> userID = new ArrayList<>();

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="fullscreen"
	private Rectangle fullscreen; // Value injected by FXMLLoader

	@FXML // fx:id="hide"
	private Circle hide; // Value injected by FXMLLoader

	@FXML // fx:id="exit"
	private Polygon exit; // Value injected by FXMLLoader





	@FXML // fx:id="dateInput"
	private JFXDatePicker dateInput; // Value injected by FXMLLoader
	@FXML // fx:id="startTimeInput"
	private JFXTimePicker startTimeInput; // Value injected by FXMLLoader

	@FXML // fx:id="doctorInput"
	private JFXComboBox<String> doctorInput; // Value injected by FXMLLoader
	@FXML // fx:id="additionalNotesInput"
	private JFXTextArea additionalNotesInput; // Value injected by FXMLLoader


	@FXML // fx:id="oneMonthPriorInput"
	private JFXCheckBox oneMonthPriorInput; // Value injected by FXMLLoader
	@FXML // fx:id="twoWeeksPriorInput"
	private JFXCheckBox twoWeeksPriorInput; // Value injected by FXMLLoader
	@FXML // fx:id="oneWeekPriorInput"
	private JFXCheckBox oneWeekPriorInput; // Value injected by FXMLLoader
	@FXML // fx:id="twoDaysPriorInput"
	private JFXCheckBox twoDaysPriorInput; // Value injected by FXMLLoader
	@FXML // fx:id="oneDayPriorInput"
	private JFXCheckBox oneDayPriorInput; // Value injected by FXMLLoader







	@FXML // fx:id="cancel"
	private JFXButton cancel; // Value injected by FXMLLoader

	@FXML // fx:id="submit"
	private JFXButton submit; // Value injected by FXMLLoader

	@FXML
	void handleButtonCancel(ActionEvent event) {

	}



	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		userID = DB.getAssigneeIDs("doctor");
		userNames = DB.getAssigneeNames("doctor");

		assert fullscreen != null : "fx:id=\"fullscreen\" was not injected: check your FXML file 'Appointment.fxml'.";
		assert hide != null : "fx:id=\"hide\" was not injected: check your FXML file 'Appointment.fxml'.";
		assert exit != null : "fx:id=\"exit\" was not injected: check your FXML file 'Appointment.fxml'.";
		assert doctorInput != null : "fx:id=\"DoctorInput\" was not injected: check your FXML file 'Appointment.fxml'.";

		doctorInput.setItems(userNames);

		assert additionalNotesInput != null : "fx:id=\"descriptionInput\" was not injected: check your FXML file 'Appointment.fxml'.";
		assert cancel != null : "fx:id=\"cancel\" was not injected: check your FXML file 'Appointment.fxml'.";
		assert submit != null : "fx:id=\"submit\" was not injected: check your FXML file 'Appointment.fxml'.";

	}

	/**
	 * records inputs from user into a series of String variables and returns to the main page
	 * @param actionEvent
	 */
	@FXML
	private void saveData(ActionEvent actionEvent) throws MessagingException, IOException, GeneralSecurityException {


		if (validateInput()) {

			String date = dateInput.getValue().toString();
			System.out.println("date " + date);
			String startTime = startTimeInput.getValue().toString();
			System.out.println("startTime " + startTime);

			int doctorIndex = doctorInput.getSelectionModel().getSelectedIndex();
			System.out.println("doctorIndex " + doctorIndex);

			Integer doctorID = userID.get(doctorIndex);

			String additionalNotes = additionalNotesInput.getText();
			System.out.println("additionalNotes " + additionalNotes);

			boolean oneMonthPrior = oneMonthPriorInput.isSelected();
			System.out.println("oneMonthPrior " + oneMonthPrior);
			boolean twoWeeksPrior = twoWeeksPriorInput.isSelected();
			System.out.println("twoWeeksPrior " + twoWeeksPrior);
			boolean oneWeekPrior = oneWeekPriorInput.isSelected();
			System.out.println("oneWeekPrior " + oneWeekPrior);
			boolean twoDaysPrior = twoDaysPriorInput.isSelected();
			System.out.println("twoDaysPrior " + twoDaysPrior);
			boolean oneDayPrior = oneDayPriorInput.isSelected();
			System.out.println("oneDayPrior " + oneDayPrior);

			super.handleButtonSubmit(actionEvent);

			DB.addAppointment(App.userID, startTime, doctorID);

			String email = DB.getEmail(App.userID);
			String fullName = DB.getUserName(App.userID);
			sendEmail.sendAppointmentConfirmation(email, startTime, fullName);
			int position = fullName.indexOf(" ");

			String firstName = fullName.substring(0, position);
			String lastName = fullName.substring(position);
			String dateAndTime = date + " " + startTime;

			if (oneMonthPrior == true) {
				SheetsAndJava.addAppointmentToSheet(1, email, firstName, lastName, userNames.get(doctorIndex), dateAndTime, "1 Month Prior");
			}
			if (twoWeeksPrior == true) {
				SheetsAndJava.addAppointmentToSheet(1, email, firstName, lastName, userNames.get(doctorIndex), dateAndTime, "2 Weeks Prior");
			}
			if (oneWeekPrior == true) {
				SheetsAndJava.addAppointmentToSheet(1, email, firstName, lastName, userNames.get(doctorIndex), dateAndTime, "1 Week Prior");
			}
			if (twoDaysPrior == true) {
				SheetsAndJava.addAppointmentToSheet(1, email, firstName, lastName, userNames.get(doctorIndex), dateAndTime, "48 Hours Prior");
			}
			if (oneDayPrior == true) {
				SheetsAndJava.addAppointmentToSheet(1, email, firstName, lastName, userNames.get(doctorIndex), dateAndTime, "24 Hours Prior");
			}

		}
	}

	/**
	 * Detects if the user has entered all required fields
	 */
	private boolean validateInput() {

		validator.setMessage("Input required");


		dateInput.getValidators().add(validator);
		startTimeInput.getValidators().add(validator);
		doctorInput.getValidators().add(validator);
		additionalNotesInput.getValidators().add(validator);


		return dateInput.validate() && startTimeInput.validate() &&
				doctorInput.validate() && additionalNotesInput.validate();
	}

}
