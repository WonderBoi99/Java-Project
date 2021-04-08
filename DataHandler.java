/**
 *@author Gibran Akmal, 30094918
 *@author John Kvellestad, 10125207
 *@author Aashik Ilangovan, 30085993
 *@author Nikhil Naikar, 30039350

 *@version 12
 *@since 11
*/
package edu.ucalgary.ensf409;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.lang.StringBuilder;
import java.lang.Integer;

/**
DataHandler is the main functionality of the program apart from user input
It handles interaction with the database and calculation of recommendations
*/
public class DataHandler {
	
	public final String DBURL = "jdbc:mysql://localhost/inventory"; //store the database url information
    public final String USERNAME = "scm";	//store the user's account username
    public final String PASSWORD =  "ensf409";	//store the user's account password

    private Connection connect; //connection
    private ResultSet results;	//results of query

    private String inputCategory; //category from user
    private String inputType; //type from user
    private int inputAmount; //amount from user

	private int checkList = 0; //how many columns the current list has
	private int totalCost = 0; //total cost of current combination
	private int count = 0; //count of options for a specified column
	private String a1[] = new String[2]; //data column 1
	private String a2[] = new String[2]; //data column 2
	private String a3[] = new String[2]; //data column 3
	private String a4[] = new String[2]; //data column 4
	private	ArrayList <String> newID = new ArrayList <String>(); //stores IDs of new valid combination
	private ArrayList<ArrayList<String>> finalIds = new ArrayList<ArrayList<String>>();
	//stores the specified number of combinations of IDs
	private ArrayList<Integer> finalCost = new ArrayList<Integer>();
	//stores the total cost of each combination in finalIds
	private ArrayList<String> usedIds = new ArrayList<String>();
	//stores all the Ids that have been used
	
	/**
	 * When code is run
	 * Program will ask for user input of category, type and amount separately
	 * Looks for the cheapest combination(s) if avaiable
	 * If no combination found, recommendations of manufacturers will be displayed
	 * If yes combination found, will show and ask user if they are happy with the combination
	 * If user says yes, then a orderform.txt file will be made and the UsedIds will be deleted from the database
	 * If user says no, then recommendations of manufacturers will be displayed
	 */
    public static void main(String[] args){
		IO test = new IO();
		test.start();
	}

    /**
	 * Constructor for DataHandler Class
	 * initializes data members 
	 * @param DBURL is the specific database URL that the user inputs that is for the target database
	 * @param USERNAME is the username to access the database with
	 * @param PASSWORD is the password corresponding to that username
	 */	
    public DataHandler(String category, String type, int amount){
    	//these data members initalized based on where the user's database is located
    	initializeConnection();
    	inputCategory = category;
    	inputType = type;
    	inputAmount = amount;
    }

	public ArrayList<ArrayList<String>> getFinalIds(){
		//returns finalIds
		return finalIds;
	}

	public ArrayList<Integer> getFinalCost(){
		//returns finalCost
		return finalCost;
	}
    
	//returns the sum of all the costs 
	public int sumOfCosts(){
		int sum = 0;
		for(int i = 0; i < finalCost.size(); i++){
			sum = sum + finalCost.get(i);
		}
		return sum;
	}

    /**
	 * initializes connection with the database
	 */	
    public void initializeConnection(){
    	try{
    		connect = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
    	}
    	catch(SQLException e){
    		e.printStackTrace();
    	}
    }
    
