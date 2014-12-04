/*the basic class in our project the user can choose the algorithm he wants to
 test.
 */
package machine_learning;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 *
 * @author Nick
 */
public class MachineLearning {
    /*-------Basic Definitions-------*/
    public static final int EXIT = 0;
    public static final int LOGISTIC_REGRESSION = 1;
    public static final int LEAST_SQUARES = 2;
    public static final int ADABOOST = 3;
    public static final int NAIVE_BAYES = 4;
    public static final int KNN = 5;
    /*the definitions of the datasets*/
    public static final String BREAST_CANCER_DATA_SET = "src/data/breast-cancer-wisconsin.data";
    public static final String TRANSFUSION_DATA_SET = "src/data/transfusion.data";
    public static final String HOUSING_DATA_SET = "src/data/housing.data";
    public static final String WINE_DATA_SET = "src/data/winequality-red.csv";
    public static final String HEART_DATA_SET = "src/data/heart.data";
    public static final String MESSAGES_DATA_SET = "src/data/messages.txt";
    
    /*the csv file we are creating*/
    public static final String LOGISTIC_FILE = "src/data/logistic.csv";
    public static final String NAIVE_FILE = "src/data/naive.csv";
    public static final String KNN_FILE = "src/data/knn.csv";
    public static final String ADABOOST_FILE = "src/data/adaboost.csv";
    public static final String LEAST_FILE = "src/data/least.csv";
    public static final String LOGISTIC_VALIDATION_FILE = "src/data/logistic_validation.csv";
    
    
////////////////////////////////////////////////////////////////////////////////
    /*the main function of our project from here the user will choose the algorithm
     he wants to test.
     */
    public static void main(String[] args) {
        String line = null,fileName;
        int choice,percentageOfTestData,k,trainPercentage,percentageCounter = 10;
        BufferedReader br;
        System.out.println("0. Exit");
        System.out.println("1. Logistic Regression");
        System.out.println("2. Local Least Squares Regression");
        System.out.println("3. Adaboost");
        System.out.println("4. Naive Bayes");
        System.out.println("5. KNN");
        
        try {
            /*getting the choice of the user*/
            System.out.print("\n" + "Give your choice: ");
            br = new BufferedReader(new InputStreamReader(System.in));
            line = br.readLine();
            choice = Integer.parseInt(line);
            
            if(choice != EXIT && choice != LOGISTIC_REGRESSION && choice != LEAST_SQUARES
               && choice != ADABOOST && choice != NAIVE_BAYES && choice != KNN) {
                System.out.println("Choose a number between 0 and 5");
                return ;
            }
            
            if(choice == EXIT) {
                System.out.println("Stop running...");
                return ; 
            }
            
            /*the user gives the number of test data*/
            System.out.println("Give the percentage of test data");
            br = new BufferedReader(new InputStreamReader(System.in));
            line = br.readLine();
            percentageOfTestData = Integer.parseInt(line);
                   
            /*according to the choice we call the appropriate algorithm*/
            if(choice == LOGISTIC_REGRESSION) {
                
                fileName = MachineLearning.chooseDataSet(true);
                
                /*we create the logistic object*/
                LogisticRegression logistic = new LogisticRegression(fileName,percentageOfTestData);
                
                for(trainPercentage = 10;  trainPercentage <= 100; trainPercentage += percentageCounter) {
                    System.out.println("Testing logistic regression with "+trainPercentage+ "% train examples");
                    logistic.getTrainExamples(percentageCounter);
                    logistic.setTrainPercentage(trainPercentage);
                    /*we evaluate the method to choose the best lamda parameter*/
                    logistic.validation();
                
                    /*we train the algorithm with the train data set after we have chose
                     the lamda parameter*/
                    logistic.train();
                
                    logistic.classifyTrainData();
                    /*we test the algorithm in the test data set*/
                    logistic.test();
                }
                
                logistic.writeToCsv();
                logistic.closeValidationFile();
            }
            else if(choice == ADABOOST) {
                fileName = MachineLearning.chooseDataSet(true);
                Adaboost ad = new Adaboost(fileName,percentageOfTestData, 50, "NaiveBayes");
                for(trainPercentage = 10;  trainPercentage <= 100; trainPercentage += percentageCounter) {
                    
                    ad.getTrainExamples(percentageCounter);
                    ad.setTrainPercentage(trainPercentage);
                    ad.initialize();
                    ad.train();
                    ad.test();
                }
                
                ad.writeToCsv();
            }
            /*if the user chose the least squares algorithm*/
            else if(choice == LEAST_SQUARES)
            {
                fileName = MachineLearning.chooseDataSet(false);
                k = chooseK();
                
                /*we create the least squares object*/
                LeastSquares least = new LeastSquares(fileName,percentageOfTestData,k);
                
                for(trainPercentage = 10;  trainPercentage <= 100; trainPercentage += percentageCounter) {
                    System.out.println("Testing least squares with "+trainPercentage+ "% train examples");
                    /*we get the new training data*/
                    least.getTrainExamples(percentageCounter);
                    least.setTrainPercentage(trainPercentage);
                    
                    least.testOnTrainExamples();
                    /*we test the algorithm in the test data set*/
                    least.test();
                }
                
                least.writeToCsv();
            }
            /*if the user chose the naive bayes classifier*/
            else if(choice == NAIVE_BAYES) {
                
                fileName = MachineLearning.chooseDataSet(true);
                
                /*we create the appropriate object*/
                NaiveBayes naive = new NaiveBayes(fileName,percentageOfTestData);
                
                for(trainPercentage = 10;  trainPercentage <= 100; trainPercentage += percentageCounter) {
                    naive.getTrainExamples(percentageCounter);
                    naive.setTrainPercentage(trainPercentage);
                    naive.initialization();
                    System.out.println("Testing naive bayes with "+trainPercentage+ "% train examples");
                    naive.classifyTrainExamples();
                    naive.test();
                }
                
                naive.writeToCsv();
            }
            /*if tjhe user chose the KNN classifier*/
            else if(choice == KNN) {
                fileName = MachineLearning.chooseDataSet(true);
                k = chooseK();
                
                /*we create the appropriate object*/
                KNN knn = new KNN(fileName,percentageOfTestData,k);
                percentageCounter = 10;
              
                for(trainPercentage = 10;  trainPercentage <= 100; trainPercentage += percentageCounter) {
                    System.out.println("Testing knn with "+trainPercentage+ "% train examples");
                    knn.getTrainExamples(percentageCounter);
                    knn.setTrainPercentage(trainPercentage);
                    
                    knn.classifyTrainExamples();
                    /*and call the classify function*/
                    knn.test();
                }
                
                knn.writeToCsv();
            }   
        }
        catch (FileNotFoundException ex) {
            System.err.println("The file given does not exist!");
            System.err.println(ex.getMessage());
        } 
        catch(NumberFormatException ex) {
            System.err.println("Not a valid number: " + line);
            System.err.println(ex.getMessage());
        }
        catch(IOException ioe) {
            System.err.println("Unexpected IO error: " + ioe);
        }
        catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
////////////////////////////////////////////////////////////////////////////////
    /*this function will display the possible choices of the user according to
     the case the machine learning algorithm is used for classification or
     regression.
     */
    public static String chooseDataSet(boolean classifier) throws IOException, Exception {
        BufferedReader br;
        String line,fileName;
        int dataset;
        /*if we want data sets for classification*/
        if(classifier) {
            System.out.println("Choose the data set you want:");
            System.out.println("0. Breast Cancer");
            System.out.println("1. Blood Transfusion");
            System.out.println("2. Heart");
            System.out.println("3. Messages");
                
            br = new BufferedReader(new InputStreamReader(System.in));
            line = br.readLine();
            dataset = Integer.parseInt(line);
            
            if(dataset != 0 && dataset != 1 && dataset != 2 && dataset != 3) {
                throw new Exception("Give 0, 1, 2 or 3 for the dataset");
            }
                
            /*according to his choice we get the filename of the appropriate data set*/
            if(dataset == 0)
                fileName = BREAST_CANCER_DATA_SET;
            else if(dataset == 1)
                fileName = TRANSFUSION_DATA_SET;
            else if(dataset == 2)
                fileName = HEART_DATA_SET;
            else
                fileName = MESSAGES_DATA_SET;
        }
        /*if we want regression*/
        else {
            /*we display the appropriate menu.*/
            System.out.println("Choose the data set you want:");
            System.out.println("0. Housing");
            System.out.println("1. Wine quality");
                
            br = new BufferedReader(new InputStreamReader(System.in));
            line = br.readLine();
            dataset = Integer.parseInt(line);
            
            if(dataset != 0 && dataset != 1) {
                throw new Exception("Give 0 or 1 for the dataset");
            }
                
            /*according to his choice we get the filename of the appropriate data set*/
            if(dataset == 0)
                fileName = HOUSING_DATA_SET;
            else
                fileName = WINE_DATA_SET;
        }
        
        return fileName;
    }
////////////////////////////////////////////////////////////////////////////////
    /*this function will be used in order the user can choose the ammount of k
     neighbors he wants to get in KNN and Least Squares algorithms*/
    public static int chooseK() throws IOException, Exception {
        int k;
        BufferedReader br;
        String line;
        
        /*we ask him to give the ammount of k neighbors he wants*/
        System.out.println("How many neighbors you want to get for each test data example");
        br = new BufferedReader(new InputStreamReader(System.in));
        line = br.readLine();
        k = Integer.parseInt(line);
                
        /*if he gives negative number we throw an exception*/
        if(k < 0) 
            throw new Exception("Please give positive k");
        
        return k;
    }
////////////////////////////////////////////////////////////////////////////////
}
