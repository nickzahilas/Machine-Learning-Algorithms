/*
 *The parent class of all the algorithms we implemented. Has some basic functions
 * needed from all the algorithms.
 */
package machine_learning;

import common.DataNormalizer;
import common.DataReader;
import common.Example;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Nick
 */
public abstract class MachineLearningAlgorithm {
    /*the reader that will be used to access the data from the correct file.*/
    protected DataReader reader;  
    
    /*the number of attributes we have.*/
    protected int numOfAttributes; 
    
    /*the number of different categories we can classify.*/
    protected int numOfCategories; 
    
    /*the total number of training examples.*/
    protected int totalNumberOfTrainExamples;
    
    /*an array that will hold all the train data.*/
    protected ArrayList<Example> allTrainData;

    /*the train data we will use it will be a percentage of the above array.*/
    protected ArrayList<Example> trainData;  
    
    /*the test data we will use.*/
    protected ArrayList<Example> testData;   
    
    /*the evaluation data we will use.*/
    protected ArrayList<Example> validationData;   
    
    /*we will have for each attribute an instance of the normalizer class that will be used to do the normalization.*/
    protected ArrayList<DataNormalizer> normalizers; 
    
    /*this table will hold the results the algorithm had on the test data set.*/
    protected double[] algorithmResultsTest;
    
    /*the currently explored percentage of the train examples*/
    protected int trainPercentage;
    
    /*this variable will be used to write to the csv file we have to store the results
     of each algorithm.*/
    protected BufferedWriter out;
    
    /*the data that will be stored in the csv file.*/
    protected String csvData = "";
    
////////////////////////////////////////////////////////////////////////////////    
    /*-----------Constructor-----------*/
    public MachineLearningAlgorithm(String fileName,int percentageOfTestData) throws FileNotFoundException, IOException, Exception {
        int i,j;
        double result = 0.0;
        Example newExample;
        /*we read all the data from the set passed as argument*/
        reader = new DataReader(fileName);
        reader.readData();
       
        trainData = new ArrayList<>();
        testData = new ArrayList<>();
        allTrainData = new ArrayList<>();
        
        /*we get the number of categories*/
        numOfCategories = reader.getNumOfUniqueCategories();
        
        /*and the number of attributes*/
        numOfAttributes = reader.getNumOfAttrubutes();
        
        normalizers = new ArrayList<>();
        
        ArrayList<ArrayList<String>> testAttributes = reader.generateRandomTestData(percentageOfTestData);
        ArrayList<ArrayList<String>> trainAttributes = reader.getAttributesValues();
        
        /*we initialize the categories tables*/
        algorithmResultsTest = new double[testAttributes.size()];
        
        System.out.println("Number of attributes "+numOfAttributes);
        
        /*we fill the train data table with real data (they will be stored as doubles)*/
        for(i = 0; i < trainAttributes.size(); i++) {
            
            /*if we have the breast cancer data set we have a classification
            task but the classifications is two and four so we change them to
            zero and one*/
            if(fileName.equals(MachineLearning.BREAST_CANCER_DATA_SET)) {
                    /*according to the classification of the data we store it as 0 or 1*/
                    switch (reader.getClassifications().get(i)) {
                        case "2":
                            result = 0.0;
                            break;
                        case "4": 
                            result = 1.0;
                            break;
                        default:
                            break;
                }
            }
            else if(fileName.equals(MachineLearning.HEART_DATA_SET)) {
                    /*according to the classification of the data we store it as 0 or 1*/
                    switch (reader.getClassifications().get(i)) {
                        case "1":
                            result = 0.0;
                            break;
                        case "2": 
                            result = 1.0;
                            break;
                        default:
                            break;
                    }
            }
            else 
                result = Double.parseDouble(reader.getClassifications().get(i));
            
            /*we add attributes values in an array*/
            double[] currAttributes = new double[reader.getNumOfAttrubutes()];
            for(j = 0; j < trainAttributes.get(i).size(); j++) {
                currAttributes[j] = Double.parseDouble(trainAttributes.get(i).get(j));
            }
            
            newExample = new Example(currAttributes,result);
            /*and then add the table to the ArrayList*/
            allTrainData.add(newExample);
        }   
        
        totalNumberOfTrainExamples = allTrainData.size();
        
        /*we do the same job for the test data*/
        for(i = 0; i < testAttributes.size(); i++) {
            if(fileName.equals(MachineLearning.BREAST_CANCER_DATA_SET)) {
                    /*according to the classification of the data we store it as 0 or 1*/
                    switch (reader.getClassificationsTest().get(i)) {
                        case "2":
                            result = 0.0;
                            break;
                        case "4": 
                            result = 1.0;
                            break;
                        default:
                            break;
                    }
            }
            else if(fileName.equals(MachineLearning.HEART_DATA_SET)) {
                    /*according to the classification of the data we store it as 0 or 1*/
                    switch (reader.getClassificationsTest().get(i)) {
                        case "1":
                            result = 0.0;
                            break;
                        case "2": 
                            result = 1.0;
                            break;
                        default:
                            break;
                    }
            }
            else
                result = Double.parseDouble(reader.getClassificationsTest().get(i));
                   
            double[] currAttributes = new double[numOfAttributes];
            for(j = 0; j < numOfAttributes; j++) {
                currAttributes[j] = Double.parseDouble(testAttributes.get(i).get(j));
            }
            
            newExample = new Example(currAttributes,result);
            testData.add(newExample);
        }
        
    }
////////////////////////////////////////////////////////////////////////////////
    public void getTrainExamples(int percentageOfTrainData) throws Exception {
        int i, number;
        Random generator;
        /*we will numOfTest data elements*/
        double numOfData = (double)((double)percentageOfTrainData / (double)100) * (double) totalNumberOfTrainExamples;
        
        numOfData = Math.floor(numOfData);
        
        /*we check that the requested ammount of data is at least equal to the data we have in
         the dataset if not we throw the correct exception*/
        if((int)numOfData > allTrainData.size())
            numOfData = allTrainData.size();
        
        
        for(i = 0; i < numOfData; i++) {
            generator = new Random();
            number = generator.nextInt(allTrainData.size());
            
            trainData.add(allTrainData.get(number));
            allTrainData.remove(number);
        }
    }
////////////////////////////////////////////////////////////////////////////////
    /*this function will return the results of the algorithm on the test data.*/
    public double[] getAlgorithmResultsTest() {
        return algorithmResultsTest;
    }
////////////////////////////////////////////////////////////////////////////////
    /*--------------Print functions for debugging--------------*/
    public void printTrainingData() {
        int i,j;
        System.out.println("Printing training data:");
        for(i = 0; i < trainData.size(); i++) {
            System.out.print("Row "+i+ " ");
            for(j = 0; j < trainData.get(i).getAttributesValues().length; j++) {
                System.out.print(trainData.get(i).getAttributesValues()[j]+ " ");
            }
            System.out.print(trainData.get(i).getResult() + "\n");
        }
    }
    
