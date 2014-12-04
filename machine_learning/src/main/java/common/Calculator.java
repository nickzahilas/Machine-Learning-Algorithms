package common;


/*this class will help us do the neccessary calculations in our other classes.*/
public class Calculator {
    
    /*the inner product of two vectors.*/
    public static double innerProduct(double[] weights,double[] attributes) {
        /*the weights vector has one more element so the sum will be initialized
         as weights[0]*/
        double sum = weights[0];
        int i;
        
        /*we calculate the inner product, we start from 1 because we already
         taken care of the first element of the weights vector, attributes will
         be i - 1 for the same reason*/
        for(i = 1; i < weights.length; i++) {
            
            sum = sum + weights[i] * attributes[i - 1];
            
        }
        
        /*finally we return the sum*/
        return (double)sum;
    }
    
    /*the logistic function (sigmoid function).*/
    public static double LogisticFunction(double[] weights,double[] attributes) {
        
        double result;
        /*we calculate first the inner product of the two vectors*/
        double product = Calculator.innerProduct(weights, attributes);  
        
        /*the sigmoid function is (1/(1 + exp(-inner product)))*/
        result = (double)(1/((double)1 + Math.exp(-product)));
        
        /*finally we return the result*/
        return result;
    }

    /*the function that will calculate distance of two vectors.*/
    public static double distance(double[] attributes1,double[] attributes2) {
       double result = 0;
       int i;
    
       /*we calculate the distance using the appropriate equation */
       for (i = 0 ; i < attributes1.length; i++){
           result= result + Math.pow(attributes1[i]-attributes2[i],2);
       }
             
       result = Math.sqrt(result);
       /*finally we return the result*/
       return result;        
    }
}