/*
 *This class will be used to get and store the data from the files we will read.
 */
package common;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 *
 * @author Nick
 */
public class DataReader {
    /*the number of attributes we will have.*/
    private int numOfAttributes = 0;
    
    /*the values of the attributes will be stored in this form*/
    private ArrayList<ArrayList<String>> attributesValues;
    
    /*the values of the attributes in the train data*/
    private ArrayList<ArrayList<String>> trainData;
    
    /*the values of the attributes in the test data*/
    private ArrayList<ArrayList<String>> testData;
    
    /*the last collumn of the data sets will be the classification*/
    private ArrayList<String> classifications; 
    
    /*the classifications of the test data*/
    private ArrayList<String> classificationsTest;
    
    /*the classifications of the train data*/
    private ArrayList<String> classificationsTrain;
    
    /*the file from which we will read the data*/
    private String fileName = null;

	private BufferedReader in;
    
    /*-----------Constructor-----------*/
    public DataReader(String fileName) {
        /*we do the neccessary initializations*/
        this.fileName = fileName;
        classifications = new ArrayList<>();
        attributesValues = new ArrayList<>();
        classificationsTest = new ArrayList<>();
        trainData = new ArrayList<>();
        testData = new ArrayList<>();
        classificationsTrain = new ArrayList<>();
    }
    
    /*-----------------Accessors-----------------*/
    public ArrayList<ArrayList<String>> getAttributesValues() {
        return attributesValues;
    }
    
    public ArrayList<String> getClassifications() {
        return classifications;
    }
    
    public int getNumOfAttrubutes() {
       return numOfAttributes;
    }
    
    public ArrayList<String> getClassificationsTest() {
        return classificationsTest;
    }
    
