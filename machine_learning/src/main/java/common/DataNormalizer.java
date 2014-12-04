/*this class will be used to do all the normalization necessary to our attributes.*/
package common;
/**
 *
 * @author Nick Zacheilas
 */
public class DataNormalizer {
    double[] values;
    double maxValue;
    double minValue;
    double middleValue;
    double rangeOfValues;
    
    /*--------------Constructor--------------*/
    public DataNormalizer(double[] values) {
        int i;
        
        this.values = values;
        
        /*finding larger and minimum values*/
        maxValue = this.values[0];
        minValue = this.values[0];
        
        for(i = 0; i < this.values.length; i++) {
            if(maxValue < this.values[i])
                maxValue = this.values[i];
            
            if(minValue > this.values[i])
                minValue = this.values[i];
        }
        
        /*we calculate the middle value of this attribute*/
        middleValue = (maxValue + minValue) / 2;
        
        /*and the range of values for this attribute*/
        rangeOfValues = Math.abs(maxValue - minValue);
    }
////////////////////////////////////////////////////////////////////////////////    
    /*this function will be called to normalize the value of an attribute.*/
    public double normalizeData(double value) {
        
        double result;
        
        /*if the value given is a new one that we have not yet seen that is lower
         than the min value we assign it to the min value and procceed with the
         normalization*/
        if(value < minValue)
            value = minValue;
        
        /*the same thing happens if the value is larger than max value we have
         already found*/
        if(value > maxValue)
            value = maxValue;
        
        /*we normalize the result according to the range of values and its 
         distance from the  middle value*/
        if(rangeOfValues != 0)
            result = Math.abs((value - minValue)) / rangeOfValues;
        else
            result = 0.0;
       
        
        return result;
    }
////////////////////////////////////////////////////////////////////////////////
}
