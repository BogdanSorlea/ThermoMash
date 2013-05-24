/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package thermomash;

import java.io.BufferedReader;
import java.util.Map;

/**
 *
 * @author Bogdan
 */
abstract class __RequestProcessor {
    
    public static String responseStringKey = "RESPONSE_STRING_KEY";
    public static String breakConditionKey = "BREAK_CONDITION_KEY";
    public static String breakConditionTrue = "true";
    public static String breakConditionFalse = "false";
    
    public abstract Map<String, String> process(BufferedReader in);
    
}
