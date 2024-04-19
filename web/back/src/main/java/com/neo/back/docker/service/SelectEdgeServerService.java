package com.neo.back.docker.service;

import com.neo.back.control_edge.service.EdgeServer;
import com.neo.back.control_edge.service.selectEdgeServer;
import com.neo.back.docker.dto.EdgeServerInfoDTO;
import com.neo.back.docker.entity.EdgeServerEntity;
import com.neo.back.docker.repository.EdgeServerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

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
	public synchronized EdgeServerInfoDTO selectingEdgeServer(double UserMemory){
		List<EdgeServerEntity> allEdgeServers = edgeServerInfoTEST.findAll();
		List<EdgeServerInfoDTO> edgedgeServerDTO =  new ArrayList<>();;
		EdgeServerInfoDTO selecteEdgeServer = null;
        double memoryLimit = UserMemory; // 향후 개인 사용자가 사용할 Memory의 최소 기준 MiB

        int edgeServermemoryLeft = 1000; // 엣지 서버의 시스템 메모리 공간 할당

		for(EdgeServerEntity edgeServer : allEdgeServers){
			EdgeServerInfoDTO edgeServerInfoDTO = getEdgeServerService.changeEdgeServerEntityTODTO(edgeServer);
			// if()

		}
        // for(int Index = 0 ; Index < edgeServerNumber ; Index++){
        //     String hostIndex = host.get(Index);
        //     String IDIndex = ID.get(Index);
        //     String userIndex = user.get(Index);
        //     String passwordIndex = password.get(Index);
        //     System.out.println(Index);
        //     EdgeServer tmp = makeDatas.getDataOfEdgeServer(hostIndex,userIndex,passwordIndex,IDIndex);
        //     if( cpuLimit < tmp.getCpuIdle() && storageLimit < tmp.getStorageIdle() && memoryLimit < tmp.getMemoryIdle()){
        //         edgeServers.add(tmp);
        //     }
        // }

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
        // Collections.sort(edgeServers, Comparator.comparingDouble(EdgeServer::getMemoryIdle));
        // for(EdgeServer edgeServer : edgeServers){
        //     // edgeServers의 내부 엣지서버 데이터들이 오름차순인지 확인
        //     // System.out.println(edgeServer.getMemoryIdle());
        //     if(edgeServermemoryLeft < edgeServer.getMemoryIdle() - memoryLimit){
        //         selecteEdgeServer = edgeServer;
        //         return selecteEdgeServer;
        //     }
        // }
        return selecteEdgeServer;
    }


}