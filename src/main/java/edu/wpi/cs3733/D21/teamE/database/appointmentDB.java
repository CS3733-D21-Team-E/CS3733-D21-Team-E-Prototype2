package edu.wpi.cs3733.D21.teamE.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import edu.wpi.cs3733.D21.teamE.App;
import edu.wpi.cs3733.D21.teamE.DB;

import java.sql.*;

public class appointmentDB {

	static Connection connection = makeConnection.makeConnection("jdbc:derby:BWDB;create=true").connection;

	public static void createAppointmentTable() {
		//TODO: before deleting any users, save their information from userAccount into CSV
		String query = "Create Table appointment( " +
				"    appointmentID Int Primary Key References ToDo, " +
				"    patientID Int References useraccount (userid) On Delete Cascade , " +
				"    doctorID Int References useraccount (userid) On Delete Cascade, " +
				"    appointmentDate varchar(31) Not Null, " +
				"    startTime varchar(31) Not Null, " +
				"    Constraint appointmentUnique Unique(patientID, startTime, appointmentDate) " +
				")";


		try (PreparedStatement prepState = connection.prepareStatement(query)) {

			prepState.execute();

		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("error creating appointment table");
		}
	}

	/**
	 * creates an appointment and adds to the appointmentDB table
	 * @param patientID is the ID of the patient making the appointment
	 * @param startTime is when the appointment starts
	 * @param doctorID  is the doctor assigned to the appointment
	 * @return an int (0 if add fails, 1 if add succeeded)
	 */
	public static int addAppointment(int patientID, String startTime, String date, Integer doctorID) {
		// Not multi-user safe, but hey we only have one client accessing the db at a time
		ToDoDB.addCustomToDo(doctorID, "Appointment #" + (ToDoDB.getMaxToDoID() + 1) + " with " + UserAccountDB.getUserName(patientID));
		ToDoDB.updateToDo(ToDoDB.getMaxToDoID(), null, -1, -1, -1, null, date, startTime, null, null, null, null);

		String insertAddApt = "Insert Into appointment Values(" + ToDoDB.getMaxToDoID() + ", ?, ?, ?, ?)";

		try (PreparedStatement prepState = connection.prepareStatement(insertAddApt)) {
			prepState.setInt(1, patientID);
			prepState.setInt(2, doctorID);
			prepState.setString(3, date);
			prepState.setString(4, startTime);

			prepState.executeUpdate();
			prepState.close();

			return 1;
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error inserting into appointment table inside function addAppointment()");
			return 0;
		}

	}

	/**
	 * edits an appointment
	 * @param appointmentID is the ID of the appointment
	 * @param newStartTime  is the new start time of the appointment
	 * @param newDoctorID   is the new doctor assigned
	 * @return an int (0 if add fails, 1 if add succeeded)
	 */
	public static int editAppointment(int appointmentID, String newStartTime, String newDate, Integer newDoctorID) {

		boolean added = false;

		String query = "update appointment set ";


		if (newStartTime != null) {
			query = query + "startTime = '" + newStartTime + "'";

			added = true;
		}
		if (newDate != null) {
			if (added == true) {
				query = query + ", ";
			}
			query = query + "appointmentDate = '" + newDate + "'";
			added = true;
		}
		if (newDoctorID != null) {
			if (added == true) {
				query = query + ", ";
			}
			query = query + "doctorID = " + newDoctorID;
		}

		query = query + " where appointmentID = " + appointmentID;
		try (PreparedStatement prepState = connection.prepareStatement(query)) {
			prepState.executeUpdate();
			prepState.close();
			return 1;
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error in updating appointment table");
			return 0;
		}

	}

	/**
	 * cancels an appointment given the appointmentID
	 * @param appointmentID is the ID of the appointment
	 * @return an int (0 if add fails, 1 if add succeeded)
	 */
	public static int cancelAppointment(int appointmentID) {

		String insertCancelQuery = "Delete From appointment Where appointmentid = ?";

		try (PreparedStatement prepState = connection.prepareStatement(insertCancelQuery)) {
			prepState.setInt(1, appointmentID);
			prepState.execute();
			DB.updateToDoStatus(appointmentID, 0);
			return 1;
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error deleting from cancelAppointment inside function cancelAppointment()");
			return 0;
		}

	}

	public static int getAppointmentID(int patientID, String startTime, String date) {
		String getAppointmentID = "Select * From appointment Where patientID = ? And startTime = ? And appointmentDate = ?";
		int appointmentID = 0;
		try (PreparedStatement prepState = connection.prepareStatement(getAppointmentID)) {

			prepState.setInt(1, patientID);
			prepState.setString(2, date);
			prepState.setString(3, startTime);

			ResultSet rset = prepState.executeQuery();

			while (rset.next()) {
				appointmentID = rset.getInt("appointmentID");
			}


			rset.close();
			prepState.close();
			return appointmentID;


		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error getting appointmentID inside function getAppointmentID()");
			return 0;
		}

	}

}

