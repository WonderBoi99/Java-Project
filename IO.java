import java.util.Scanner;
import java.util.stream.Sink.ChainedReference;

public class IO{
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
    


}