    /**
     * closes results and connect 
	 */
    public void close(){
        try{
            results.close();
            connect.close();
        } 
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
	 * determines what attributes the item has based on its category
	 */
	private void makeChecklist(){
		if(inputCategory.equals("chair")){
			a1[0] = "Legs";
			a2[0] = "Arms";
			a3[0] = "Seat";
			a4[0] = "Cushion";
			checkList = 4;
		}
		else if(inputCategory.equals("desk")){
			a1[0] = "Legs";
			a2[0] = "Top";
			a3[0] = "Drawer";
			checkList = 3;
		}
		else if(inputCategory.equals("filing")){
			a1[0] = "Rails";
			a2[0] = "Drawers";
			a3[0] = "Cabinet";
			checkList = 3;
		}
		else if(inputCategory.equals("lamp")){
			a1[0] = "Base";
			a2[0] = "Bulb";
			checkList = 2;
		}
	}

	/**
	 * finds the valid combinations for the user's order
	 */
    public boolean findCombo(){
		boolean orderFilled = false;
		makeChecklist();
    	try{
			Statement myStatement = connect.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			//statement for getting the number of rows in table
    		results = myStatement.executeQuery("SELECT count(ID) FROM " + inputCategory);
			results.next();
			int numberOfRows = results.getInt(1);
			//statement for getting and putting the data from user picked table in ascending order depending on Price
    		results = myStatement.executeQuery("SELECT * FROM " + inputCategory + " ORDER BY Price ASC");
			for(int start = 0; start < numberOfRows;start++){
				int counting = 0;
				boolean combMade = false;
				//for skipping rows
				while(counting < start){
					results.next();
					counting++;
				}
    			combMade = lookForOneCombination();
				if(combMade){ //if valid and not already used
					mainStrategy();
					updateUsedIds();
				}
				resetVariables();
			}
    		myStatement.close(); //close statement very important
			if(finalIds.size() == inputAmount){
				orderFilled = true;
			}
    	}
    	catch(SQLException e){
    		e.printStackTrace();    		
    	}	
    	return orderFilled;
    } 

	/**
	 * determines if the most recent valid combination should be stored
	 */
	private void mainStrategy(){
		if(finalCost.size() < inputAmount){ //if not enough combinations made, then always store
			finalCost.add(totalCost);
			finalIds.add(new ArrayList <String>(newID));
		}
		else if(totalCost < Collections.max(finalCost)){ 
			//if new combination cheaper than current most expensive combination
			int index = finalCost.indexOf(Collections.max(finalCost));
			//get index of most expensive combination
			finalCost.remove(index);
			//remove index from cost
			finalIds.remove(index);
			//remove index from finalIds
			finalCost.add(totalCost);
			//add new combination cost
			finalIds.add(new ArrayList <String>(newID));
			//add new combination Ids
		}
	}

	/**
	 * finds a single valid combination
	 */
	private boolean lookForOneCombination(){
		boolean tmp = false;
		try{
			while (results.next()){
				String temp = results.getString("Type");
				if(temp.equals(inputType)){
					if(checkList == 4){	
						tmp = checklistForChair();
					}
					else if(checkList == 3){
						tmp = checklistForDeskOrFiling();
					}
					else if(checkList == 2){
						tmp = checklistForLamp();
					}	
				}
				count = 0;     
			}
		}
		catch(SQLException e){
    		e.printStackTrace();    		
    	}
		return tmp;
	}

	/**
	 * updates usedIds with all the Ids that have be selected as a temp combination from finalIds
	 */
	private void updateUsedIds(){
		usedIds.clear();
		for(int o = 0; o < finalIds.size(); o++){
			ArrayList<String> tmp = finalIds.get(o);
			for(int i = 0; i < tmp.size(); i++){
				usedIds.add(tmp.get(i));
			}
		}
	}

	/**
	 * resets attributes to base state
	 */
	private void resetVariables(){
		a1[1] = null;
		a2[1] = null;
		a3[1] = null;
		a4[1] = null;
		totalCost = 0;
		newID.clear();
		try{
			results.beforeFirst();
		}
		catch(SQLException e){
    		e.printStackTrace();    		
    	}
	}

	/**
	 * sets up a validity check for each column for the chair type
	 */
	private boolean checklistForChair(){
		boolean tmp = false;
		checklist(a1);
		checklist(a2);
		checklist(a3);
		checklist(a4);
		if(a1[1] != null && a2[1] != null && a3[1] != null && a4[1] != null){
			tmp = true;
		}
		else{
			tmp = false;
		}
		return tmp;
	}

	/**
	 * sets up a validity check for each column for the desk or filing types
	 */
	private boolean checklistForDeskOrFiling(){
		boolean tmp = false;
		checklist(a1);
		checklist(a2);
		checklist(a3);
		if(a1[1] != null && a2[1] != null && a3[1] != null){
			tmp = true;
		}
		else{
			tmp = false;
		}
		return tmp;
	}

	/**
	 * sets up a validity check for each column for the lamp type
	 */
	private boolean checklistForLamp(){
		boolean tmp = false;
		checklist(a1);
		checklist(a2);
		if(a1[1] != null && a2[1] != null){
			tmp = true;
		}
		else{
			tmp = false;
		}
		return tmp;
	}

	/**
     * UsedIds will be deleted from the database 
     */
	public void deleteUsedIds(){
		try{
			String qe = "DELETE FROM " + inputCategory + " WHERE ID = ?";
			PreparedStatement myFinalStatment = connect.prepareStatement(qe);
			for(int o = 0; o < finalIds.size(); o++){
				ArrayList<String> tmp = finalIds.get(o);
				for(int i = 0; i < tmp.size(); i++){
					myFinalStatment.setString(1, tmp.get(i));
					myFinalStatment.executeUpdate();	
				}
			}
			myFinalStatment.close();
		}
		catch(SQLException e){
    		e.printStackTrace();    		
    	}
	}

	/**
	 * performs a validity check on a column in one row of results
	 */	
	private void checklist(String[] x){
		try{
			String id = results.getString("ID");
			if(results.getString(x[0]).equals("Y") && x[1] == null && !usedIds.contains(id)){
				x[1] = "Y";
				//only want to add cost to totalCost once for each row
				if(count == 0){
					totalCost = totalCost + results.getInt("Price");
					//recording the IDs
					newID.add(results.getString("ID"));
					count++;
				}
			}
		}
		catch(SQLException e){
			e.printStackTrace();    		
		}
	}

    /**
	 * prints recommendations for manufacturers if not able to fill order
	 */
	public void printRecommedations(){
		try{
			StringBuilder suggestions = new StringBuilder();
			ArrayList<String> manuIDs = new ArrayList<String>();
			Statement myStatement;
			PreparedStatement mySecondStatment;
			String qe;
			//making prepared statement for getting all ManuID related to type
			qe = "SELECT DISTINCT ManuID FROM "+ inputCategory + " WHERE Type = ?";
			mySecondStatment = connect.prepareStatement(qe);
			mySecondStatment.setString(1, inputType);
			results = mySecondStatment.executeQuery();
			//storing the ManuID in manuIDs for later use
			while(results.next()){
				manuIDs.add(results.getString("ManuID"));
			}
			//making statement for getting data from manufacturer table
			myStatement = connect.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    		results = myStatement.executeQuery("SELECT * FROM manufacturer");
			//storing the names for the manuIDs collected earlier
			while(results.next()){
				if(manuIDs.contains(results.getString("ManuID"))){
					suggestions = suggestions.append(results.getString("Name") + ", ");
				}
			}
			//formatting stuff
			if(suggestions.length() == 0){
				printAllManufacturers();
			}
			else{
				suggestions.setLength(suggestions.length()-2);
				System.out.println("Order cannot be fulfilled. Suggested manufacturer(s) are: ");
				System.out.println(suggestions.toString());
			}
			myStatement.close();
			mySecondStatment.close();
		}
		catch(SQLException e){
			e.printStackTrace();    		
		}
	}

	/**
	 * Prints out all of the manufacturers names avaiable
	 */
	private void printAllManufacturers(){
		try{
			StringBuilder suggestions = new StringBuilder();
			Statement myStat = connect.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			results = myStat.executeQuery("SELECT * FROM manufacturer");
			while(results.next()){
				suggestions = suggestions.append(results.getString("Name") + ", ");
			}
			suggestions.setLength(suggestions.length()-2);
			System.out.println("Order cannot be fulfilled. Suggested manufacturer(s) are: ");
			System.out.println(suggestions.toString());
			myStat.close();
		}
		catch(SQLException e){
			e.printStackTrace();    		
		}
	}
}
