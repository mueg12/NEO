package com.neo.back.control_edge;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.neo.back.docker.dto.EdgeServerInfoDTO;
import com.neo.back.docker.entity.EdgeServerEntity;
import com.neo.back.docker.repository.EdgeServerRepository;
import com.neo.back.docker.service.GetEdgeServerService;
import com.neo.back.docker.service.SelectEdgeServerService;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class GetEdgeServerInfoTest {

    @Autowired
	private EdgeServerRepository edgeServerInfoTEST;

    @Autowired
	GetEdgeServerService getEdgeServer;

	@Autowired
	SelectEdgeServerService selectEdgeServer;

	@Test
	public void getEdgeServerInfoFromDatabase(){
		List<EdgeServerEntity> allEdgeServers = edgeServerInfoTEST.findAll();
		int index = 0;
		for(EdgeServerEntity edgeServer : allEdgeServers){
			System.out.println("EdgeServer Datas of Number " + index++);
			System.out.println(edgeServer.getEdgeServerName());
			System.out.println(edgeServer.getUser());
			System.out.println(edgeServer.getPassWord());
			System.out.println(edgeServer.getIp());
			System.out.println(edgeServer.getMemoryTotal());
			System.out.println(edgeServer.getMemoryUse());
			System.out.println("\n");
		}
	}

	@Test
	public void getEdgeServerByDatabase(){
		List<EdgeServerEntity> allEdgeServers = edgeServerInfoTEST.findAll();
		EdgeServerInfoDTO edgedgeServerDTO = new EdgeServerInfoDTO("test");

		for(EdgeServerEntity edgeServer : allEdgeServers){
			edgedgeServerDTO = getEdgeServer.changeEdgeServerEntityTODTO(edgeServer);
			System.out.println(edgedgeServerDTO.getEdgeServerID());
			System.out.println(edgedgeServerDTO.getMemoryIdle());
			System.out.println(edgedgeServerDTO.getMemoryUse());
			System.out.println(edgedgeServerDTO.getIP());
		}

	}

	@Test
	public void selectEdgeServerByDatabase(){
		EdgeServerInfoDTO selectServer = selectEdgeServer.selectingEdgeServer(8);
		System.out.println(selectServer.getEdgeServerID());
		System.out.println(selectServer.getMemoryUse());
		System.out.println(selectServer.getMemoryIdle());
		// 이은구가 사용해야할 때 부분
		System.out.println(selectServer.getPortSelect());
		System.out.println(selectServer.getIP());
	}
	
}
