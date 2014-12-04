/*
 * This interface will be implemented by all the classification algorithms in the
 * project.
 */

package machine_learning;

/**
 *
 * @author Nikos Zacheilas
 */
public interface Classifier {
    /*the function that will be used in order to train our algorithm.*/
    public void train();
    
    /*this function will test how well the algorithm goes to the training examples.*/
    public void test();
    
    /*this function will be used in order to classify an example, the table passed
     as argument will be the values of the attributes of the example.
     */
    public double classify(double[] testExample);
    
}
