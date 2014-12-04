/*
 * This class will be used to simulate the least squares algorithm (local regression)
 * on the datasets passed as arguments.
 */
package machine_learning;

import common.Calculator;
import common.DataNormalizer;
import common.DistanceKNeighbors;
import common.Evaluation;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Nikos Zacheilas
 */
public class LeastSquares extends MachineLearningAlgorithm {
    /*the k parameter that will be the number of neighbors we will get.*/
    private int k;  
    
    /*the n parameter in the gradient descend.*/
    private double nParameter = 0.001;   
    
    /*the array that will hold the results of the algorithm on the test examples.*/
    private double[] results;
    
    /*an object of the DataNormalizer class used to normalize the results of the
     train examples.*/
    private DataNormalizer categoriesNormalizer;
    
    /*this variable will help us check if the we have already normalized the
     test data.*/
    private boolean hasTestBeenNormalized = false;
////////////////////////////////////////////////////////////////////////////////
    /*-----------------Constructor-----------------*/
    public LeastSquares(String fileName, int percentageOfTest, int k) throws FileNotFoundException, IOException, Exception{
        /*we call the constructor of the parent class*/
        super(fileName,percentageOfTest);
        this.k = k;
        
        int i;
        
        /*we normalize the results of the train examples*/
        double[] resultsTrain = new double[allTrainData.size()];
        for(i = 0; i < allTrainData.size(); i++) {
            resultsTrain[i] = allTrainData.get(i).getResult();
        }
        
        /*we normalize the results of the all the train examples*/
        categoriesNormalizer = new DataNormalizer(resultsTrain);
        
        for(i = 0; i < resultsTrain.length; i++) {
            allTrainData.get(i).setResult(categoriesNormalizer.normalizeData(resultsTrain[i]));
            
            System.out.println("Train example "+i+ " result "+allTrainData.get(i).getResult());
        }
        
        /*and initialize the table where we will store the results of the algorithm
         on the test examples*/
        results = new double[testData.size()];
        
        /*we normalize the values of the attributes of the train data*/
        this.normalizeTrainData();
        
        out = new BufferedWriter(new FileWriter(MachineLearning.LEAST_FILE));
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
        for (i = 0; i <  trainData.size() ; i++){
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
    /*the method that will return the result of the function used in the least
     squares for the attributes and weights passed as parameter*/
    public double fw(double[] weights,double[] attributes) {
        int i;
        /*we start the y value with the weights in position zero*/
        double y = weights[0];
        for(i = 1; i < weights.length; i++) {
            /*we add the product of the current weight and attribute value to the
             variable we will return*/
            y += weights[i] * attributes[i - 1];
        }
        
        /*we return the value calculated*/
        return y;
    }
////////////////////////////////////////////////////////////////////////////////
    /*the function that will be used to find weights the function of the training examples
     and then will be used in order. It will return the weights we must use in
     the regression function.*/
    public double[] stochasticGradientDescend(int maxIterations,ArrayList<double[]> neighboringExamples,double[] classifications) {
        
        double[] weights = new double[numOfAttributes + 1];
        double s,oldValue = 1.0,tolerance = 0.0001,xl;
        int i,numOfIterations,j;
        Random generate = new Random();
        
        /*we add random numbers in the weights table*/
        for(i = 0; i < weights.length; i++) {
           
            weights[i] = generate.nextDouble();
        }   
        
        s = 0.0;
        numOfIterations = 0;
        
        /*while we have iterations and we have not exceeded the tolerance*/
        while(numOfIterations < maxIterations && Math.abs(s - oldValue) > tolerance) {
            oldValue = s;
            s = 0.0;
            
            /*for all the neighbors*/
            for(i = 0; i < neighboringExamples.size(); i++) {
                
                /*we change the value of the variable used for the convergence*/
                s = s + (Math.pow(fw(weights,neighboringExamples.get(i)) - classifications[i], 2.0)) / 2.0;
            
                /*we recalculate the weights*/
                for(j = 0; j < weights.length; j++) {
                    if(j == 0) {
                        xl = 1.0;
                    }
                    else
                        xl = neighboringExamples.get(i)[j - 1];
                    
                    /*using the formula in the least squares algorithm*/
                    weights[j] = weights[j] - nParameter * (fw(weights,neighboringExamples.get(i)) - classifications[i]) * xl;
                }
            }
            
            /*we increase the number of iterations*/
            numOfIterations++;
        }
        
        /*and finally we return the weights*/
        return weights;
    }
////////////////////////////////////////////////////////////////////////////////
    /*the function that will be used to find weights the function of the training examples
     and then will be used in order. It will return the weights we must use in
     the regression function.*/
    public double[] batchGradientDescend(ArrayList<double[]> neighboringExamples,double[] classifications) {
        double[] weights = new double[numOfAttributes + 1];
        double s = 0.0,oldValue = 1.0,tolerance = 0.0001,xl,sum;
        int i,j;
        Random generate = new Random();
        
        /*we add random numbers in the weights table*/
        for(i = 0; i < weights.length; i++) {
           
            weights[i] = generate.nextDouble();
        }  
        
        while(Math.abs(s - oldValue) > tolerance) {
            oldValue = s;
            
            for(j = 0; j < weights.length; j++) {
                
                sum = 0.0;
                for(i = 0; i < neighboringExamples.size(); i++) {
                    if(j == 0)
                        xl = 1.0;
                    else
                        xl = neighboringExamples.get(i)[j - 1];
                
                    sum = sum + (fw(weights,neighboringExamples.get(i)) - classifications[i]) * xl;
                }
                weights[j] = weights[j] - nParameter * sum;
            }
            
            sum = 0.0;
            for(i = 0; i < neighboringExamples.size(); i++) {
                sum = sum + Math.pow(fw(weights,neighboringExamples.get(i)) - classifications[i],2);
            }
            s = sum / 2;
        }
        
        //System.out.println("Number of iterations until convergence "+iterations);
        return weights;
    }
////////////////////////////////////////////////////////////////////////////////
    /*this function will be called in order to test how well our algorithm goes
     on the training data.*/
    public void testOnTrainExamples() {
        int i;
        double accuracy;
        csvData += trainPercentage+ ";";
        double[] trainResults = new double[trainData.size()];
        
        /*we examine each train example*/
        for(i = 0; i < trainData.size(); i++) {
            /*and we call the regression function for it*/
            trainResults[i] = regression(trainData.get(i).getAttributesValues());
        }
        
        /*finally we get some statistics*/
        System.out.println("Testing least squares on training examples");
        Evaluation statistic = new Evaluation(false);
        accuracy = statistic.accuracy(trainResults, trainData);
        
        csvData += (accuracy * 100)+ ";";
        csvData += ((1 - accuracy) * 100)+ ";";
    }
////////////////////////////////////////////////////////////////////////////////
    /*the test function with which we will classify the test data.*/
    public void test(){
        int i,j;
        double accuracy;
        
        /*for each test example...*/
        for(i = 0; i < testData.size(); i++) {
            for(j = 0; j < numOfAttributes; j++) {
			}
            
            /*if it has not been normalized we normalize it*/
            if(!hasTestBeenNormalized) {
                /*we firstly normalize the attributes of the test example*/
                for(j = 0; j < numOfAttributes;j++) {
                    testData.get(i).getAttributesValues()[j] = normalizers.get(j).normalizeData(testData.get(i).getAttributesValues()[j]);
                }
                testData.get(i).setResult(categoriesNormalizer.normalizeData(testData.get(i).getResult()));
            }
         
            /*and finally calculate the value of the test example*/
            results[i] = regression(testData.get(i).getAttributesValues());
        }
        
        /*we make it true so we will not normalize the test data again*/
        hasTestBeenNormalized = true;
        System.out.println("Testing least squares on test examples");
        Evaluation statistic = new Evaluation(false);
        accuracy = statistic.accuracy(results, testData);
        
        csvData += (accuracy * 100)+ ";";
        csvData += ((1 - accuracy) * 100)+ "\n";
    }
////////////////////////////////////////////////////////////////////////////////
    /*this function will be used to classify an example.*/
    public double regression(double[] exampleValues) {
        ArrayList<double[]> trainExamples = new ArrayList<>();
        DistanceKNeighbors[] neighbors;
        double[] classifications = new double[k];
        double[] weights;
        double result;
        int j;
        
        /*we find the k nearest neighbors*/
        neighbors = findKNeighbors(k,exampleValues);
        
        /*we get the training examples that the neighbors refer to*/
        for(j = 0; j < neighbors.length; j++) {
            trainExamples.add(trainData.get(neighbors[j].getTrainingExample()).getAttributesValues());
            classifications[j] = trainData.get(neighbors[j].getTrainingExample()).getResult();
        }  
            
        /*and we calculate the weights using stochastic gradient descend*/
        //weights = stochasticGradientDescend(maxIterations,trainExamples,classifications);
        weights = batchGradientDescend(trainExamples,classifications);
        
        /*and finally calculate the value of the test example*/
        result = fw(weights,exampleValues);
        return result;   
    }
////////////////////////////////////////////////////////////////////////////////
}