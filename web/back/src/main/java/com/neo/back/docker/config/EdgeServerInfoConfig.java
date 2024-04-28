package com.neo.back.docker.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.neo.back.docker.entity.EdgeServer;
import com.neo.back.docker.entity.Game;
import com.neo.back.docker.repository.EdgeServerRepository;
import com.neo.back.docker.repository.GameRepository;

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
    @Autowired
    GameRepository gameRepo;

	@PostConstruct
	private void init() {
		EdgeServer edgeServer = new EdgeServer();
        int edgeServerNumber = edgeServerIp.size();
		for(int index = 0; index < edgeServerNumber ; index++){
            edgeServer.setEdgeServerName(edgeServerName.get(index));
            edgeServer.setIp(edgeServerIp.get(index));
            edgeServer.setUser(edgeServerUser.get(index));
            edgeServer.setPassWord(edgeServerPassword.get(index));
            edgeServer.setMemoryTotal(Integer.parseInt(edgeServerMemoryTotal.get(index)));
            edgeServer.setMemoryUse(Integer.parseInt(edgeServerMemoryUse.get(index)));
            edgeServerInfo.save(edgeServer);
        }

        Game game = new Game();
        game.setGame("mc1.16.5");
        game.setDockerImage("mc1.16.5");
        game.setDefaultSetting(null);
        gameRepo.save(game);
        game.setGame("mc1.19.2");
        game.setDockerImage("mc1.19.2");
        gameRepo.save(game);
        game.setGame("mc1.20.4");
        game.setDockerImage("mc1.20.4");
        gameRepo.save(game);
	}
}
