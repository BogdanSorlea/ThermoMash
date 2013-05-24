/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package thermomash;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Bogdan
 */
public class __NetworkAttachRequestProcessor extends __RequestProcessor {

    @Override
    public Map<String, String> process(BufferedReader in) {
        
        HashMap<String, String> result = new HashMap<>();
        
        result.put(breakConditionKey, breakConditionFalse);
        result.put(responseStringKey, Settings.NETWORK_ATTACH_REQ);
        
        return result;
    }    
    
}