    public ArrayList<String> getClassificationsTrain() {
        return classificationsTrain;
    }
    /*-----------------Basic Methods-----------------*/
////////////////////////////////////////////////////////////////////////////////
    /*parser that reads the data from the file and stores them as strings*/
    public void readData() throws FileNotFoundException, IOException, Exception {
        
        in = new BufferedReader(new FileReader(fileName)); 
        String str = in.readLine();
        
        /*the character used to split the lines in the file into different attributes*/
        String splitCharacter;
        
        /*the number of rows we read*/
        int i;
        
        /*according to teh ending of the file we choose the appropriate split 
         character
         */
        if(fileName.endsWith(".txt") || fileName.endsWith(".data") || fileName.endsWith(".train") || fileName.endsWith(".test"))
            splitCharacter = ",";
        else
            splitCharacter = ";";
        
        /*while we have a line to read...*/
        while(str!=null) {
            /*we split the line using the appropriate split character*/
            String[] splittedString = str.split(splitCharacter);
           
            /*if the line is not split we throw the appropriate exception*/
            if(splittedString.length == 0)
                throw new Exception("Problem with the data set file given");
            
            /*if the numOfAttributes is not assigned we initialize it by getting
             the length of the split table we have just created*/
            if(numOfAttributes == 0)
                numOfAttributes = splittedString.length - 1;
            
            /*if the numOfAttributes are lower than one we have problem because
             we have not the neccessary number of attributes*/
            if(numOfAttributes < 1) 
                throw new Exception("Wrong file must have at least one attribute");
            
            /*the last attribute of the split table will be the classifier so
             we add it in the correct ArrayList*/
            classifications.add(splittedString[splittedString.length - 1]);
            
            /*we also put the other elements of the split array in the attributes
             ArrayList*/
            ArrayList<String> tempStringList = new ArrayList<>();
            for(i = 0; i < numOfAttributes; i++) {
                tempStringList.add(splittedString[i]);
            }
            attributesValues.add(tempStringList);
            
            /*and we read the next line*/
            str = in.readLine();
        }       
    }
////////////////////////////////////////////////////////////////////////////////    
    /*this method will return the train (or evaluation) data.*/
    public  ArrayList<ArrayList<String>> getPercentageData(int percentageOfTrain) throws Exception {
        double numOfData = (double)((double)percentageOfTrain / (double)100) * (double) attributesValues.size();
        
        /*we check that the requested ammount of data is at least equal to the data we have in
         the dataset if not we throw the correct exception*/
        if((int)numOfData > attributesValues.size())
            throw new Exception("You have requested more data than the number of data in the dataset!");
        
        int i;
        ArrayList<ArrayList<String>> dataToReturn = new ArrayList<>();
        
        /*and add elements to it until we have inserted the neccessary ammount
         of training data*/
        for(i = 0; i < numOfData; i++) {
            dataToReturn.add(attributesValues.get(0));
            classificationsTrain.add(classifications.get(i));
            attributesValues.remove(0);
        }
        
        /*we return tha train data*/
        return dataToReturn;
    }
////////////////////////////////////////////////////////////////////////////////
    /*this method will return the requested ammount of test data.*/
    public ArrayList<ArrayList<String>> getTestData(int numOfTestData) throws Exception {
        
        /*we check that the requested ammount of data is at least equal to the data we have in
         the dataset if not we throw the correct exception*/
        if(numOfTestData > attributesValues.size() - 1)
            throw new Exception("You have requested more test data than the number of data in the dataset!");
        
        int i = 0;
        /*we start from the last data in the data set and continue adding them to the
         test set until we have reached the requested number*/
        while(i < numOfTestData) {
            
            testData.add(attributesValues.get(attributesValues.size() - 1 ));
            classificationsTest.add(classifications.get(attributesValues.size() - 1));
            
            /*we remove the data that will be used in the test set from the other data
             because they will be the possible train set*/
            attributesValues.remove(attributesValues.size() - 1);
            classifications.remove(attributesValues.size() - 1);
            i++;
        }
        
        /*finally we return the test data*/
        return testData;
    }
////////////////////////////////////////////////////////////////////////////////
    /*this function will return how many unique categories we have in case of 
     classification problem.
     */
	public int getNumOfUniqueCategories() {
        /*we find the different categories*/
        ArrayList<String> diffClassifications = new ArrayList<String>(new 
        		HashSet<String>(classifications));
        
        /*and return their ammount*/
        return diffClassifications.size();
    }
////////////////////////////////////////////////////////////////////////////////
    /*this function will return random training data according to the variable
     passed as parameter.
     */
    public ArrayList<ArrayList<String>> getRandomTrainingData(int numOfData) {
        int i;
        
        for(i = 0; i <numOfData; i++) {
            Random generator = new Random();

            /*we generate a random number, that will be the example in the
             array list we will take*/
            int number = generator.nextInt(attributesValues.size() - 1);
            
            /*we add the classification of the example*/
            classificationsTrain.add(classifications.get(i));
            
            /*and add the values of the example attributes in the train data*/
            trainData.add(attributesValues.get(number));
            attributesValues.remove(number);
        }
        
        /*we return the train examples*/
        return trainData;
    }
////////////////////////////////////////////////////////////////////////////////
    /*this function will return the test data, it will choose random elements from the attributes values
     table and add them to the test data we will return to the classifier.
     */
    public ArrayList<ArrayList<String>> generateRandomTestData(int percentageOfTestData) throws Exception {
        int i,number;
        /*we will numOfTest data elements*/
        double numOfTestData = (double)((double)percentageOfTestData / (double)100) * (double) attributesValues.size();
        
        /*we check that the requested ammount of data is at least equal to the data we have in
         the dataset if not we throw the correct exception*/
        if(numOfTestData > attributesValues.size() - 1)
            throw new Exception("You have requested more test data than the number of data in the dataset!");
        
        Random generator;
        
        /*we will numOfTest data elements*/
        for(i = 0; i <numOfTestData; i++) {
            /*we generate a random number (position in attributes array)*/
            generator = new Random();
            number = generator.nextInt(attributesValues.size() - 1);
            
            /*we add the corresponding values and classification*/
            testData.add(attributesValues.get(number));
            classificationsTest.add(classifications.get(number));
            
            attributesValues.remove(number);
            classifications.remove(number);
        }
        
        /*finally we return the test data*/
        return testData;
    }
////////////////////////////////////////////////////////////////////////////////
    /*this function will return the test data, it will choose random elements from the attributes values
     table and add them to the test data we will return to the classifier.
     */
    public ArrayList<ArrayList<String>> generateRandomTrainData(int percentageOfTrainData) throws Exception {
        int i,number;
      
        Random generator;
        
        /*we will numOfTest data elements*/
        double numOfData = (double)((double)percentageOfTrainData / (double)100) * (double) attributesValues.size();
        
        /*we check that the requested ammount of data is at least equal to the data we have in
         the dataset if not we throw the correct exception*/
        if((int)numOfData > attributesValues.size())
            throw new Exception("You have requested more data than the number of data in the dataset!");
        
        for(i = 0; i < numOfData; i++) {
            /*we generate a random number*/
            generator = new Random();
            number = generator.nextInt(attributesValues.size());
            /*we add the corresponding values and classification*/
            trainData.add(attributesValues.get(number));
            classificationsTrain.add(classifications.get(number));
            
            attributesValues.remove(number);
            classifications.remove(number);
        }
        
        /*finally we return the test data*/
        return trainData;
    }
////////////////////////////////////////////////////////////////////////////////
}