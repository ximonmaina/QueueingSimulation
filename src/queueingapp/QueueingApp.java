
package queueingapp;

import java.io.FileNotFoundException;
import java.io.IOException;
import static java.lang.Math.log;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Formatter;
import java.util.FormatterClosedException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 *
 * @author greendelta
 */
public class QueueingApp {

   private  static final int Q_LIMIT = 100;
   private static final int BUSY = 1;
   private static final int IDLE = 0;
   private static Scanner input;
   private static Formatter output;
   
   
   private static int nextEventType, numCustsDelayed,numEvents, numInQ, serverStatus;
   private static int numDelaysRequired;
   
   private static double areaNumInQ, areaServerStatus, time,
           time_i, timeLastEvent, totalOfDelays;
  private static double []  timeArrival = new double[Q_LIMIT +1];
  private static double [] timeNextEvent = new double[3];
  private static double meanInterarrival; 
  private static double meanService;
           
      /*Main mehtod*/     
    public static void main(String[] args) throws IOException {
       openInputFiles();
       openOutputFile2();
       readContents();
       
       
       /* Specify the number of events for timing function */
       numEvents = 2;
       
       /* Write report heading and input parameters */
       try {
           output.format("Single-server queueing system %n%n");
           output.flush();
           output.format("Mean interarrival time%11.3f minutes%n%n",meanInterarrival);
           output.flush();
           output.format("Mean service time%16.3f minutes%n%n",meanService);
           output.flush();
           output.format("Number of customers%14d",numDelaysRequired);
       }
       catch (FormatterClosedException formatterClosedException){
           System.err.println("Error writing to file.Terminating");
           
       }
       
       
       /* Initialize the simulation. */
       initialize();
      
       
       /* Run the simulation while more delays are still needed */
       while(numCustsDelayed < numDelaysRequired){
            
           /* Determine the next event */
           timing();
           
           /* Update time-average statistical accumulators */
           updateTimeAvgStats();
           
           /* Invoke the appropriate event function */
           
           switch(nextEventType){
               case 1:
                   arrive();
                   break;
               case 2:
                   depart();
                   break;                   
           }
       }
       
       /* Invoke the report generator and end the simulation */
       report();       
       closeFiles();
       
      
    }
    
    
    
    public static void openInputFiles() throws IOException{
        /*Open file to read from*/
        try{
          input = new Scanner(Paths.get("/home/greendelta/NetBeansProjects/QueueingApp/src/mmIn.txt"));
         
          
        }catch (SecurityException securityException){
           System.err.println("Write Permission deined");
           System.exit(1);
        }        
        catch(IOException ioException){
            System.err.println("Error opening file one. Terminating.");
            System.exit(1);
        }
        
       
    }
    
    public static void openOutputFile2(){
           /*Open file to write to*/
        try{
          output = new Formatter("/home/greendelta/NetBeansProjects/QueueingApp/src/mmOut.txt"); //open file
        }
        catch (FileNotFoundException fileNotFounfException){
           System.err.println("Error opening file two. Terminating");
           System.exit(1);
        }
    }
    //read file contents
    public static void readContents(){
        try{
           while(input.hasNext()){
              meanInterarrival = input.nextDouble();
              meanService = input.nextDouble();
              numDelaysRequired = input.nextInt();              
           }
        }
           catch (NoSuchElementException elementException){
                   System.err.println("File improperly formed. Terminating");
                   }
            catch (IllegalStateException stateException){
                System.err.println("erro reading from file");
            }
        
        }
    
    //close files
    public static void closeFiles(){
        if(input != null){
            input.close();
        }
        
        if(output != null){
           output.close();
        }
        
        
    }
    
    /* Initialize the function */
    public static void initialize(){
       
        /* Initialize the simulation clock */
        time_i = 0.0;
        
        /* Initialize the state variables */
        serverStatus = IDLE;
        numInQ = 0;
        timeLastEvent = 0.0;
        
        /* Initialize the statistical counters */
        numCustsDelayed = 0;
        totalOfDelays = 0.0;
        areaNumInQ = 0.0;
        areaServerStatus = 0.0;
        
        /* Initialize event list. Since no customers are present, the departure (service completion) event is
    is eliminated from consideration*/
        timeNextEvent[1] = time_i + expon(meanInterarrival);
        timeNextEvent[2] = 1.0e+30;
        
     }
    
