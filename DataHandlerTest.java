/**
 * @author Gibran Akmal, 30094918
 * @author John Kvellestad, 10125207
 * @author Aashik Ilangovan, 30085993
 * @author Nikhil Naikar, 30039350
 * @version 2.0
 * @since 1.0 Unit Tests File
 */

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.File;
import java.util.ArrayList;
import java.sql.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * JUnit test file for the DataHandler program
 * Uses both DataHandler.java and IO.java
 * tests logic for found combinations and unfillable orders
 * test functionality for user paths such as declining an order
 * tests database insertion and deletion
 */

public class DataHandlerTest {
	
	/** TEST 1
	 * Take in user Input for 1) a furniture category, 2) the type and 3) amount
	 * of items requested
	 * Tests Constructor with three arguments for each Input,
	 * Returns the Input
	 */
	@Test
	public void testConstructorInputs() {
		String furnCat = "chair"; // Furniture Category
		String type = "Task"; // Type
		int amount = 1; // Number
		// Constructor with three arguments, respective to category, type and amount
		DataHandler d = new DataHandler(furnCat, type, amount); // INPUTS
		// Should return set Inputs
		assertEquals("ERROR: Your Category Input is not initialized", furnCat, d.getInputCategory());																																
		assertEquals("ERROR: Your Type Input is not initialized", type, d.getInputType()); 																		
		assertEquals("ERROR: Your Amount Input is not initialized", amount, d.getInputAmount()); 																	
	}

