package rest_calls;

import java.util.HashMap;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class Session{
	String id;
	HashMap<String,String> ipIdMap = new HashMap<>();
	
	/*
	 * Erstellen einer Session und weiterleiten zu einer leeren
	 * Seite mit SessionID
	 */
	@RequestMapping("session/creat")
	public ModelAndView creatSession(HttpServletRequest request){
		id = UUID.randomUUID().toString();
		ipIdMap.put(id,request.getRemoteAddr());
		return new ModelAndView("redirect:"+id);
	}
	@RequestMapping("session/{sessionID}")
	public String session(@PathVariable(value="sessionID")String sessionID){
		if(ipIdMap.containsKey(sessionID)){
			return "Gültige Session";
		}
		return "Ungültige Session";
	}
	/*
	 * Platzhalter für mögliche aktionen 
	 */
	@RequestMapping("session/{sessionID}/wastun")
	public String wasTun(@PathVariable(value="sessionID")String sessionID){
		if(sessionPruefen(sessionID)){
			return "platzhalter";
		}
		//System.out.println("wasTun: "+ipIdMap.toString());
		return "Ungültige Session";
	}
	/*
	 * Löschen der aktuellen Session
	 */
	@RequestMapping("session/{sessionID}/delete")
	public String delete(@PathVariable(value="sessionID")String sessionID){
		//System.out.println("delete: "+ipIdMap.toString());
		if(sessionPruefen(sessionID)){
			ipIdMap.remove(sessionID);
			return json_msg(202,"Erfolgreich gelöscht");
		}
		return json_msg(404,"fehler");
	}
	
	/*
	 * Zum überprüfen der SessionID ob sie Gültig ist
	 */
	public boolean sessionPruefen(String session){
		if(ipIdMap.containsKey(session)){
			return true;
		}
		return false;
	}
	
	
	private String json_msg (int code, String msg){
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
