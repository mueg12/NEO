package com.neo.back.config;

import com.neo.back.docker.entity.EdgeServer;
import com.neo.back.docker.repository.EdgeServerRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class test {
	private final EdgeServerRepository edgeServerRepository;

	@Value("#{'${edgeservers.ip}'.split(',')}")
	private List<String> hostsTest;

	@Value("#{'${edgeservers.id}'.split(',')}")
	private List<String> IDsTest;

	public test(EdgeServerRepository edgeServerRepository) {
		this.edgeServerRepository = edgeServerRepository;
	}

	@PostConstruct
	private void init() {
		EdgeServer edgeServer = new EdgeServer();
		edgeServer.setEdgeServerName(IDsTest.get(0));
		edgeServer.setIp(hostsTest.get(0));
		edgeServerRepository.save(edgeServer);
		edgeServer.setEdgeServerName(IDsTest.get(1));
		edgeServer.setIp(hostsTest.get(1));
		edgeServerRepository.save(edgeServer);
	}


}
