/*
 * This class will be a simple Naive Bayes classsifier used to compare him with the
 * other classifiers.
 */
package machine_learning;

import common.Evaluation;
import common.Example;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Nick
 */
public class NaiveBayes extends MachineLearningAlgorithm implements Classifier {
    
    /*how many training examples are categorized to category one.*/
    private double counterOne = 0;
    
    /*how many training examples are categorized to category zero.*/
    private double counterZero = 0;
    
    /*the possibility the category will one based on the training examples.*/
    private double possibilityOne;
    
    /*the possibility the category will zero based on the training examples.*/
    private double possibilityZero;
    
    /*weights for examples*/
    private double[] w;
////////////////////////////////////////////////////////////////////////////////    
    /*-----------------Constructor-----------------*/
    public NaiveBayes(String filename,int percentageOfTestData) throws FileNotFoundException, IOException, Exception {
        super(filename,percentageOfTestData); 
        
        out = new BufferedWriter(new FileWriter(MachineLearning.NAIVE_FILE));
    }
////////////////////////////////////////////////////////////////////////////////
    public void initialization() {
        int i;
        
        w = new double[trainData.size()];
        for(i=0; i<trainData.size(); i++){
            w[i]=1.0;
        }
        
        counterOne = 0;
        /*first of all we calculate the possibilities of the result will be 1 and
         the result being 0 according to the train data, so we calculate how many
         training examples are one*/
        for(i = 0; i < trainData.size(); i++) {
            if(trainData.get(i).getResult() == 1)
                counterOne++;
        }
        
        /*the number of zeros will be the number of training data if we remove the
         ones with classification one*/
        counterZero = trainData.size() - counterOne;
        
        /*and we calculate the possibilities*/
        possibilityOne = (double)((double)counterOne / (double)trainData.size());
        possibilityZero = (double) ((double)counterZero / (double)trainData.size());
    }
////////////////////////////////////////////////////////////////////////////////
    /*this class will be used in order to classify test data.*/
    @Override
    public void test() {
        int i;
        double accuracy;
        /*then for each test data*/
        for(i = 0; i < testData.size(); i++) {
            algorithmResultsTest[i] = classify(testData.get(i).getAttributesValues());
        }
        
        System.out.println("Classifying the test data set with Naive Bayes");
        Evaluation statistic = new Evaluation(true);
        accuracy = statistic.accuracy(algorithmResultsTest, testData);
        
        csvData += (accuracy * 100)+ ";";
        csvData += ((1 - accuracy) * 100)+ "\n";
    }
////////////////////////////////////////////////////////////////////////////////
    /*this function is used to train the algorithm it will not be implemented
     because the training in the Naive Bayes algorithm is just the storing of
     the training examples*/
    @Override
    public void train() {
    }
////////////////////////////////////////////////////////////////////////////////
    public void classifyTrainExamples() {
        int i;
        csvData += trainPercentage+ ";";
        double accuracy;
        double[] results = new double[trainData.size()];
        for(i = 0; i < trainData.size(); i++) {
            results[i] = classify(trainData.get(i).getAttributesValues());
        }
        
        System.out.println("Classifying train examples");
        Evaluation statistic = new Evaluation(true);
        accuracy = statistic.accuracy(results,trainData);
        
        csvData += (accuracy * 100)+ ";";
        csvData += ((1 - accuracy) * 100)+ ";";
    }
////////////////////////////////////////////////////////////////////////////////
    /*this function will be used for classifying a test example.*/
    @Override
    public double classify(double[] testExample) {
        double possibilityAttributeOne,possibilityAttributeZero;
        double counter, counter2;
        int j,k;
        double w1=0.0000001;
        
        possibilityAttributeOne = 1.0;
        possibilityAttributeZero = 1.0;
        
        /*for each attribute*/
        for(j = 0; j < numOfAttributes; j++) {
            counter = 0;
            counter2 = 0;
                
            /*we pass all the train data*/
            for(k = 0; k < trainData.size(); k++) {
                /*and count how many of them have category given the fact the attribute value
                 of the test data is the same with the one in the train data*/
                if(trainData.get(k).getResult() == 1 && trainData.get(k).getAttributesValues()[j] == testExample[j]) {
                    counter += w[k];
                    w1 += w[k];
                }
                    
                /*we do the same for the zero categoty..*/
                if(trainData.get(k).getResult() == 0 && trainData.get(k).getAttributesValues()[j] == testExample[j]) {
                    counter2 += w[k];
                }
            }
            
            if(w1<1) {
                possibilityAttributeOne *=(double)((double)counter) / (double)((double)counterOne);
                possibilityAttributeZero *=(double)((double)counter2) / (double)((double)counterZero);
            }
            else{
                /*we calculate the conditial probability of the category being one 
                * given the fact that the attributes are independent*/
                possibilityAttributeOne *= (double)((double)1 + (double)counter) / (double)((double)numOfCategories + (double)counterOne);
                
                /*we do the same for the conditial probability of the zero category*/
                possibilityAttributeZero *= (double)((double)1 + (double)counter2) / (double)((double)numOfCategories + (double)counterZero); 
            }
        }
        /*we check the probability that is larger, the possibility the category will be one
         given the attribute values of the test example, or the category being zero with
         this attributes*/
        if(possibilityAttributeOne * possibilityOne > possibilityAttributeZero * possibilityZero) {
                
            /*if the possibility of being one is larger we return 1*/
            return 1.0;
        }
        /*we return zero if the category being zero is larger*/
        else {
            return 0.0;
        }
    }
////////////////////////////////////////////////////////////////////////////////
    public void setWeights(double[] weights){
        this.w=weights;
        counterOne=0;
        counterZero=0;
        for(int i = 0; i < trainData.size(); i++) {
            if(trainData.get(i).getResult() == 1)
                counterOne += w[i];
            else{
                counterZero+=w[i];
            }
        }
        
        /*and we calculate the possibilities*/
        possibilityOne = (double)counterOne;
        possibilityZero =(double)counterZero;
        
    }
    
    public double[] getWeights(){
        return this.w;
    }
    
    public void setTrainData(ArrayList<Example> trainExamples) {
        this.trainData = trainExamples;
    }
    
    public void setTestData(ArrayList<Example> testExamples) {
        this.testData = testExamples;
    }
}
