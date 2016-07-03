package com.practicefusion.sqa.SimilarDoctors;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Doctors class will read and populate a SQL database, and based on the users input, 
 * return a listing of the particular doctors information, and other doctors similar in skill, location, or rating
 * <p>
 *	sv - temporary array for populate data base
 *	doctors - result: similar doctors list
 *	vsSplitBy -defining delimiter using in csv file
 *	chosenDoctorId - chosen doctor's id 
 *	maxDoctorId - current qty doctors in data base
 * <p>
 * Creating data base with fields:
 *	id,	fName,	lName,	specialty,	city,	reviewScore
 * Populating data base using csv file
 * Choosing doctor by id and printing out doctor's info
 * Finding similar doctors using 4 rulers:
 * 	1. getting all doctors within specified city, of specified specialty and same or better rating
 * 	2. getting all doctors of specified specialty and same or better rating but not in the specified city
 * 	3. getting all doctors of specified specialty and in the same city but worse rating
 * 	4. getting all doctors of specified specialty but not in the same city and with the worse rating
 * All chosen doctors putting in list no longer than 100 doctors and printing out
 * 
 * @author      Tony 
 * @version     1.0.0
 * @since       1.0
 *
 */

public class Doctors {
	
	private static String[] csv;	//temporary array for populate data base
	private static ArrayList<String> doctors = new ArrayList<String>();	//result: similar doctors list
	private static String cvsSplitBy = ";";	//defining delimiter using in csv file
	private static int chosenDoctorId;	//chosen doctor's id 
	private static int maxDoctorId;		//current qty doctors in data base

  public static void main(String[] args) throws ClassNotFoundException, FileNotFoundException, IOException, SQLException {
	  Doctors doctors = new Doctors();
	  doctors.dataBase();
	  chosenDoctorId = doctors.chooseDoctor();
	  doctors.printChoosenDoctor();
	  doctors.printDoctors(doctors.getSimilarDoctors(chosenDoctorId));
  }//main
  
 /**
   * choosing doctor by id
 * @return chosen doctor id
 */
  public int chooseDoctor() {
	  int choosenDoctorId;

	  System.out.print("Enter doctor's id (valid values are from 1 to " + maxDoctorId + "): ");
	  //@SuppressWarnings("resource")
	  Scanner scanner = new Scanner(System.in);

	  if (scanner.hasNextInt()) {
		  choosenDoctorId = scanner.nextInt();
	  } else {
		  scanner.next();   // get the non integer from scanner
	      choosenDoctorId = 0;
	  }

	  // If the doctor's id input is below 1 or greater than maxDoctorId, prompt for another value
	  while (choosenDoctorId < 1 || choosenDoctorId > maxDoctorId) {
	      System.out.print("Invalid value! Enter doctor's id (valid values are from 1 to " + maxDoctorId + "): ");
	      if (scanner.hasNextInt()) {
	    	  choosenDoctorId = scanner.nextInt();
	       } else {
	         String dummy = scanner.next();
	         choosenDoctorId = 0;
	      }
	  }
	  scanner.close();
	  return choosenDoctorId;
  }//chooseDoctor
  
  /**
 * creating data base
 */
  public void dataBase() throws ClassNotFoundException, FileNotFoundException, IOException {
	  
	System.out.print("Please wait, data base is creating...");
	//populate data base from csv file  
	populateDb();  
    // load the SQLite-JDBC driver using the current class loader
    Class.forName("org.sqlite.JDBC");

    Connection connection = null;
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:doctors.db");
      Statement statement = connection.createStatement();
      statement.setQueryTimeout(40);  // set timeout to 40 sec.

      statement.executeUpdate("drop table if exists person");
      statement.executeUpdate("create table person ("
      		+ "  id integer"
      		+ ", fName string"
      		+ ", lName string"
      		+ ", specialty string"
      		+ ", city string"
      		+ ", reviewScore float"
      		+ ")");

      String query = null;
      for(int i = 0; i < doctors.size(); i++) {
    	  csv = doctors.get(i).split(cvsSplitBy);
    	  query = "insert into person values("+csv[0]+",'"+csv[1]+"','"+csv[2]+"','"+csv[3]+"','"+csv[4]+"',"+csv[5]+")";
          statement.executeUpdate(query);
      }
      
    }//try
    catch(SQLException e) {
      // if the error message is "out of memory", 
      // it probably means no database file is found
      System.err.println(e.getMessage());
    }//catch
    finally {
      try {
        if(connection != null)
          connection.close();
      }//try
      catch(SQLException e) {
        // connection close failed.
        System.err.println(e);
      }//catch
    }//finally
    
