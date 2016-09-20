package com.ge.predix.solsvc.websocket.server.endpoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 
 * @author predix -
 */
@ServerEndpoint(value = "/tvCtrl/{nodeId}")
@Component
public class PredixWebSocketServerEndPoint {
	private static Logger logger = LoggerFactory.getLogger(PredixWebSocketServerEndPoint.class);
	//private static CopyOnWriteArraySet<PredixWebSocketServerEndPoint> webSocketSet = new CopyOnWriteArraySet<>();
	private Session session;
	private static HashMap <String,PredixWebSocketServerEndPoint> webSocketMap= new HashMap<>();
	/**
	 * @param nodeId1
	 *            - nodeId for the session
	 * @param session
	 *            - session object
	 * @param ec
	 *            -
	 * @throws IOException 
	 */
	@OnOpen  
    public void onOpen (@PathParam(value = "nodeId") String nodeId,Session session) throws IOException{
        this.session = session;
        if(nodeId.equals("user")){
        	String eqmtNames="";
        	if(webSocketMap.size()>0){
        		for (String key : webSocketMap.keySet()) {
        			eqmtNames=key+"$"+eqmtNames;
        			
        	    }
        	}
        	this.session.getBasicRemote().sendText(eqmtNames);
        }
        else{
        	if(!nodeId.equals("")){
        		webSocketMap.put(nodeId,this);
        	}
        }
       
    }  
  
    /**
     *  -
     */
    @OnClose  
    public void onClose (){
    	Iterator it = webSocketMap.keySet().iterator();
    	while (it.hasNext()) {
    		String key = (String)it.next();
    		if(webSocketMap.get(key).equals(this)){
    			webSocketMap.remove(key); 
    		}
    	}
    }  
  
    /**
     * @param message
     * @param session
     * @throws IOException -
     */
    @OnMessage  
    public void onMessage (String message, Session session) throws IOException {   
    	if(!message.equals("")&&message.contains("#")){
    		String eqmtName=message.split("#")[0];
    		String msgText=message.split("#")[1];
    		 for ( String key : webSocketMap.keySet()){  
                  if(eqmtName.equals(key)){
                	  webSocketMap.get(key).session.getBasicRemote().sendText(msgText);
                  }
    		 	}  
    		}
       
    } 
    /**
     * @param message
     * @throws IOException -
     */
    public void sendMessage (String message) throws IOException {  
        this.session.getBasicRemote().sendText(message);  
    }  
}
