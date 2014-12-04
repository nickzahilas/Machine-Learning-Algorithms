/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package machine_learning;

import common.Evaluation;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author Juliana Litou
 */
public class Adaboost extends MachineLearningAlgorithm implements Classifier {

    private double[][] w;
    private double[][] h;
    private double[] z;
    private double[] category;
    private NaiveBayes classifier;
    private int iterations = 1;
    private int numOfTestData;
    
    public Adaboost( String filename, int percentageOfTestData, int iterations, String classifiersName) throws FileNotFoundException, IOException, Exception {
        super(filename, percentageOfTestData);
        //initialization of classifier
        if (classifiersName.equalsIgnoreCase("NaiveBayes")) {
            classifier = new NaiveBayes(filename, numOfTestData);
            classifier.setTestData(testData);
        } else {
            System.out.print("Classifier not supported by Adaboost");
            return;
        }
        this.iterations = iterations;
    }

    //training of the algorithm so as to get the appropriate weights of iterations
    @Override
    public void train() {
        classifier.setTrainData(trainData);
        classifier.initialization();
        
        for (int i = 0; i < iterations; i++) {
            //fit weights for the next iterations
            if(i>0)
            {
                for(int j=0; j<trainData.size(); j++){
                    w[i][j]=w[i-1][j];
                }
            }
            //set weights to the cassifier
            classifier.setWeights(w[i]);
            //classify examples with the given weights
            for (int data = 0; data < trainData.size(); data++) {
                h[i][data] = classifier.classify(trainData.get(data).getAttributesValues());
            }
            double mistake=0.00001;
            //calculate mistake
            for (int j = 0; j < trainData.size(); j++) {
                if(h[i][j]!=trainData.get(j).getResult()){
                    mistake+=w[i][j];
                }
            }
            //claculate new weights
            for(int j=0; j<trainData.size(); j++){
                if(h[i][j]==trainData.get(j).getResult())
                    w[i][j]*=mistake/(1.0 - mistake);
            }
            
            //normalize weights to add up to 1
            double sum=0.0;
            for(int j=0; j<trainData.size(); j++){
                sum+=w[i][j];
            }
            for(int j=0; j<trainData.size(); j++){
                w[i][j]=w[i][j]/sum;
               
            }
            mistake=mistake/trainData.size();
            //set weiht of itearation
            double log=(1.0-mistake)/mistake;
            z[i]=Math.log(log);
        }
        //calculate category of train data based on majority
        weighted_majority();
    }

    //Na rwthsw ta paidia
    public void weighted_majority(){
        for(int j=0; j<trainData.size(); j++){
            double div=0.0000001;
            category[j]=0;
            for(int i=0; i<iterations; i++){
                category[j]+=h[i][j]*z[i];
                div+=z[i];
            }
            category[j]/=div;
                
            
            category[j]=(category[j])>0.5 ? 1.0 : 0.0;
            
        }
    }
    
    //test the testData classification
    @Override
    public void test() {
        double[] results = new double[testData.size()];
        for(int i=0; i<testData.size(); i++){
            results[i]=classify(testData.get(i).getAttributesValues());
        }
        Evaluation statistic = new Evaluation(true);
        statistic.accuracy(results, testData);
    }

    //classify the example according to weights after training (considering majority as in the train examples)
    @Override
    public double classify(double[] testExample) {
       double cluster[]=new double[iterations];
       for(int i=0; i<iterations; i++){
           classifier.setWeights(w[i]);
           cluster[i]=classifier.classify(testExample);
       }
       double div=0;
       double categoryOfExample=0.0;
       for(int i=0; i<iterations; i++){
                categoryOfExample+=cluster[i]*z[i];
                div+=z[i];
            }
            categoryOfExample/=div;
                
           return (categoryOfExample)>0.5 ? 1.0 : 0.0;
    }

    //initialize variables
    public void initialize() {
        w = new double[iterations][trainData.size()];
        z=new double[iterations];
        category=new double[trainData.size()];
        for (int i = 0; i < trainData.size(); i++) {
            w[0][i] = 1.0/ trainData.size();
        }
        h = new double[iterations][trainData.size()];
    }
}
