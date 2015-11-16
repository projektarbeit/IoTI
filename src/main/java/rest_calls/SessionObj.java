package rest_calls;

import java.time.LocalDateTime;

import org.json.JSONException;
import org.json.JSONObject;

public class SessionObj {
	private String ip;
	private int port;
	private LocalDateTime time;
	
	public SessionObj(String ip,int port){
		this.ip = ip;
		this.port = port;
		time = LocalDateTime.now();
	}
	public String getIp(){return ip;}
	public int getPort(){return port;}
	public LocalDateTime getTime(){return time;}
	
	public String toJSON(){
		JSONObject obj = new JSONObject();
		try {
			obj.put("IP: ",ip);
			obj.put("Port: ", port);
			obj.put("Zeit: ", time);
			
		} catch (JSONException e) {
			System.out.println("ERROR: parsing JSON in class (UserDeviceMapper)");
			return "{\"code\":500,\"message\":\"Internal Server Error\"}";
		}
    	return obj.toString();
	}
}
