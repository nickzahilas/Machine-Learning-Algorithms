/*
 * This class will simulate the behaviour of a logistic regression classifier.
 */
package machine_learning;

import common.Calculator;
import common.Example;
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
public class LogisticRegression extends MachineLearningAlgorithm implements Classifier {
    /*the weights ArrayList.*/
    private double[] weights;   
    /*the n parameter in the equation.*/
    private double nParameter = 0.001;     
    
    /*the array list with the evaluation examples*/
    private ArrayList<Example> validationExamples;
    
    /*the lamda parameter in the equation  we will use.*/
    private double lamda = 0.5; 
    
    /*we want to normalize the validation examples only once. In the beginning it
     will be false.*/
    private boolean hasValidationBeenNormalized;
    
    /*the same with the above this time for the test examples.*/
    private boolean hasTestBeenNormalized;
    
    /*this writer will be used to write the validation results of the algorithm 
     in a csv file.*/
    private BufferedWriter outValidation;
    
////////////////////////////////////////////////////////////////////////////////
    /*-----------------Constructor-----------------*/
    public LogisticRegression(String fileName,int percentageOfTest) throws FileNotFoundException, IOException, Exception {
        super(fileName,percentageOfTest);
        int i, number, numOfValidationExamples;
        Random generator;
        weights = new double[reader.getNumOfAttrubutes() + 1];
        validationExamples = new ArrayList<>();
       
        numOfValidationExamples = testData.size() / 2;
        
        /*we will use half of the test data as validation*/
        for(i = 0; i < numOfValidationExamples; i++) {
            generator = new Random();
            number = generator.nextInt(testData.size());
            validationExamples.add(testData.get(number));
            testData.remove(number);
        }
        
        /*we normalize the train data*/
        this.normalizeTrainData();
        
        /*in the beginning validation and test examples have not been normalized
         so we make the boolean variable true*/
        hasValidationBeenNormalized = false;
        hasTestBeenNormalized = false;
        
        System.out.println("Number of validation examples "+numOfValidationExamples);
        System.out.println("Number of test examples "+testData.size());
        System.out.println("Number of training examples "+trainData.size());
        
        out = new BufferedWriter(new FileWriter(MachineLearning.LOGISTIC_FILE));
        outValidation = new BufferedWriter(new FileWriter(MachineLearning.LOGISTIC_VALIDATION_FILE));
    }
    /*---------------Basic Methods---------------*/
////////////////////////////////////////////////////////////////////////////////
    /*this method will be used in order to train the logistic regression classifier
     with the training dataset.
     */
    @Override
    public void train() {
        int i,j,iterations = 0;
        double xl;
        double newValue = 0;
        double oldValue = Double.POSITIVE_INFINITY;
        double tolerance = 0.0001;
        double ProbPositive;
        double ProbNegative;
        double sum;
        double sumWeights;
        double weightI;
        
        /*we initialize the weights table with random numbers*/
        Random generate = new Random();
        for(i = 0; i < weights.length; i++) {
            weights[i] = generate.nextDouble();
        }   
        
        while(Math.abs(newValue - oldValue) > tolerance) {
            oldValue = newValue;
            
            for(i = 0; i < weights.length; i++) {
                weightI = weights[i];
                sum = 0;
            
                for(j = 0; j < trainData.size(); j++) {
                    if(i == 0)
                        xl = 1;
                    else
                        xl = trainData.get(j).getAttributesValues()[i - 1];
                    
                    sum = sum + xl * (trainData.get(j).getResult() - Calculator.LogisticFunction(weights,trainData.get(j).getAttributesValues()));
                }
            
                double result = weightI + nParameter * sum - nParameter * lamda * weightI;
                weights[i] = result;
            }
            
            sum = 0;
            
            /*calculating the new value*/
            for(i = 0; i < trainData.size(); i++) {
                ProbPositive = Calculator.LogisticFunction(weights,trainData.get(i).getAttributesValues());
                ProbNegative = 1 - ProbPositive;
                
                sum = sum + trainData.get(i).getResult() * Math.log(ProbPositive) + (1 - trainData.get(i).getResult()) * Math.log(ProbNegative); 
            }
            
            sumWeights = 0.0;
            /*calculating the sum of the weights*/
            for(i = 0; i < weights.length; i++) {
                sumWeights = sumWeights +  Math.pow(weights[i],2.0);
            }
            
            newValue = sum - (lamda * sumWeights)/2;
            iterations++;
        }
        
        System.out.println("Number of iterations until convergence "+iterations);
    }
////////////////////////////////////////////////////////////////////////////////
    /*this method will be used to classify the train examples and then print the
     accuracy we got on them (how well the algorithm classifies the training example)*/
    public void classifyTrainData() {
        csvData += trainPercentage+ ";";
        int i;
        double accuracy;
        double[] algorithmClassification = new double[trainData.size()];
        
        /*we classify each training data*/
        for(i = 0; i < trainData.size(); i++) {
            algorithmClassification[i] = classify(trainData.get(i).getAttributesValues());
        }
        
        System.out.println("Classifying the training data set with Logistic Regression");
        /*and then use the appropriate function from the statistics class to
         get the accuracy of the algorithm.*/
        Evaluation statistic = new Evaluation(true);
        accuracy = statistic.accuracy(algorithmClassification, trainData);
        
        csvData += (accuracy * 100)+ ";";
        csvData += ((1 - accuracy) * 100)+ ";";
    }
////////////////////////////////////////////////////////////////////////////////
    /*this method will do the classification of the test data set.*/
    @Override
    public void test() {
        int i, j;
        double accuracy;
        
        for(i = 0; i < testData.size(); i++) {
           
            /*if the test examples have not been normalized...*/
            if(!hasTestBeenNormalized) {
                /*we normalize the test example*/
                for(j = 0; j < numOfAttributes;j++) {
                    testData.get(i).getAttributesValues()[j] = normalizers.get(j).normalizeData(testData.get(i).getAttributesValues()[j]);
                }
            }
            
            /*and we classify this example*/
            algorithmResultsTest[i] = classify(testData.get(i).getAttributesValues());
        }
        
        /*the next the function will be called it will not re-normalize the examples*/
        hasTestBeenNormalized = true;
        
        System.out.println("Classifying the test data set with Logistic Regression");
        /*after all the test examples have been classified we call the appropriate
         function to check the statistics*/
        Evaluation statistic = new Evaluation(true);
        accuracy = statistic.accuracy(algorithmResultsTest, testData);
        
        csvData += (accuracy * 100)+ ";";
        csvData += ((1 - accuracy) * 100)+ "\n";
    }
////////////////////////////////////////////////////////////////////////////////
    /*this function will classify the example passed as parameter.*/
    @Override
    public double classify(double[] testExample) {
        double prediction;
        
        /*and we use the logistic function to find the possibility the category
         will be one. If the prediction is larger than 0.5 then the category
         will be 1 else the category will be zero*/
        prediction = Calculator.LogisticFunction(weights,testExample);
        if(prediction > 0.5)
            return 1.0;
        else
            return 0.0;
    }
////////////////////////////////////////////////////////////////////////////////
    /*this method will be used for the choosing of the lamda parameter that is
     used in the training phase. We train the algorithm and then check how well he
     * is doing it in the evaluation examples we choose the lamda that does it 
     * better.
     */
    public void validation() throws IOException {
        
        int i,j = 0, maxAccuracy = 0, maxAccuracyPosition = 0, k;
        int[] numberOfAccuratePredictions = new int[5];
        
        double prediction;
        double accuracy;
     
        for(lamda = 0.1; lamda < 0.6; lamda = lamda + 0.1) {
            numberOfAccuratePredictions[j] = 0;
            train();
            for(i = 0; i < validationExamples.size(); i++) {
                
                /*if the validation examples have not been normalized we normalize them*/
                if(!hasValidationBeenNormalized) {
                    /*we normalize the evaluation example*/
                    for(k = 0; k < numOfAttributes; k++) {
                        validationExamples.get(i).getAttributesValues()[k] = normalizers.get(k).normalizeData(validationExamples.get(i).getAttributesValues()[k]);
                    }
                    
                }
                
                prediction  = classify(validationExamples.get(i).getAttributesValues());
                
                if(prediction == validationExamples.get(i).getResult()) {
                    numberOfAccuratePredictions[j]++;
                }
            }
            
            /*it will be change so only the first time the validation examples wlll 
             be normalized*/
            hasValidationBeenNormalized = true;
            
            j++;
        }
        
        /*this is the string that will be stored in the validations.csv*/
        String toStore = "";
        //toStore += ""+trainPercentage+";"+percentageOfValidation+";";
        System.out.println("");
        for(i = 0; i < 5; i++) {
            accuracy = ((double)numberOfAccuratePredictions[i] / (double)validationExamples.size()) * 100;
            toStore += trainPercentage+ ";" +((i+1) * 0.1)+";"+accuracy+ "\n";
            
            System.out.println("Number of accurate predictions with lamda equals to "+((i+1) * 0.1)+ " " +numberOfAccuratePredictions[i] + " from "+validationExamples.size());
            System.out.println("Accuracy is "+accuracy+ "%");
            if(maxAccuracy < numberOfAccuratePredictions[i]) {
                maxAccuracyPosition = i;
                maxAccuracy = numberOfAccuratePredictions[i];
            }
        }
        
        
        /*we store the results to the file we have for this reason*/
        outValidation.write(toStore);
        outValidation.flush();
        
        lamda = 0.1 * (maxAccuracyPosition + 1);
        
        System.out.println("Best classification of evaluation data set when lamda equals to "+lamda);
    }
////////////////////////////////////////////////////////////////////////////////
    /*this function will be called in order to close the file we are writing the
     validation results.*/
    public void closeValidationFile() throws IOException {
        outValidation.close();
    }
}