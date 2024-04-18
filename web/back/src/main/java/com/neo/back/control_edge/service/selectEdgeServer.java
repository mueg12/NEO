package com.neo.back.control_edge.service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class selectEdgeServer {
    public synchronized EdgeServer selectingEdgeServer(List<String> host,List<String> ID,List<String> user,List<String> password ,double UserMemory){
        int edgeServerNumber = ID.size();
        List<EdgeServer> edgeServers = new ArrayList<>();
        EdgeServer selecteEdgeServer = null; 
        getDataEdgeServer makeDatas = new getDataEdgeServer();

        int cpuLimit = 10; // 향후 개인 사용자가 사용할 Cpu의 최소 기준 퍼센트
        double memoryLimit = UserMemory; // 향후 개인 사용자가 사용할 Memory의 최소 기준 MiB
        int storageLimit = 3000; 

        int edgeServermemoryLeft = 1000; // 엣지 서버의 시스템 메모리 공간 할당
        // 모든 edgeServer에 대한 데이터를 list 형태로 구조화 + 생성하고 남는 램이 edgeServermemoryLeft 이상 남아있어야 한다.
        // + 사용자의 최소 기준을 넘어야한다.
        //  -> 엣지 서버 시스템 메모리를 위한것

        for(int Index = 0 ; Index < edgeServerNumber ; Index++){
            String hostIndex = host.get(Index);
            String IDIndex = ID.get(Index);
            String userIndex = user.get(Index);
            String passwordIndex = password.get(Index);
            System.out.println(Index);
            EdgeServer tmp = makeDatas.getDataOfEdgeServer(hostIndex,userIndex,passwordIndex,IDIndex);
            if( cpuLimit < tmp.getCpuIdle() && storageLimit < tmp.getStorageIdle() && memoryLimit < tmp.getMemoryIdle()){
                edgeServers.add(tmp);
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
        Collections.sort(edgeServers, Comparator.comparingDouble(EdgeServer::getMemoryIdle));
        for(EdgeServer edgeServer : edgeServers){
            // edgeServers의 내부 엣지서버 데이터들이 오름차순인지 확인
            // System.out.println(edgeServer.getMemoryIdle());
            if(edgeServermemoryLeft < edgeServer.getMemoryIdle() - memoryLimit){
                selecteEdgeServer = edgeServer;
                return selecteEdgeServer;
            }
        }
        return selecteEdgeServer;
    }
}