	/** TEST 2
	 * Tests a Successfully Fulfilled Order, and produces a formatted order in a .txt format
	 * Tests For The Output File being made, and its existence
	 * Inserts its own record into the database to maintain integrity
	 * Deletes its record at the end of the test
	 */
	@Test
	public void testTextFileMade() {
		String furnCat = "chair"; // Furniture Category
		String type = "Task"; // Type
		int amount = 1; // Number
		// Constructor with three arguments, respective to category, type and amount
		DataHandler d = new DataHandler(furnCat, type, amount); // INPUTS
		// Setting up connections
		Connection connect;
		Statement myStatement = null;
		// SELF NOTE: DEFAULTS
		final String DBURL = "jdbc:mysql://localhost/inventory"; // store the database url information
		final String USERNAME = "scm"; // store the user's account username
		final String PASSWORD = "ensf409"; // store the user's account password
		try {
			connect = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
			myStatement = connect.createStatement();
			// Adding sql commands 
			String sql = "INSERT INTO CHAIR (ID, Type, Legs, Arms, Seat, Cushion, Price, ManuID) ";
			sql += "VALUES ('T0000', 'Task', 'Y', 'Y', 'Y', 'Y', '-1' , '002')";
			// Executing Command
			myStatement.executeUpdate(sql);	
			//Finding Combination Method to find combination
			d.findCombo();
			// Deleting the used ID's
			d.deleteUsedIds();
			// makeFile Arguments correspond to Category, Type, Amount, Items Ordered, and Total Price
			d.makeFile();
			File temps = new File("orderform.txt"); // name of .txt file
			boolean exists = temps.exists(); //Checking its existence, once created
			assertEquals("ERROR: orderform.txt File does not exist", true, exists);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** TEST 3
	 * Inserts and Updates Inventory With Desired Output
	 * Tests if ID's and sum of costs for cheapest combination are correct
	 * Inserts its own records into the database to maintain integrity
	 * Deletes its record at the end of the test
	 */
	@Test
	public void testInsertUpdateInv() {
		String furnCat = "chair";
		String type = "Task";
		int items = 2;
		DataHandler d = new DataHandler(furnCat, type, items);
		// Setting up connections
		Connection connect;
		Statement myStatement = null;
		// SELF NOTE: DEFAULTS
		final String DBURL = "jdbc:mysql://localhost/inventory"; // store the database url information
		final String USERNAME ="scm"; // store the user's account username
		final String PASSWORD = "ensf409"; // store the user's account password
		ArrayList<ArrayList<String>> finalIDs = new ArrayList<ArrayList<String>>();
		try {
			connect = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
			myStatement = connect.createStatement();
			// build query to insert 3 entries to be picked up and deleted
			String sql = "INSERT INTO CHAIR (ID, Type, Legs, Arms, Seat, Cushion, Price, ManuID) ";
			sql+= "VALUES ('T0000', 'Task', 'Y', 'Y', 'N', 'N', '-1' , '002'), ";
			sql+= "('T1111', 'Task', 'N', 'N', 'Y', 'Y', '-1' , '002'), ";
			sql += "('T2222', 'Task', 'Y', 'Y', 'Y', 'Y', '-1' , '002')";
			// Executing Command
			myStatement.executeUpdate(sql);	
			//Finding Combination Method to find combination
			d.findCombo();
			// Finding Final Ids of request 
			finalIDs = d.getFinalIds();
			// Deleting the used ID's
			d.deleteUsedIds();
			assertEquals("ERROR: Total is incorrect", -3, d.sumOfCosts()); // Tests Sum of costs
			assertEquals("ERROR: Cheapest Combination not found", "T0000", finalIDs.get(0).get(0)); // Test if right ID picked
			assertEquals("ERROR: Cheapest Combination not found", "T1111", finalIDs.get(0).get(1)); // Test if right ID picked
			assertEquals("ERROR: Cheapest Combination not found", "T2222", finalIDs.get(1).get(0)); // Test if right ID picked
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** TEST 4
	 * Inserts and Updates Inventory With a Desired Output
	 * Test if the cheapest combination ID is deleted once used
	 */
	@Test
	public void testDeleteUpdateInv() {
		String furnCat = "chair";
		String type = "Task";
		int items = 1;
		DataHandler d = new DataHandler(furnCat, type, items);
		// Setting up connections
		Connection connect;
		Statement myStatement = null;
		// SELF NOTE: DEFAULTS
		final String DBURL = "jdbc:mysql://localhost/inventory"; // store the database url information
		final String USERNAME = "scm"; // store the user's account username
		final String PASSWORD = "ensf409"; // store the user's account password
		ResultSet rs = null;
		try {
			connect = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
			myStatement = connect.createStatement();
			// builds query to insert cheapest combination for testing
			String sql = "INSERT INTO CHAIR (ID, Type, Legs, Arms, Seat, Cushion, Price, ManuID) ";
			sql += "VALUES ('A0000', 'Task', 'Y', 'Y', 'Y', 'Y', '-1' , '002')";
			// Executes Insertion/Update
			myStatement.executeUpdate(sql);
			d.findCombo(); //Find's combination
			d.deleteUsedIds(); // DELETES USED ID'S
			// Searching for Inserted ID (Now deleted)
			sql = "SELECT * from chair where ID = 'A0000'";
			rs = myStatement.executeQuery(sql);
			// Test To Find Deleted ID
			assertEquals("ERROR: Did not Delete Used ID's", false, rs.next());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** TEST 5
	 * Test if recommendations are outputted when a request is unfulfilled
	 */
	@Test
	public void testUnFullfilled() {
		String furnCat = "chair";
		String type = "Task";
		int items = 100; // WAY TOO MANY ITEMS ORDERED
		// Putting Inputs into Constructor To be initialized
		DataHandler d = new DataHandler(furnCat, type, items);
		// Tries to find a possible combination (which it cannot as there are not enough items in inventory)
		boolean itemoverload = d.findCombo(); // should result in false
		assertEquals("ERROR: Order must be unfillable", false, itemoverload);
	}

	/** Test 6
	 * Testing user's "no" input is being read correctly by the program
	 */
	@Test
	public void testUnhappyUser() {
		String furnCat = "chair";
		String type = "Task";
		int items = 1;
		DataHandler d = new DataHandler(furnCat, type, items);
		// New IO Object
		IO i = new IO();
		String input = "no"; // simulates consumer declining the order after combination is found
		InputStream in = new ByteArrayInputStream(input.getBytes());
		System.setIn(in);
		boolean check = i.checkWithUser(); // Should return false
		//Tests check is false after user input is unhappy with their offer and inputs "no"
		assertEquals("ERROR: Error in receiving a \"no\" from input", false, check);
	}

	/** TEST 7
	 * Testing user's "yes" input is being read correctly by the program
	 */
	@Test
	public void testHappyUser() {
		String furnCat = "chair";
		String type = "Task";
		int items = 1;
		DataHandler d = new DataHandler(furnCat, type, items);
		// New IO Object
		IO i = new IO();
		String input = "yes"; // simulates consumer accepting the order after combination is found
		InputStream in = new ByteArrayInputStream(input.getBytes());
		System.setIn(in);
		boolean check = i.checkWithUser(); // Should return True
		// Tests if User is happy with the offer, will return true if they are
		assertEquals("ERROR: Error in receiving a \"yes\" from input", true, check);
	}
}
