package com.neo.back.docker.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.neo.back.docker.entity.EdgeServerEntity;
import com.neo.back.docker.repository.EdgeServerRepository;

import jakarta.annotation.PostConstruct;

@Configuration
public class EdgeServerInfoConfig {
    
	@Value("#{'${edgeservers.id}'.split(',')}")private List<String> edgeServerName;
    @Value("#{'${edgeservers.ip}'.split(',')}")private List<String> edgeServerIp;
	@Value("#{'${edgeservers.user.id}'.split(',')}")private List<String> edgeServerUser;
	@Value("#{'${edgeservers.password}'.split(',')}")private List<String> edgeServerPassword;
	@Value("#{'${edgeservers.memoryTotal}'.split(',')}")private List<String> edgeServerMemoryTotal;
	@Value("#{'${edgeservers.memoryUse}'.split(',')}")private List<String> edgeServerMemoryUse;

    @Autowired
    EdgeServerRepository edgeServerInfo;

	@PostConstruct
	private void init() {
		EdgeServerEntity edgeServer = new EdgeServerEntity();
        int edgeServerNumber = edgeServerIp.size();
		for(int index = 0; index < edgeServerNumber ; index++){
            edgeServer.setEdgeServerName(edgeServerName.get(index));
            edgeServer.setIp(edgeServerIp.get(index));
            edgeServer.setUser(edgeServerUser.get(index));
            edgeServer.setPassWord(edgeServerPassword.get(index));
            edgeServer.setMemoryTotal(Double.parseDouble(edgeServerMemoryTotal.get(index)));
            edgeServer.setMemoryUse(Double.parseDouble(edgeServerMemoryUse.get(index)));
            edgeServerInfo.save(edgeServer);
        }
	}
}
