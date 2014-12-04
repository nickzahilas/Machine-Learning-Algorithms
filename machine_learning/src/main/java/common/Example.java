/*
 * This class will help us store the information of each example.
 */
package common;

/**
 *
 * @author Nikos Zacheilas
 */
public class Example {
    private double[] attributesValues;
    private double result;
    
    public Example(double[] attributesValues, double result) {
        this.attributesValues = attributesValues;
        this.result = result;
    }
    
    public double[] getAttributesValues() {
        return attributesValues;
    }
    
    public double getResult() {
        return result;
    }
    
    public void setResult(double result) {
        this.result = result;
    }
    
    public boolean equals(Example ex){
        for(int i=0; i<this.getAttributesValues().length; i++){
            if(this.getAttributesValues()[i]!=ex.getAttributesValues()[i]){
                return false;
            }
        }
        if(ex.getResult()!=this.getResult())
            return false;
        return true;
    }
}