    public void printTestData() {
        int i,j;
        System.out.println("Printing test data:");
        for(i = 0; i < testData.size(); i++) {
            System.out.print("Row "+i+ " ");
            for(j = 0; j < testData.get(i).getAttributesValues().length; j++) {
                System.out.print(testData.get(i).getAttributesValues()[j]+ " ");
            }
            System.out.print(testData.get(i).getResult() + "\n");
        }
    }
////////////////////////////////////////////////////////////////////////////////
    /*this function will be used to do the normalization of the trainData.*/
    public void normalizeTrainData() {
        ArrayList<double[]> attributesCol = new ArrayList<>();
        int i,j;
        /*we make each collumn of the trainData ArrayList a row in our new
         ArrayList that will be used for the normalization. The total size
         of the ArrayList will be as much as the number of attributes we got*/
        for(i = 0; i < numOfAttributes; i++) {
            /*each row will have size as the trainData*/
            double[] valuesOfAttribute = new double[allTrainData.size()];
            for(j = 0; j < allTrainData.size(); j++) {
                valuesOfAttribute[j] = allTrainData.get(j).getAttributesValues()[i];
                
            }
            attributesCol.add(valuesOfAttribute);
        }
        
        /*we initialize the data normalizers with the appropriate row we created*/
        for(i = 0; i < numOfAttributes; i++) {
            DataNormalizer normal = new DataNormalizer(attributesCol.get(i));
            
            normalizers.add(normal);
        }
        
        /*and we pass all train data through the normalize function tha DataNormalizer
         class offers*/
        for(i = 0; i < allTrainData.size(); i++ ) {
            
            for(j = 0; j < numOfAttributes; j++) {
                allTrainData.get(i).getAttributesValues()[j] = normalizers.get(j).normalizeData(allTrainData.get(i).getAttributesValues()[j]);        
            }
        }
    }
////////////////////////////////////////////////////////////////////////////////
    /*this function will set the percentage that is used for training*/
    public void setTrainPercentage(int percentage) {
        trainPercentage = percentage;
    }
////////////////////////////////////////////////////////////////////////////////
    /*this function will write the csv file we have created.*/
    public void writeToCsv() throws IOException {
        out.write(csvData);
        out.flush();
        out.close();
    }
////////////////////////////////////////////////////////////////////////////////
    /*this function will return the example in the position passed as a parameter.*/
    public Example getTrainData(int index){
        return trainData.get(index);
    }
////////////////////////////////////////////////////////////////////////////////
}
