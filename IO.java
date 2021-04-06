import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
//import java.util.stream.Sink.ChainedReference;






/**
 * IO class handles all input and output activities of the program
 * Mainly involves reading from the data base
 * taking in processed information to be formatted into an output file
 */	

public class IO
{
    private boolean combFound;
    private String category;
    private String type;
    private int amount;

    public IO(){
    }

    public void start(){
        //Collecting user input
        askUser();
        //Trying to find cheapest combination
        //action();
   
    }

    /*
    private boolean checkWithUser(data){
        //out data 
        //ask if good or not
        //return true or false
    }



    private void action(){
        
        //send data to dataHandler
        //get reply if combination found
        combFound = dataHandler.function(...., category, type, amount);
        
        //if no, output recommendations and terminate program
        if(combFound == false){
            dataHandler.outputRecommedations;
            System.exit(1);
        }
        else{
            dataHandler.getComb
            boolean answer = checkWithUser(comb)
            if(answer == true)
            {
                dataHandler.makeTxtFile;
                System.exit(1);
            }
            else{
                dataHandler.outputRecommedations;
                System.exit(1);
            }
        }

        if yes, ask user if the accept
        if accept, ask datahandler to make txt file
        if reject, terminate program
        

    }
    */
    private void askUser(){
        Scanner in = new Scanner(System.in);
        System.out.println("Start Program");
        System.out.println("Furniture Categories => chair, Desk, Filing, Lamp");
        System.out.println("Please select one of the categories: ");
        category = in.nextLine();
        in.nextLine();
        System.out.println("Chair types => Task, Mesh, Kneeling, Executive, Ergonomic");
        System.out.println("Desk types => Traditional, Adjustable, Standing");
        System.out.println("Filing types => Small, Medium, Large");
        System.out.println("Lamp types => Desk, Swing Arm, Study");
        System.out.println("Please select a appropriate type from the category chosen: ");
        type = in.nextLine();
        in.nextLine();
        System.out.println("Please specific the amount you would like: ");
        amount = in.nextInt();
        in.nextLine();
    }

    //chair
    //Chair
    //CHAIR
    //check data
    

    //data member for output handling
	public FileWriter output;
	
	/**
	 * No return type. Only performs output actions
	 * Takes in required information and formats it onto an output file 
	 * Following the specified format of the given example
	 * Overloaded method in case order cannot be fulfilled
	 * @param furnitureType is the piece of furniture that the customer wants e.g(chair)
	 * @param units is how many of that specific type of furniture the user is requesting
	 * @param ID is an array list consisting of all the ID's of the pieces used to fulfill a VALID COMPLETE order
	 * @param totalPrice is the total price of an order that can be fulfilled
	 */	
	public void output(String furnitureType, int units, ArrayList<String> ID,  double totalPrice) 
	{
		
		try 
		{
			//the output FileWriter will output to a text file called "output.txt"			
			this.output = new FileWriter("orderform.txt", false); //the second parameter of false tells it to overwrite an existing file of that name if it exists
			
			this.output.write("Furniture Order Form\n\n");
			this.output.write("Faculty Name: \n");
			this.output.write("Contact: \n");
			this.output.write("Date: \n\n");
			
			this.output.write("Original Request: " + furnitureType + ", /" + units + "\n\n");
			
			this.output.write("Items Ordered\n");
			
			for(int counter = 0; counter < ID.size(); counter++) 
			{
				this.output.write("ID: " + ID.get(counter) + "\n");
			}
			
			this.output.write("\nTotal Price: $" + "totalPrice");
			
			
		}
		catch(IOException e) 	//exception for output file
		{
			//System.out.println("error occurred with the output file");
			e.printStackTrace();
		}
	}
	
	
	//overloaded method
	//if only these two arguments are supplied, this means the order was unsuccessful
	public void output(String furnitureType, int units) 
	{
		try 
		{
			//the output FileWriter will output to a text file called "output.txt"			
			this.output = new FileWriter("output.txt", false); //the second parameter of false tells it to overwrite an existing file
			
			this.output.write("User request: " + furnitureType + ", " + units + "\n" );
			this.output.write("Output " + "Order cannot be fulfilled based on current inventory. Suggested manufacturers are: \n\n");
			this.output.write("Office Furnishings\n");
			this.output.write("Chairs R Us\n");
			this.output.write("Furniture Goods\n");
			this.output.write("Fine Office Supplies\n\n\n");
			this.output.write("Sorry we didn't have what you were looking for, please come back next time!");
			
		}
		catch(IOException e) 	//exception for output file
		{
			//System.out.println("error occurred with the output file")
			e.printStackTrace();
		}	
		
	}
}

//end of class declaration
