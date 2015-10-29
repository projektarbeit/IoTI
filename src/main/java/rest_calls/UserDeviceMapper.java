package rest_calls;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.LinkedList;
import org.json.JSONException;
import org.json.JSONObject;

@RestController
public class UserDeviceMapper {
	
	private HashMap<Long, LinkedList<Long>> user_deviceID = new HashMap<Long,LinkedList<Long>>();
	
	/* Erstellen eines neuen Nutzers mit der UID
	 * Bsp: http://localhost:8080/newuser?uid=42
	 */
    @RequestMapping("/newuser")
    public String newuser(@RequestParam(value="uid") String uid) {
    	long uidl;
    	LinkedList<Long> uid_ec = new LinkedList<Long>();
    	
    	try{
    		uidl = Long.valueOf(uid).longValue();
    	} catch (NumberFormatException e) { 
    		return err_msg(400,"Wrong number format (long) of " + uid);
    	}
    	
    	uid_ec = user_deviceID.get(uidl);
    	
    	if (uid_ec == null) {
    		LinkedList<Long> tmp = new LinkedList<Long>();
    		user_deviceID.put(uidl, tmp);
    	} else {
    		return err_msg(400,"uid already in use.");
    	}
    	
    	return "1";
    }
    
   @RequestMapping(method = RequestMethod.GET)
   public String defaultfunc() {
	   
	   return "Error";
	   
   }
    
   private String err_msg (int code, String msg){
    	
    	JSONObject obj = new JSONObject();
    	try {
			obj.put("code",code);
			obj.put("message",msg);
		} catch (JSONException e) {
			System.out.println("ERROR: parsing JSON in class (UserDeviceMapper)");
			return "{\"code\":500,\"message\":\"Internal Server Error\"}";
		}
    	return obj.toString();
    }
    
}
