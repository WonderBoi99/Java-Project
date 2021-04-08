/**
 *@author Gibran Akmal, 30094918
 *@author John Kvellestad, 10125207
 *@author Aashik Ilangovan, 30085993
 *@author Nikhil Naikar, 30039350

 *@version 11
 *@since 10
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.lang.StringBuilder;

/**
 * IO class collects input from user for order
 * Uses the DataHandler class to interact with the database
 * Looks for cheapeast combination to fill order from user
 * If no combination found, recommendations of manufacturers will be displayed
 * If yes combination found, will show and ask user if they are happy with the combination
 * If user says yes, then a orderform.txt file will be made and the UsedIds will be deleted from the database
 * If user says no, then recommendations of manufacturers will be displayed 
 */	
public class IO{
    private String category;
    private String type;
    private int amount;
    private int sum;
    private ArrayList<ArrayList<String>> allIds = new ArrayList<ArrayList<String>>();
	private ArrayList<Integer> allCosts = new ArrayList<Integer>(); 

    public IO(){
    }

    /**
     * starts the program
     */
    public void start(){
        boolean combFound;
        //Collecting user input
        askUser();
        //Trying to find cheapest combination
        DataHandler test = new DataHandler(category, type, amount);
        combFound = test.findCombo();
        if(combFound == false){
            test.printRecommedations();
            System.exit(1);
        }
        else{
            allIds = test.getFinalIds();
            allCosts = test.getFinalCost();
            sum = test.sumOfCosts();
            boolean answer = checkWithUser();
            if(answer == true)
            {
                //dataHandler.makeTxtFile;
                System.out.println("Text file made");
                makeFile();
                test.deleteUsedIds();
                System.exit(1);
            }
            else{
                test.printRecommedations();
                System.exit(1);
            }
        }
    }

    /**
     * Checks if user accepts the combinations found
     * If user says yes, then a output.txt file will be made and the UsedIds will be deleted from the database
     * If user says no, then recommendations of manufacturers will be displayed 
     */
    private boolean checkWithUser(){
        String answer;
        boolean tmp = false;
        Scanner in = new Scanner(System.in);
        System.out.println("Order: "+type+" "+category+", "+amount);
        System.out.println("Here is what we can provide: ");
        for(int o = 0; o < allIds.size(); o++){
            StringBuilder solution = new StringBuilder("ID: ");
            ArrayList<String> temp = allIds.get(o);
            for(int i = 0; i < temp.size(); i++){
                solution = solution.append(temp.get(i)+", ");
            }
            solution.setLength(solution.length()-2);
            solution.append(" => $"+allCosts.get(o));
            System.out.println(solution.toString());
        }
        System.out.println("Total Cost => $"+sum);
        while(true){
            System.out.println("Are you happy with this? (yes/no): ");
            answer = in.nextLine();
            if(answer.equals("yes")){
                tmp = true;
                break;
            }
            else if(answer.equals("no")){
                tmp = false;
                break;
            }
            System.out.println("Please enter yes or no");
        }   
        return tmp;
    }

    /**
     * gets input from user for category, type, and order size
     */
    private void askUser(){
        Scanner in = new Scanner(System.in);
        System.out.println("Start Program");
        askForCategory();
        System.out.println();
        askForType();
        System.out.println();
        System.out.println("Please specific the amount you would like: ");
        amount = in.nextInt();
        System.out.println("-----------------------");
    }

    /**
     * keeps asking user for category until they input a category that exists
     */
    private void askForCategory(){
        Scanner in = new Scanner(System.in);
        while(true){
            System.out.println("Please select one of the categories: ");
            System.out.println("Furniture Categories => chair, desk, filing, lamp");
            category = in.nextLine();
            if(category.equals("chair") || category.equals("desk") || category.equals("filing") || category.equals("lamp")){
                break;
            }
            System.out.println("Spelling mistake or "+category+" category does not exist");
        }
    }

    /**
     * keeps asking user for type until they input a correct type in the specific category they chose
     */
    private void askForType(){
        Scanner in = new Scanner(System.in);
        while(true){
            System.out.println("Please select a appropriate type from the category chosen: ");
            if(category.equals("chair")){
                System.out.println("Chair types => Task, Mesh, Kneeling, Executive, Ergonomic");
                type = in.nextLine();
                if(type.equals("Task") || type.equals("Mesh") || type.equals("Kneeling") || type.equals("Executive") || type.equals("Ergonomic")){
                    break;
                }
            }
            else if(category.equals("desk")){
                System.out.println("Desk types => Traditional, Adjustable, Standing");
                type = in.nextLine();
                if(type.equals("Traditional") || type.equals("Adjustable") || type.equals("Standing")){
                    break;
                }
            }
            else if(category.equals("filing")){
                System.out.println("Filing types => Small, Medium, Large");
                type = in.nextLine();
                if(type.equals("Small") || type.equals("Medium") || type.equals("Large")){
                    break;
                }
            }
            else if(category.equals("lamp")){
                System.out.println("Lamp types => Desk, Swing Arm, Study");
                type = in.nextLine();
                if(type.equals("Desk") || type.equals("Swing Arm") || type.equals("Study")){
                    break;
                }
            }
            
            System.out.println("Spelling mistake or "+type+" is not a type in "+category+" category");
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
			
			output.write("Original Request: "+type+" "+category+", "+amount+"\n\n");
			
			output.write("Items Ordered:\n");
			
            for(int o = 0; o < allIds.size(); o++){
                ArrayList<String> tmp = allIds.get(o);
                for(int i = 0; i < tmp.size(); i++){
                    output.write(tmp.get(i)+"\n");
                }
            }
			
			output.write("\nTotal Price: $" + sum);

            output.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}	
}


