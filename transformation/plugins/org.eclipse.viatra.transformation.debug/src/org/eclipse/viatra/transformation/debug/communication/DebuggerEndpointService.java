package org.eclipse.viatra.transformation.debug.communication;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

//This is TEMP AF
//Will be replaced by a proper MQ implementation
public class DebuggerEndpointService {
    private static DebuggerEndpointService instance;
    
    
    private Map<String, IDebuggerTargetEndpoint> targetEndpoints;
    private Map<String, IDebuggerHostEndpoint> hostEndpoints;
    
    public static DebuggerEndpointService getInstance() {
        if(instance == null){
            instance = new DebuggerEndpointService();
        }
        return instance;
    }
    
    protected DebuggerEndpointService(){
        targetEndpoints = Maps.newHashMap();
        hostEndpoints = Maps.newHashMap();
    }
    
    
    
    public void registerTargetEndpoint(String ID, IDebuggerTargetEndpoint endpoint){
        if(!targetEndpoints.containsKey(ID)){
            targetEndpoints.put(ID, endpoint);
        }
    }
      
    public void unRegisterTargetEndpoint(String ID){
        targetEndpoints.remove(ID);
    }
    
    public void registerHostEndpoint(String ID, IDebuggerHostEndpoint endpoint){
        if(!hostEndpoints.containsKey(ID)){
            hostEndpoints.put(ID, endpoint);
        }
    }
    
    public void unRegisterHostEndpoint(String ID){
        hostEndpoints.remove(ID);
    }
    
    public IDebuggerTargetEndpoint getTargetEndpoint(String ID){
        return targetEndpoints.get(ID);
    }
    
    public IDebuggerHostEndpoint getHostEndpoint(String ID){
        return hostEndpoints.get(ID);
    }

    public Set<IDebuggerTargetEndpoint> getTargetEndpoints() {
        return Sets.newHashSet(targetEndpoints.values());
    }

    public Set<IDebuggerHostEndpoint> getHostEndpoints() {
        return Sets.newHashSet(hostEndpoints.values());
    }
    
    
    
}
