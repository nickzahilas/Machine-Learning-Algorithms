/*
 * This class will help us get some statistics according to the algorithm we examine.
 * It will tell us how good the algorithm goes with the test example, but it
 * may be also used to evaluate the training examples.
 */
package common;

import java.util.ArrayList;

/**
 *
 * @author Nick
 */
public class Evaluation {
    /*it will tell us if the algorithm we are testing is a classifier or
     * regression.
     */
    boolean classifier;
////////////////////////////////////////////////////////////////////////////////
    /*-----------------Constructor-----------------*/
    public Evaluation(boolean classifier) {
        this.classifier = classifier;
    }
////////////////////////////////////////////////////////////////////////////////
    /*this function will calculate the accuracy of our algorithms it will check
     how well the algorithm guesses the real values of the examples and print
     the results.
     */
    public double accuracy(double[] algorithmResults, ArrayList<Example> realResults) {
        int numOfAccurateResults = 0,i;
        double accuracy,inaccuracy;
        
        /*if the algorithm we test is a classifier..*/
        if(classifier) {
            /*we count the number of accurate results*/
            for(i = 0; i < realResults.size(); i++) {
                
                /*by checking if the predicstion is equal with the real result*/
                if(algorithmResults[i] == realResults.get(i).getResult())
                    numOfAccurateResults++;
            }
        
            /*we print the statistics needed to draw our conclusions*/
            accuracy = (double)numOfAccurateResults / (double)realResults.size();
        
            System.out.println("Number of data " +realResults.size());
            System.out.println("Number of accurate predictions " +numOfAccurateResults);
            System.out.println("Number of inaccurate predictions " +(realResults.size() - numOfAccurateResults));
            System.out.println("Accuracy rate " +(accuracy * 100)+ "%");
            System.out.println("Inaccuracy rate " +( (1 - accuracy) * 100)+ "%");
        }
        /*if we are testing a regression method*/
        else {
            System.out.println("Calculating accuracy for a regression function.");
            double SSerr;
            
            SSerr = 0.0;
            /*we count the number of accurate results*/
            for(i = 0; i < algorithmResults.length; i++) {
                SSerr = SSerr + Math.pow(realResults.get(i).getResult() - algorithmResults[i],2);
                
                System.out.println("Algorithm Result "+algorithmResults[i]+ " Real Result " +realResults.get(i).getResult());
            }
            
            inaccuracy = SSerr / algorithmResults.length;
            accuracy  = 1 - inaccuracy;
            System.out.println("Accuracy rate " +(accuracy * 100)+ "%");
            System.out.println("Inaccuracy rate " +( inaccuracy * 100)+ "%");
        }
        
        return accuracy;
    }
////////////////////////////////////////////////////////////////////////////////
}