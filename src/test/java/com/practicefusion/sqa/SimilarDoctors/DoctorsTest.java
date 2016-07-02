package com.practicefusion.sqa.SimilarDoctors;

import java.sql.SQLException;
import java.util.ArrayList;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit test for Doctors class methods (using TestNG).
 */
public class DoctorsTest {
	private Doctors doctors;
	
	
	@BeforeMethod
	public void createInstanceOfDoctorsClass() {
		doctors = new Doctors();
	}

	@Test(enabled = true, groups="DoctorsTests")
	public void doctorIdInvalid() {
		ArrayList<String> doctorsList;
		try {
			doctorsList = doctors.getSimilarDoctors(-1);
			Assert.assertTrue(doctorsList.isEmpty());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test(enabled = true, groups="DoctorsTests")
	public void doctorIdDoesNotExist() {
		Doctors doctors = new Doctors();
		ArrayList<String> doctorsList;
		try {
			doctorsList = doctors.getSimilarDoctors(100000);
			Assert.assertTrue(doctorsList.isEmpty());
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	@Test(enabled = true, groups="DoctorsTests")
	public void doctorIdCorrect() {
		ArrayList<String> doctorsList;
		try {
			doctorsList = doctors.getSimilarDoctors(1);
			Assert.assertTrue(!doctorsList.isEmpty());
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	@Test(enabled = true, groups="DoctorsTests")
	public void noMoreThan100Doctors() {
		ArrayList<String> doctorsList;
		try {
			doctorsList = doctors.getSimilarDoctors(1);
			int number = doctorsList.size();
			Assert.assertTrue(doctorsList.size() <= 100);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test(enabled = true, groups="DoctorsTests")
		public void checkSpeciality() throws SQLException {
		//doctor with id = 3 is Cardiologist
		ArrayList<String> doctorsList;
		doctorsList = doctors.getSimilarDoctors(3);
		Boolean flag = true;
		for(int i = 0; i < doctorsList.size(); i++){
			if(!doctorsList.get(i).split(";")[3].equals("Cardiologist")) {
				flag = false;
				break;
			}
		}
		Assert.assertTrue(flag);
	} 
	

}
