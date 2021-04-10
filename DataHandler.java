/**
 *@author Gibran Akmal, 30094918
 *@author John Kvellestad, 10125207
 *@author Aashik Ilangovan, 30085993
 *@author Nikhil Naikar, 30039350

 *@version 12
 *@since 11
*/
// package edu.ucalgary.ensf409;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.lang.StringBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Integer;

/**
DataHandler is the main functionality of the program apart from user input
It handles interaction with the database and calculation of recommendations
*/
public class DataHandler {
	
	public final String DBURL = "jdbc:mysql://localhost/inventory"; //store the database url information
    public final String USERNAME = "naikar";	//store the user's account username
	//scm
    public final String PASSWORD =  "ensf409";	//store the user's account password
	//ensf409
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
	private ArrayList<ArrayList<String>> allPossibleCombinations = new ArrayList<ArrayList<String>>();
	//stores all possible valid combinations
	private ArrayList<Integer> allCosts = new ArrayList<Integer>();
	//stores all possible valid costs
	private int numberOfRows;
	//private int whereAreWe;

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
    
    public String getInputCategory() {
    	// returns input Category
    	return inputCategory;
    }
    
    public String getInputType() {
    	// returns input Type
    	return inputType;
    }
    
    public int getInputAmount() {
    	// returns input Type
    	return inputAmount;
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
			numberOfRows = results.getInt(1);
			//statement for getting and putting the data from user picked table in ascending order depending on Price
    		results = myStatement.executeQuery("SELECT * FROM " + inputCategory + " ORDER BY Price ASC");
			
			boolean combMade = false;
			
    		combMade = lookForOneCombination();
			
    		myStatement.close(); //close statement very important
			//System.out.println(allPossibleCombinations);
			while (allPossibleCombinations.size() > 0) {
				int minIndex = allCosts.indexOf(Collections.min(allCosts));
				boolean duplicate = false;
				for (String id : allPossibleCombinations.get(minIndex)) {
					for (ArrayList<String> comboFinal : finalIds) {
						if (comboFinal.contains(id)) {
							duplicate = true;
						}
					}
				}
				if (!duplicate) {
					finalIds.add(new ArrayList <String>(allPossibleCombinations.get(minIndex)));
					finalCost.add(Collections.min(allCosts));
				}
				allPossibleCombinations.remove(allPossibleCombinations.get(minIndex));
				allCosts.remove(minIndex);
				if (finalIds.size() == inputAmount) {
					return true;
				}
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
		if (allPossibleCombinations.size() == 0) {
			allPossibleCombinations.add(new ArrayList <String>(newID));
			allCosts.add(totalCost);
		}
		else {
			if (!allPossibleCombinations.contains(newID)) {
					allPossibleCombinations.add(new ArrayList <String>(newID));
					allCosts.add(totalCost);
			}
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
						checklistForChair();
					}
					else if(checkList == 3){
						checklistForDeskOrFiling();
					}
					else if(checkList == 2){
						checklistForLamp();
					}	
				}
				count = 0;     
			}
		}
		catch(SQLException e){
    		e.printStackTrace();    		
    	}
		if(allPossibleCombinations.size() >= inputAmount){
			return true;
		}
		
		return false;
		
		
	}

	/**
	 * resets attributes to base state
	 */
	private void resetVariables(){
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
	private void checklistForChair(){
		//ArrayList<String> tempIds = new ArrayList<String>();
		//ArrayList<String> tempCost = new ArrayList<String>();
		try{
			count = 0;
			boolean tmp = false;
			boolean combMade = false;
			int startingPoint;
			if(a1[1] == null && a2[1] == null && a3[1] == null && a4[1] == null){
				startingPoint = results.getRow();
				checklist(a1);
				checklist(a2);
				checklist(a3);
				checklist(a4);
				results.beforeFirst();
				for(int index = 0; index < numberOfRows; index++){
					int counting = 0;
					//for skipping rows
					while(counting < index){
						results.next();
						counting++;
					}
					while (results.next()){
						String temp = results.getString("Type");
						if(temp.equals(inputType)){
							if(checkList == 4){	
								
								checklistForChair();
							}
							else if(checkList == 3){
								checklistForDeskOrFiling();
							}
							else if(checkList == 2){
								checklistForLamp();
							}	
						}
						count = 0;  
						if(a1[1] != null && a2[1] != null && a3[1] != null && a4[1] != null){
							combMade = true;
							mainStrategy(); 
							break;
						}   
					}
					newID.clear();
					totalCost = 0;
					a1[1] = null;
					a2[1] = null;
					a3[1] = null;
					a4[1] = null;
					results.absolute(startingPoint);
					checklist(a1);
					checklist(a2);
					checklist(a3);
					checklist(a4);
					results.beforeFirst();
					combMade = false;
					
				}
				newID.clear();
				totalCost = 0;
				a1[1] = null;
				a2[1] = null;
				a3[1] = null;
				a4[1] = null;
				results.absolute(startingPoint);
			}
			else{
				checklist(a1);
				checklist(a2);
				checklist(a3);
				checklist(a4);
				count = 0;
			}
		}
		catch(SQLException e){
    		e.printStackTrace();    		
    	}
	}

	/**
	 * sets up a validity check for each column for the desk or filing types
	 */
	private void checklistForDeskOrFiling(){
		try{
			count = 0;
			boolean tmp = false;
			boolean combMade = false;
			int startingPoint;
			if(a1[1] == null && a2[1] == null && a3[1] == null){
				startingPoint = results.getRow();
				checklist(a1);
				checklist(a2);
				checklist(a3);
				results.beforeFirst();
				for(int index = 0; index < numberOfRows; index++){
					int counting = 0;
					//for skipping rows
					while(counting < index){
						results.next();
						counting++;
					}
					while (results.next()){
						String temp = results.getString("Type");
						if(temp.equals(inputType)){
							if(checkList == 4){	
								checklistForChair();
							}
							else if(checkList == 3){
								checklistForDeskOrFiling();
							}
							else if(checkList == 2){
								checklistForLamp();
							}	
						}
						count = 0;  
						if(a1[1] != null && a2[1] != null && a3[1] != null){
							combMade = true;
							mainStrategy(); 
							break;
						}   
					}
					newID.clear();
					totalCost = 0;
					a1[1] = null;
					a2[1] = null;
					a3[1] = null;
					results.absolute(startingPoint);
					checklist(a1);
					checklist(a2);
					checklist(a3);
					results.beforeFirst();
					combMade = false;
					
				}
				newID.clear();
				totalCost = 0;
				a1[1] = null;
				a2[1] = null;
				a3[1] = null;
				results.absolute(startingPoint);
			}
			else{
				checklist(a1);
				checklist(a2);
				checklist(a3);
				count = 0;
			}
		}
		catch(SQLException e){
    		e.printStackTrace();    		
    	}
	}

	/**
	 * sets up a validity check for each column for the lamp type
	 */
	private void checklistForLamp(){
		try{
			count = 0;
			boolean tmp = false;
			boolean combMade = false;
			int startingPoint;
			if(a1[1] == null && a2[1] == null){
				startingPoint = results.getRow();
				checklist(a1);
				checklist(a2);
				results.beforeFirst();
				for(int index = 0; index < numberOfRows; index++){
					int counting = 0;
					//for skipping rows
					while(counting < index){
						results.next();
						counting++;
					}
					while (results.next()){
						String temp = results.getString("Type");
						if(temp.equals(inputType)){
							if(checkList == 4){	
								checklistForChair();
							}
							else if(checkList == 3){
								checklistForDeskOrFiling();
							}
							else if(checkList == 2){
								checklistForLamp();
							}	
						}
						count = 0;  
						if(a1[1] != null && a2[1] != null){
							combMade = true;
							mainStrategy(); 
							break;
						}   
					}
					newID.clear();
					totalCost = 0;
					a1[1] = null;
					a2[1] = null;
					results.absolute(startingPoint);
					checklist(a1);
					checklist(a2);
					results.beforeFirst();
					combMade = false;
					
				}
				newID.clear();
				totalCost = 0;
				a1[1] = null;
				a2[1] = null;
				results.absolute(startingPoint);
			}
			else{
				checklist(a1);
				checklist(a2);
				count = 0;
			}
		}
		catch(SQLException e){
    		e.printStackTrace();    		
    	}
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
			if(results.getString(x[0]).equals("Y") && x[1] == null){ //&& !usedIds.contains(id)){
				x[1] = "Y";
				//only want to add cost to totalCost once for each row
				if(count == 0){
					totalCost = totalCost + results.getInt("Price");
					//recording the IDs
					// System.out.println("Adding to newID: " + results.getString("ID"));
					newID.add(results.getString("ID"));
					// System.out.println("newID: " + newID);
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
				System.out.println("No manufacturers provide this item");
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
	 * Makes a orderform.txt file with some information  
	 * Following the specified format of the given example
	 */	
	public void makeFile(){
		try{   
            FileWriter output;
			//the output FileWriter will output to a text file called "output.txt"			
			output = new FileWriter("orderform.txt", false); //the second parameter of false tells it to overwrite an existing file of that name if it exists
			output.write("Furniture Order Form\n\n");
			output.write("Faculty Name: \n");
			output.write("Contact: \n");
			output.write("Date: \n\n");
			output.write("Original Request: "+inputType+" "+inputCategory+", "+inputAmount+"\n\n");
			output.write("Items Ordered:\n");
            for(int o = 0; o < finalIds.size(); o++){
                ArrayList<String> tmp = finalIds.get(o);
                for(int i = 0; i < tmp.size(); i++){
                    output.write(tmp.get(i)+"\n");
                }
            }
			output.write("\nTotal Price: $" + sumOfCosts());
            output.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}	
}
