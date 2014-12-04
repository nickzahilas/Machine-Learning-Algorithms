/*
 * This class will hold the information of a neighbor and will be used in the 
 * Least Squares algorithm.
 */
package common;

/**
 *
 * @author Nick
 */
public class DistanceKNeighbors {
    /*the distance between the neighbor and the example to be classified, or used
     in the local regression.
     */
    private double distance;
    
    /*the training example from whom we count the distance.*/
    private int trainingExample;
////////////////////////////////////////////////////////////////////////////////    
    /*-----------Constructor-----------*/
    public DistanceKNeighbors(double distance,int trainingExample) {
        this.distance = distance;
        this.trainingExample = trainingExample;
    }
////////////////////////////////////////////////////////////////////////////////
    /*-----------------Accessors-----------------*/
    public int getTrainingExample() {
        return trainingExample;
    }
    
    public double getDistance() {
        return distance;
    }
////////////////////////////////////////////////////////////////////////////////
    /*-----------------Mutators-----------------*/
    public void setDistance(double distance) {
        this.distance = distance;
    }
    
    public void setTrainingExample(int trainingExample) {
        this.trainingExample = trainingExample;
    }
////////////////////////////////////////////////////////////////////////////////
    /*Overriding toString for debugging reasons.*/
    @Override
    public String toString() {
        return "Distance = " + this.distance + " TrainingExample " + this.trainingExample;
    }
////////////////////////////////////////////////////////////////////////////////
}
