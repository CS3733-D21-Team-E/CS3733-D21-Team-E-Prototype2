package edu.wpi.cs3733.D21.teamE.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class csvDB {

	static Connection connection = makeConnection.makeConnection().getConnection();

	/**
	 * Reads csv & inserts into table
	 * @param tableName name of the table that needs to be populated
	 * @param file      file that is going to be read
	 */
	public static void populateTable(String tableName, File file) {

		try {
			// creates a file with the file name we are looking to read
			// File file = new File(csvFileName);
			// used to read data from a file
			FileReader fr = new FileReader(file);

			// used to read the text from a character-based input stream.
			BufferedReader br = new BufferedReader(fr);

			String line;
			String[] tempArr;

			// reads first line (this is the header of each file and we don't need it)
			br.readLine();

			// if there is something in the file (after line 1)
			while ((line = br.readLine()) != null) {

				// adds arguments into the array separated by the commas ("," is when it knows the next
				// index)
				tempArr = line.split(",");

				switch (tableName) {
					case "node":
						PreparedStatement nodePS = connection.prepareStatement("Insert Into node Values (?, ?, ?, ?, ?, ?, ?, ?)");
						nodePS.setString(1, tempArr[0]);
						nodePS.setInt(2, Integer.parseInt(tempArr[1]));
						nodePS.setInt(3, Integer.parseInt(tempArr[2]));
						nodePS.setString(4, tempArr[3]);
						nodePS.setString(5, tempArr[4]);
						nodePS.setString(6, tempArr[5]);
						nodePS.setString(7, tempArr[6]);
						nodePS.setString(8, tempArr[7]);
						nodePS.executeUpdate();
						nodePS.close();
						break;
					case "hasEdge":
						PreparedStatement hasEdgePS = connection.prepareStatement("Insert Into hasEdge Values (?, ?, ?, Null)");
						hasEdgePS.setString(1, tempArr[0]);
						hasEdgePS.setString(2, tempArr[1]);
						hasEdgePS.setString(3, tempArr[2]);
						hasEdgePS.executeUpdate();
						EdgeDB.addLength(tempArr[1], tempArr[2]);
						break;
					default:
						break;
				}
			}
			// closes the BufferedReader
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("error in populateTable() from csvDB");
		}
	}

	/**
	 * Translates a table into a csv file which can be later returned to the user.
	 * @param tableName this is the table/data/information that will be translated into a csv file
	 */
	public static void getNewCSVFile(String tableName) {

		String sqlQuery = "select * from " + tableName;
		try (PreparedStatement prepState = connection.prepareStatement(sqlQuery)) {

			ResultSet rset1 = prepState.executeQuery();

			if (tableName == "node") {

				StringBuilder nodeSB = new StringBuilder();
				while (rset1.next()) {

					//File nodeCSV = new File("src/main/resources/edu/wpi/cs3733/D21/teamE/output/outputNode.csv");
					nodeSB.append(rset1.getString("nodeID")).append(",");
					nodeSB.append(rset1.getInt("xCoord")).append(",");
					nodeSB.append(rset1.getInt("yCoord")).append(",");
					nodeSB.append(rset1.getString("floor")).append(",");
					nodeSB.append(rset1.getString("building")).append(",");
					nodeSB.append(rset1.getString("nodeType")).append(",");
					nodeSB.append(rset1.getString("longName")).append(",");
					nodeSB.append(rset1.getString("shortName")).append("\n");

				}

				// create a file writer to write to files
				FileWriter nodeWriter = new FileWriter("CSVs/outputNode.csv");
				nodeWriter.write("nodeID, xCoord, yCoord, floor, building, nodeType, longName, shortName\n");
				nodeWriter.write(String.valueOf(nodeSB));
				nodeWriter.close();
			}

			rset1.close();

			ResultSet rset2 = prepState.executeQuery();

			if (tableName == "hasEdge") {
				StringBuilder edgeSB = new StringBuilder();

				while (rset2.next()) {
					//File edgeCSV = new File("CSVs/outputEdge.csv");
					edgeSB.append(rset2.getString("edgeID")).append(",");
					edgeSB.append(rset2.getString("startNode")).append(",");
					edgeSB.append(rset2.getString("endNode")).append("\n");
				}

				FileWriter edgeWriter = new FileWriter("CSVs/outputEdge.csv");
				edgeWriter.write("edgeID, startNode, endNode\n");
				edgeWriter.write(String.valueOf(edgeSB));
				edgeWriter.close();
			}

		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public static void addRemovedPatientAppointmentHistory(int patientID) {

		String sqlQuery = "Select * From appointment Where patientid = ?";
		try (PreparedStatement prepStat = connection.prepareStatement(sqlQuery)) {
			prepStat.setInt(1, patientID);


			ResultSet rset = prepStat.executeQuery();

			StringBuilder strBuild = new StringBuilder();
			while (rset.next()) {

				//File nodeCSV = new File("src/main/resources/edu/wpi/cs3733/D21/teamE/output/outputNode.csv");
				strBuild.append(rset.getInt("appointmentID")).append(",");
				strBuild.append(rset.getString("patientID")).append(",");
				strBuild.append(rset.getString("doctorID")).append(",");
				strBuild.append(rset.getString("nodeID")).append(",");
				strBuild.append(rset.getTimestamp("startTime")).append(",");
				strBuild.append(rset.getTimestamp("endTime")).append("\n");
			}

			// create a file writer to write to files
			FileWriter nodeWriter = new FileWriter("CSVs/outputRemovedPatientAppointmentHistory.csv");
			nodeWriter.write("appointmentID, patientID, doctorID, nodeID, startTime, endTime\n");
			nodeWriter.write(String.valueOf(strBuild));
			nodeWriter.close();


			rset.close();


		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}


	}


}
