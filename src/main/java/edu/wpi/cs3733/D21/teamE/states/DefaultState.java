package edu.wpi.cs3733.D21.teamE.states;

import edu.wpi.cs3733.D21.teamE.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import java.io.IOException;

public class DefaultState implements State{

    public void switchScene(ActionEvent event) {

        String buttonName = ((Button) event.getSource()).getId();

        switch (buttonName) {
            case "covidSurvey":
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/D21/teamE/fxml/CovidSurvey.fxml"));
                    App.changeScene(root);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            case "pathFinderButton":
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/D21/teamE/fxml/PathFinder.fxml"));
                    App.changeScene(root);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            case "serviceRequestButton":
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/D21/teamE/fxml/ServiceRequests.fxml"));
                    App.changeScene(root);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            case "mapEditorButton":
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/D21/teamE/fxml/newMapEditor.fxml"));
                    App.changeScene(root);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            case "userManagementButton":
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/D21/teamE/fxml/UserManagement.fxml"));
                    App.changeScene(root);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            case "directionsButton":
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/D21/teamE/fxml/Directions.fxml"));
                    App.changeScene(root);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            case "scheduleAppointmentButton":
                try {
                Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/D21/teamE/fxml/updatedServiceRequests/Appointment.fxml"));
                App.changeScene(root);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            case "checkInStatusButton":
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/D21/teamE/fxml/CheckInStatus.fxml"));
                    App.changeScene(root);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            case "aboutButton":
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/D21/teamE/fxml/About.fxml"));
                    App.changeScene(root);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            case "creditButton":
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/D21/teamE/fxml/Credit.fxml"));
                    App.changeScene(root);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            }
        }
    }