    /* Timing function */
    public static void timing(){
        int i;
        double minTimeNextEvent = 1.0e+29;
        
        nextEventType = 0;
        
        /* Determine the event type of the next event to occur. */
        for(i = 1; i <= numEvents; ++i){
            if(timeNextEvent[i] < minTimeNextEvent){
                 minTimeNextEvent = timeNextEvent[i];
                 nextEventType = i;
            }
        }
        
        /*  Check to see whether the event list is empty  */
        if(nextEventType == 0){
           /* The event list is empty, so stop the simulation */
           try{
              output.format("%nEvent list empty at time %t%n", time_i);
              System.exit(1);
           } catch (FormatterClosedException formatterClosedException){
           System.err.println("Error writing to file.Terminating");           
           }
        }
        
        /* The event list is not empty, so advance the simulation clock */
        time_i = minTimeNextEvent;        
        
    }
    
    /* arrival event function */
    public static void arrive()  {
         double delay;
         
         /* Schedule next arrival */
         timeNextEvent[1] = time_i + expon(meanInterarrival);
         
         /* Check  to see whether server is busy. */
         if(serverStatus == BUSY){
            /* Server is busy, so increment number of customers in queue. */
            ++numInQ;
             /* Check to see whether an overflow condition exists. */
             if( numInQ > Q_LIMIT){
                 // The queue has overflowed, so stop the simulation.
                 try{
                       output.format("%nOverflow of the array at time %f%n", time_i);
                       System.exit(2);
                    } catch (FormatterClosedException formatterClosedException){
                        System.err.println("Error writing to file.Terminating");           
                       }
             }
             /* There is still room in queue, so store the time of
             arrival of the arriving customer at the (new) end of time_arrival. */
             timeArrival[numInQ] = time_i;
         }
         else {
             /* Server is idle, so arriving customer has a delay of zero.
                ( The following two statements are for program clarity and do not affect the
                results of the simulation.) */
             delay = 0.0;
             totalOfDelays += delay;
             
             /* Increment the number of customers delayed, and make server busy. */
             ++numCustsDelayed;
             serverStatus = BUSY;
             
              /* Schedule a departure (server completion) */
              timeNextEvent[2] = time_i + expon(meanService);
         }
    }
    
    public static void depart(){
        int i;
        double delay;
        
         /* Check to see whether the queue is empty */
         if(numInQ == 0){
           /* The queue is empty so make the server idle and eliminate the
             departure (service completion) event from consideration. */  
           serverStatus = IDLE;
           timeNextEvent[2] = 1.0e+30;
         }
         else {
           /* The queue is nonempty, so decrement the number of customers in queue. */
           --numInQ;
           
          /* Compute the delay of the customer who is beginning service and
            update the total delay accumulator. */ 
          delay = time_i - timeArrival[1];
          totalOfDelays += delay;
          
           /* Increment the number of customers delayed, and schedule */
           ++numCustsDelayed;
           timeNextEvent[2] = time_i + expon(meanService);
           
             /* Move each customer in queue (if any) up one place. */
            for(i = 1; i <= numInQ; ++i) {
               timeArrival[i] = timeArrival[i + 1];
            }
         }
    }
    
    public static void report(){
        /* Compute and write estimates of desired measures of performance. */
        try{
             output.format("%n%nAverage delay in queue%11.3f minutes%n%n", (totalOfDelays/numCustsDelayed));
             output.format("Average number in queue%10.3f%n%n",(areaNumInQ/time_i));
             output.format("Server utilization%15.3f%n%n",(areaServerStatus/time_i));
             output.format("time simulation ended%12.3f", time_i );
            } catch (FormatterClosedException formatterClosedException){
                System.err.println("Error writing to file.Terminating");           
            }
       
    }
    
    public static void updateTimeAvgStats(){
       double timeSinceLastEvent;
       
       /* Compute time since last event, and update last-event-time marker */
       timeSinceLastEvent = time_i - timeLastEvent;
       timeLastEvent = time_i;

       /* Update area under number-in-queue function */
       areaNumInQ += numInQ * timeSinceLastEvent;
       
       /* Update area under server-busy indicator function. */
       areaServerStatus += serverStatus * timeSinceLastEvent;
       
       
    }
    
    public static double expon (double mean){
      double u;
      /* Generate a U(0,1) random variate */
      
      u = getrandom();
      
     /* Return an exponential random variate with mean "mean". */
     return (-mean * log(u));
    }
    
    public static double  getrandom(){
        double r = Math.random();
        return (r);
    
}
    
    
  }
    
    
    
  

