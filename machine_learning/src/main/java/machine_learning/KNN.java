/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package machine_learning;

import common.Calculator;
import common.DistanceKNeighbors;
import common.Evaluation;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Nikos Zacheilas
 */
public class KNN extends MachineLearningAlgorithm implements Classifier {
    
    /*the k parameter that will be the number of neighbors we will get.*/
    private int k;  
    
    /*this variable will help us check if the test data has been normalized.*/
    private boolean hasTestBeenNormalized = false;
    
    /*-----------------Constructor-----------------*/
    public KNN(String filename,int percentageOfTestData,int k) throws FileNotFoundException, IOException, Exception {
        super(filename,percentageOfTestData); 
        
        this.k = k;
        
        /*and we normalize the all the train data.*/
        this.normalizeTrainData();
        
        out = new BufferedWriter(new FileWriter(MachineLearning.KNN_FILE));
    }
////////////////////////////////////////////////////////////////////////////////
    /*this function will find the k-nearest neighbor of the new test data passed
     as parameter.
     */
    public DistanceKNeighbors[] findKNeighbors(int k,double[] newAttributes) {
        
        DistanceKNeighbors[] distances =  new DistanceKNeighbors[trainData.size()];
        int i;
        
        /*we calculate the distance of each train data with the new test data passed
         as parameter
         */
        for (i=0; i<  trainData.size() ; i++){
            distances[i] = new DistanceKNeighbors(Calculator.distance(newAttributes,trainData.get(i).getAttributesValues()),i);
        }
        
        /*we sort them (ascending order)*/
        this.quickSort(distances,0,distances.length - 1);
        
        /*and we create the array we will return that will have k-elements*/
        DistanceKNeighbors[] distancesToReturn = new DistanceKNeighbors[k];
        
        /*we get the first k because this elements will have the least distances*/
        for(i = 0; i < k; i++) {
            distancesToReturn[i] = distances[i];
        }
        
        return distancesToReturn;
    }
////////////////////////////////////////////////////////////////////////////////
    /*this will be the partition function of the quick sort algorithm.*/
    int partition(DistanceKNeighbors[] arr, int left, int right)
    {
        int i = left, j = right;
        DistanceKNeighbors tmp;
        /*we get the pivot element*/
        DistanceKNeighbors pivot = arr[(left + right) / 2];
        
        /*while there are still elements*/
        while (i <= j) {
            /*we pass all element that are in the correct position*/
            while (arr[i].getDistance() < pivot.getDistance())
                  i++;
            while (arr[j].getDistance() > pivot.getDistance())
                  j--;
            /*and swap the others*/
            if (i <= j) {
                  tmp = arr[i];
                  arr[i] = arr[j];
                  arr[j] = tmp;
                  i++;
                  j--;
            }
        }
     
        return i;
    }
////////////////////////////////////////////////////////////////////////////////
    /*this function will be used to sort the array of neighbors passed as argument
     using the quick sort algorithm.*/
    void quickSort(DistanceKNeighbors[] arr, int left, int right) {
        /*we call the partition method*/
        int index = partition(arr, left, right);
        
        /*and we sort the left and right part of the table calling the quick sort
         recursively*/
        if (left < index - 1)
            quickSort(arr, left, index - 1);
        if (index < right)
            quickSort(arr, index, right);
    }
////////////////////////////////////////////////////////////////////////////////
    /*this function will help us test our algorithm with the test examples.*/
    @Override
    public void test() {
        int i,j;
        double accuracy;
        /*for each test example...*/
        for(i = 0; i < testData.size(); i++) {
            
            /*if the test data has not been normalized we normalize it*/
            if(!hasTestBeenNormalized) {
                /*we firstly normalize the attributes of the test example*/
                for(j = 0; j < numOfAttributes;j++)
                    testData.get(i).getAttributesValues()[j] = normalizers.get(j).normalizeData(testData.get(i).getAttributesValues()[j]);
            }
            
            /*and then classify the example calling the correct function*/
            algorithmResultsTest[i] = classify(testData.get(i).getAttributesValues());
        }
        
        /*we change this variable because the test data has been normalized at
         least once.*/
        hasTestBeenNormalized = true;
        
        System.out.println("Classifying the test examples with KNN");
        /*finally we create a Statistics object in order to get statistics for
         the KNN algorithm*/
        Evaluation statistic = new Evaluation(true);
        accuracy = statistic.accuracy(algorithmResultsTest, testData);
        
        
        csvData += (accuracy * 100)+ ";";
        csvData += ((1 - accuracy) * 100)+ "\n";
        
    }
////////////////////////////////////////////////////////////////////////////////
    /*this function is used to train the algorithm it will not be implemented
     because the training in the KNN algorithm is just the storing of
     the training examples*/
    @Override
    public void train() {
        
    }
////////////////////////////////////////////////////////////////////////////////
    /*this function will be used to test how well our algorithm goes with the
     training data.*/
    public void classifyTrainExamples() {
        csvData += trainPercentage+ ";";
        int i;
        double accuracy;
        /*the array where we will store the results*/
        double[] results = new double[trainData.size()];
        for(i = 0; i < trainData.size(); i++) {
            results[i] = classify(trainData.get(i).getAttributesValues());
        }
        
        System.out.println("Classifying train with KNN");
        /*finally we get the statistics*/
        Evaluation statistic = new Evaluation(true);
        accuracy = statistic.accuracy(results, trainData);
        
        csvData += (accuracy * 100)+ ";";
        csvData += ((1 - accuracy) * 100)+ ";";
    }
////////////////////////////////////////////////////////////////////////////////
    /*this function will be used for classifying a test example.*/
    @Override
    public double classify(double[] testExample) {
        int countOne,j;
        DistanceKNeighbors[] neighbors;
        
        /*we find the k nearest neighbors*/
        neighbors = findKNeighbors(k,testExample);
            
        /*and calculate how many of them are classified to one*/
        countOne = 0;
            
        for(j = 0; j < neighbors.length; j++) {
            if(trainData.get(neighbors[j].getTrainingExample()).getResult() == 1.0)
                countOne++;
        }
            
        /*if the number of neighboring train examples classified to one are more
          than the ones classified as zero then we classify the test to one*/
        if(countOne > k - countOne) {
            return 1.0;
        }
        else {
            return 0.0;
        }
    }
////////////////////////////////////////////////////////////////////////////////
}