    //determine max number of doctors in data base
    maxDoctorId = doctors.size();
	System.out.println(" Done");
  }//dataBase
  
	/**
	 * populate data base using csv file 
	 */
	public void populateDb() throws FileNotFoundException, IOException {		
		String csvFile = "./src/main/resources/doctors_list.csv";
		BufferedReader br = null;
		String line = null;

		br = new BufferedReader(new FileReader(csvFile)); 
		while ((line = br.readLine()) != null) {
			doctors.add(line);
		}
		
		br.close();	
	}//populateDb
	
	/**
	 * @param doctorId (int number less than qty doctors in data base )
	 * @return ArrayList with similar doctors
	 * @throws SQLException 
	 */
	public ArrayList<String> getSimilarDoctors(int doctorId) {
		ArrayList<String> result = new ArrayList<String>();
		Connection connection = null;
	    
			try {
				connection = DriverManager.getConnection("jdbc:sqlite:doctors.db");
			} catch (SQLException e1) {
				//e1.printStackTrace();
			}
			
	    	Statement statement;
			try {
				statement = connection.createStatement();
		
		    	statement.setQueryTimeout(40);  // set timeout to 40 sec.      
		    	
		    	ResultSet rs = statement.executeQuery("select * from person where id == "+doctorId);
		    	String specialty = rs.getString("specialty");
		    	String city = rs.getString("city");
		    	float score = rs.getFloat("reviewScore");
		    	
		    	// ------------- RULE 1 ------------------
		    	//getting all doctors within specified city, of specified specialty and same or better rating
		    	rs = statement.executeQuery("select * from person where "
		      		+ "specialty == '"+specialty+"' AND "
		      		+ "city == '"+city+"' AND "
		      		+ "reviewScore >= "+score+" ORDER BY reviewScore DESC LIMIT 100");
		      
		    	//adding all found doctors to the resulting list
		    	while(rs.next()) {
		    	  result.add(rs.getInt("id") + ";" +
		 	        	 (rs.getString("fName") + ";"+ 
		 	        	rs.getString("lName")) + ";" +
		 	         	rs.getString("specialty") + ";" +
		 	         	rs.getString("city") + ";" +
		 	         	rs.getFloat("reviewScore"));
		    	}
		    	
		    	// ------------- RULE 2 ------------------
		    	//getting all doctors of specified specialty and same or better rating but not in the specified city
		    	if(result.size() < 100) {
		    		rs = statement.executeQuery("select * from person where "
		          		+ "specialty == '"+specialty+"' AND "
		          		+ "city != '"+city+"' AND "
		          		+ "reviewScore >= "+score+" ORDER BY reviewScore DESC LIMIT 100");
		    		//adding all found doctors to the resulting list (unless the list has reached 100)
		    		while(rs.next()) {
		        	  result.add(rs.getInt("id")+";"+
		     	        	 (rs.getString("fName") +";"+ 
		     	        	rs.getString("lName")) +";"+
		     	         	rs.getString("specialty") +";"+
		     	         	rs.getString("city") +";"+
		     	         	rs.getFloat("reviewScore"));
		        	  if(result.size() >= 100) {
		        		  break;
		        	  }
		        	}
		    	}	      
		      
		    	// ------------- RULE 3 ------------------
		    	//getting all doctors of specified specialty and in the same city but worse rating
		    	if(result.size() < 100) {
		    		rs = statement.executeQuery("select * from person where "
		    		+ "specialty == '"+specialty+"' AND "
		      		+ "city == '"+city+"' AND "
		      		+ "reviewScore < "+score+" ORDER BY reviewScore DESC LIMIT 100");
		     
		    		//adding all found doctors to the resulting list (unless the list has reached 100)
				    while(rs.next()) {
				    	result.add(
				    		rs.getInt("id")+";"+
				    		(rs.getString("fName") +";"+ 
					        rs.getString("lName")) +";"+
					        rs.getString("specialty") +";"+
					        rs.getString("city") +";"+
					        rs.getFloat("reviewScore"));
					   	if(result.size() >= 100) {
					   		break;
						}
				    }
		    	}      
		   
		    	// ------------- RULE 4 ------------------
		    	//getting all doctors of specified specialty but not in the same city and with the worse rating
		    	if(result.size() < 100) {
		    		rs = statement.executeQuery("select * from person where "
			        		+ "specialty == '"+specialty+"' AND "
			        		+ "city != '"+city+"' AND "
			        		+ "reviewScore < "+score+" ORDER BY reviewScore DESC LIMIT 100");
			    //adding all found doctors to the resulting list (unless the list has reached 100)
			    while(rs.next()) {
			    	result.add(rs.getInt("id") + ";" +
			 	        	 (rs.getString("fName") + ";" + 
			 	        	rs.getString("lName")) + ";" +
			 	         	rs.getString("specialty") + ";" +
			 	         	rs.getString("city") + ";" +
			 	         	rs.getFloat("reviewScore"));
			    	 if(result.size() >= 100) {
			    		 break;
			    	 }
			    }//while
		    	}//if
		    	connection.close();  
			} catch (SQLException e) {}
			return result;
	}//getSimilarDoctors
	
	/**
	 * print out Similar Doctors (max 100)
	 * @param ArrayList doctorList contains Similar Doctors
	 */
	public void printDoctors(ArrayList<String> doctorList) {
		String columns[];
		System.out.println("----------------------Similar doctors: -----------------------------");
		for(int i = 0; i < doctorList.size(); i++) {
			columns = doctorList.get(i).split(cvsSplitBy);
			System.out.format( 
				"%4d %-18s %-18s %-19s %.1f %s",
	    		Integer.parseInt(columns[0]),
	    		columns[1] + 
	    	    " " + 
	    	    columns[2],
	    	    columns[3],
	    	    columns[4],
	    	    Float.parseFloat(columns[5]),
	    	    '\n'
			);//format
		}//for
	}//printDoctors
	
	/**
	 * print out one chosen doctor
	 * @throws SQLException 
	 */
	public void printChoosenDoctor() throws SQLException {
		Connection connection = null;
		
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:doctors.db");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	    Statement statement = connection.createStatement();
	    statement.setQueryTimeout(40);  // set timeout to 40 sec.
	      
	    ResultSet rs = statement.executeQuery("select * from person where id == " + chosenDoctorId);

		System.out.println("---------------------- Chosen doctor: ------------------------------");

	    //printing out header
		System.out.format( 
				"%4s %-18s %-18s %-19s %3s %s",
	    		"ID",
	    		"FIRST & LAST NAME",
	    	    "SPECIALITY",
	    	    "CITY",
	    	    "SCORE",
	    	    '\n');
	    
	    //printing out chosen doctor info
		System.out.format( 
				"%4d %-18s %-18s %-19s %.1f %s",
	    		Integer.parseInt(rs.getString("id")),
	    		rs.getString("fName") + 
	    	    " " + 
	    	    rs.getString("lName"),
	    	    rs.getString("specialty"),
	    	    rs.getString("city"),
	    	    Float.parseFloat(rs.getString("reviewScore")),
	    	    '\n');
		
		connection.close();	
	}//printChoosenDoctor
	
}//class
