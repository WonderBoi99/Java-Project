/**
 *@author Gibran Akmal, 30094918
 *@author John Kvellestad, 10125207
 *@author Aashik Ilangovan, 30085993
 *@author Nikhil Naikar, 30039350

 *@version 10
 *@since 9.0
*/

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.lang.StringBuilder;
import java.lang.Integer;

//REMEMBER TO CHANGE LOGIN CREDENTIALS FOR DATABASE
public class DataHandler {
	public final String DBURL = "jdbc:mysql://localhost/inventory"; 
    public final String USERNAME = "naikar";	
    public final String PASSWORD =  "ensf409";	

    private Connection connect; 
    private ResultSet results;	

    private String inputCategory;
    private String inputType;
    private int inputAmount;

	private int checkList = 0;
	private int totalCost = 0;
	private int count = 0;
	private String a1[] = new String[2];
	private String a2[] = new String[2];
	private String a3[] = new String[2];
	private String a4[] = new String[2];
	private	ArrayList <String> newID = new ArrayList <String>();
	private ArrayList<ArrayList<String>> finalIds = new ArrayList<ArrayList<String>>();
	private ArrayList<Integer> finalCost = new ArrayList<Integer>();
	
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
		return finalIds;
	}

	public ArrayList<Integer> getFinalCost(){
		return finalCost;
	}
    
    /**
	 * Initializes connection with the database
	 * no return type or params
	 */	
    public void initializeConnection(){
    	// enclose within try catch so any SQL exceptions are caught
    	try 
    	{
    		//connection initialized using the credentials provided on object creation
    		connect = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
    	}
    	catch(SQLException e) 
    	{
    		e.printStackTrace();
    	}
    }
    
    /**
     * private custom method that closes everything and releases all resources
	 * no return type and has no args
	 */
    public void close(){
        try{
            results.close();
            connect.close();
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
    
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

    public boolean findCombo() {
		boolean orderFilled = false;
		makeChecklist();
    	try 
    	{
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
				boolean used = checkUsed();
				if(combMade && !used){
					mainStrategy();
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
	
	private void mainStrategy(){
		if(finalCost.size() < inputAmount){
			finalCost.add(totalCost);
			finalIds.add(new ArrayList <String>(newID));
		}
		else if(totalCost < Collections.max(finalCost)){
			int index = finalCost.indexOf(Collections.max(finalCost));
			finalCost.remove(index);
			finalIds.remove(index);
			finalCost.add(totalCost);
			finalIds.add(new ArrayList <String>(newID));
		}
	}

	private boolean lookForOneCombination(){
		boolean tmp = false;
		try{
			while (results.next())
			{
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

	private boolean checkUsed(){
		boolean temp = false;
		for(int o = 0; o < finalIds.size(); o++){
			ArrayList<String> tmp = finalIds.get(o);
			for(int i = 0; i < tmp.size(); i++){
				if(newID.contains(tmp.get(i))){
					temp = true;
				}
			}
		}
		return temp;
	}

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


	
	private void checklist(String[] x){
		try{
			if(results.getString(x[0]).equals("Y") && x[1] == null){
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
		catch(SQLException e) 
		{
			e.printStackTrace();    		
		}
	}
    
	public void printRecommedations(){
		try{
			StringBuilder suggestions = new StringBuilder();
			ArrayList<String> manuIDs = new ArrayList<String>();
			Statement myStatement;
			String qe;
			//making prepared statement for getting all ManuID related to type
			qe = "SELECT DISTINCT ManuID FROM "+ inputCategory + " WHERE Type = ?";
			PreparedStatement mySecondStatment = connect.prepareStatement(qe);
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
			suggestions.setLength(suggestions.length()-2);
			System.out.println("Suggested manufacturer(s): ");
			System.out.println(suggestions.toString());
		}
		catch(SQLException e) 
		{
			e.printStackTrace();    		
		}
	}
}
