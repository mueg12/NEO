package com.neo.back.server.service;

import com.neo.back.control_edge.service.EdgeServer;
import com.neo.back.control_edge.service.SSHService;
import com.neo.back.server.repository.EdgeServerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SelectEdgeServerService {

	private final EdgeServerRepository edgeRepo;

	@Value("#{'${edgeservers.ip}'.split(',')}")private List<String> hostsTest;
	@Value("#{'${edgeservers.id}'.split(',')}")private List<String> IDsTest;
	@Value("#{'${edgeservers.user.id}'.split(',')}")private List<String> usersTest;
	@Value("#{'${edgeservers.password}'.split(',')}")private List<String> passwordsTest;

    public String selectingEdgeServer(){

		SSHService sshService = new SSHService();

		EdgeServer selecting = sshService.selectingEdgeServer(hostsTest,IDsTest,usersTest,passwordsTest);
		if(selecting != null){
			System.out.println("selectingEdgeServer of "+selecting.getEdgeServerID());
			return edgeRepo.findByEdgeServerName(selecting.getEdgeServerID()).getIp();
		}
		else{
			System.out.println("selectingEdgeServer of NULL");
			return null;
		}
	}
}