package com.neo.back.docker.service;


import com.neo.back.docker.dto.EdgeServerInfoDTO;
import com.neo.back.docker.entity.EdgeServerEntity;
//import com.neo.back.control_edge.service.EdgeServer;
import com.neo.back.docker.repository.EdgeServerRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;



@Service
// @Transactional
// @RequiredArgsConstructor
public class SelectEdgeServerService {

	private final EdgeServerRepository edgeServerInfoTEST;
	private final GetEdgeServerService getEdgeServerService;

	@Autowired
	public SelectEdgeServerService(EdgeServerRepository edgeServerInfoTEST,GetEdgeServerService getEdgeServerService){
		this.edgeServerInfoTEST = edgeServerInfoTEST;
		this.getEdgeServerService = getEdgeServerService;
	}
	
	@Value("#{'${edgeservers.ip}'.split(',')}")private List<String> hostsTest;
	@Value("#{'${edgeservers.id}'.split(',')}")private List<String> IDsTest;
	@Value("#{'${edgeservers.user.id}'.split(',')}")private List<String> usersTest;
	@Value("#{'${edgeservers.password}'.split(',')}")private List<String> passwordsTest;

    public String selectingEdgeServer(){

		// selectingEdgeServer sshService = new SSHService();

		// EdgeServer selecting = sshService.selectingEdgeServer(hostsTest,IDsTest,usersTest,passwordsTest);
		// if(selecting != null){
		// 	System.out.println("selectingEdgeServer of "+selecting.getEdgeServerID());
		// 	return edgeRepo.findByEdgeServerName(selecting.getEdgeServerID()).getIp();
		// }
		// else{
		// 	System.out.println("selectingEdgeServer of NULL");
		// 	return null;
		// }

		return "223.130.154.221";
	}
	public synchronized EdgeServerInfoDTO selectingEdgeServer(double UserMemory){
		List<EdgeServerEntity> allEdgeServers = edgeServerInfoTEST.findAll();
		List<EdgeServerInfoDTO> edgedgeServerDTO =  new ArrayList<>();;
		EdgeServerInfoDTO selecteEdgeServer = null;

        double edgeServermemoryLeft = 1; // 엣지 서버의 시스템 메모리 공간 할당

		for(EdgeServerEntity edgeServer : allEdgeServers){
			EdgeServerInfoDTO edgeServerInfoDTO = getEdgeServerService.changeEdgeServerEntityTODTO(edgeServer);
			double canUseMemory = edgeServerInfoDTO.getMemoryIdle() - UserMemory - edgeServermemoryLeft;
			if(0 <= canUseMemory){
				edgedgeServerDTO.add(edgeServerInfoDTO);
			}
		}
        /*
         * 모든 edgeServer에 대한 데이터를 기반으로 선정 알고리즘 실행
         * 
         * 항시 지켜야하는 조건 : 생성하고 남는 램이 1GB이상 남아있어야 한다. -> 조건문으로 달성
         * 가장 작은 램(1GB 이상)이 남은데에 할당한다 -> 오름차순에서 확인하면서 달성 
         * 
         * 현 상황에서는 각각의 edgeServer에 들어가서 위의 사용자가 사용할 최소 기준(사용자가 요구한)을 만족하면, -> 위에서 달성
         * 서버를 개설시키도록 한다.
         * 만약 기준을 만족하는게 없다면, NULL값을 리턴한다. 
         */
        Collections.sort(edgedgeServerDTO, Comparator.comparingDouble(EdgeServerInfoDTO::getMemoryIdle));

        for(EdgeServerInfoDTO edgeServer : edgedgeServerDTO){
			selecteEdgeServer = edgeServer;
			return selecteEdgeServer;
        }
		
        return selecteEdgeServer;
    }


}