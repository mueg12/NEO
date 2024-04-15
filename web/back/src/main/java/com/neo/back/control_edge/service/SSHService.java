package com.neo.back.control_edge.service;

import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

import java.lang.reflect.Field;

public class SSHService {


    private static final Logger logger = LoggerFactory.getLogger(SSHService.class);
    // Map으로 관리된 각각의 엣지 서버의 데이터를 실제 EdgeServer 클래스에 넣는 함수
    private void setCpu(EdgeServer ES, Map<String, Double> lines){
        ES.setCpuUse(100 - lines.get("cpu"));
        ES.setCpuIdle( lines.get("cpu") );
    }

    private void setMemory(EdgeServer ES, Map<String, Double> lines){
        ES.setMemoryUse(lines.get("memoryTotal")-lines.get("memoryFree"));
        ES.setMemoryIdle(lines.get("memoryFree"));
    }
    
    private void setStorage(EdgeServer ES, Map<String, Double> lines){
        ES.setStorageUse(lines.get("storageTotal") - lines.get("storageAvailable"));
        ES.setStorageIdle( lines.get("storageAvailable") );
    }
    
    // getDataOfEdgeServer 아큐먼트로 받은 엣지 서버에 대한 Cpu, Memory, Storage를 EdgeServer 클래스화 하여 리턴하는 함수
    /*
     * 데이터를 위해 저장된 명령어를 SSH 접근을 통해 얻고, 이를 EdgeServer에 담아서 리턴한다.
     */
    public EdgeServer getDataOfEdgeServer(String host, String user, String password, String ID){
        
        EdgeServer edgeServer = new EdgeServer(ID); 
        ObjectMapper objectMapper = new ObjectMapper();
        Session session = null;
        String command = "";

        try {
            // ssh 접근해서 데이터를 얻기위한 명령어 저장파일 관련 코드
            ClassPathResource jsonFile = new ClassPathResource("edgeServer/control_edgeCmd.json");
            EdgeServerCmd cmd = objectMapper.readValue(jsonFile.getInputStream(), EdgeServerCmd.class);

            Class<?> clazz = cmd.getClass();
            Field[] fields = clazz.getDeclaredFields();

            for(Field field : fields){
                field.setAccessible(true); // 필드의 접근성 설정
                command += (String) field.get(cmd) + " ; "; // 변수의 값 가져오기
            }

            // System.out.println();
            // System.out.println(command); 해당 명령어가 어떤 식으로 들어가나 확인용
            // System.out.println();

            // ssh 접근을 위한 jsch 연결 코드
            JSch jsch = new JSch();
            session = jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // 연결 채널 열기 & cmd 전송
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            InputStream commandOutput = channel.getInputStream();
            channel.connect();
            // 출력값 가져오기
            BufferedReader reader = new BufferedReader(new InputStreamReader(commandOutput));
            String line;
            Map<String, Double> lines = new HashMap<>();
            // 각각 데이터를 Map 구조를 통해 데이터 구조화
            for(int i = 0;(line = reader.readLine()) != null;i++){
                Field field = fields[i];
                // System.out.println(line); // 출력 로그로 출력
                // System.out.println(field.getName()); // 출력 로그로 출력
                // System.out.println(line.getClass()); // 출력 로그로 출력
                lines.put(field.getName(), Double.valueOf(line)) ;
            }
            // EdgeServer 클래스에 저장
            setCpu(edgeServer, lines);
            setMemory(edgeServer, lines);
            setStorage(edgeServer, lines);
            // 로그를 통해 EdgeServer 저장되었는지 확인
            logger.info(edgeServer.getEdgeServerID() +" CPU USE 테스트 " + edgeServer.getCpuUse());
            logger.info(edgeServer.getEdgeServerID() +" CPU IDLE 테스트 " + edgeServer.getCpuIdle());

            logger.info(edgeServer.getEdgeServerID() +" MEMORY USE 테스트 " + edgeServer.getMemoryUse());
            logger.info(edgeServer.getEdgeServerID() +" MEMORY IDLE 테스트 " + edgeServer.getMemoryIdle());
            logger.info(edgeServer.getEdgeServerID() +" MEMORY PERCENT 테스트 " + edgeServer.getMemoryUsePercent()+ " & " + edgeServer.getMemoryIdlePercent());
        
            logger.info(edgeServer.getEdgeServerID() +" STORAGE USE 테스트 " + edgeServer.getStorageUse());
            logger.info(edgeServer.getEdgeServerID() +" STORAGE IDLE 테스트 " + edgeServer.getStorageIdle());
            logger.info(edgeServer.getEdgeServerID() +" STORAGE PERCENT 테스트 " + edgeServer.getStorageUsePercent()+ " & " + edgeServer.getStorageIdlePercent());
            // 연결 끊기 및 예외처리
            reader.close();
            channel.disconnect();
        } catch (JSchException | java.io.IOException e) {
            e.printStackTrace();
        } catch(IllegalAccessException e){
            e.printStackTrace();
        } catch(NumberFormatException  e){
            e.printStackTrace();
        }
        finally {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }

        return edgeServer; 
    }

    // selectingEdgeServer 향후에 사용자의 서버의 최소 기준을 아큐먼트로 받고,
    /*
     * 현 시점에서 구현한 것은 정해진 최소 기준을 만족하는 엣지 서버를 리턴해주고 없다면 null을 리턴한다.
     * 향후 향상시킬 수 있는 방향
     * 1. 사용자의 빠른 서버 할당을 위해 지속적으로 ssh를 하여 데이터베이스? 에 저장하여 데이터 갖다가 사용하는 방식
     */

    public synchronized EdgeServer selectingEdgeServer(){
        int edgeServerNumber = Integer.parseInt(System.getProperty("edgeserver.number"));
        List<EdgeServer> edgeServers = new ArrayList<>();
        EdgeServer selecteEdgeServer = null; 

        int cpuLimit = 10; // 향후 개인 사용자가 사용할 Cpu의 최소 기준 퍼센트
        int memoryLimit = 2000; // 향후 개인 사용자가 사용할 Memory의 최소 기준 MiB
        int storageLimit = 3000; // 향후 개인 사용자가 사용할 Storage의 최소 기준 MiB

        int edgeServermemoryLeft = 1000; // 엣지 서버의 시스템 메모리 공간 할당
        // 모든 edgeServer에 대한 데이터를 list 형태로 구조화 + 생성하고 남는 램이 edgeServermemoryLeft 이상 남아있어야 한다.
        // + 사용자의 최소 기준을 넘어야한다.
        //  -> 엣지 서버 시스템 메모리를 위한것
        for(int i = 1 ; i < edgeServerNumber + 1 ;i++){
            String host = System.getProperty("naver.edgeserver." + i + ".ip");
            String ID = System.getProperty("naver.edgeserver." + i + ".id");
            String user = System.getProperty("naver.edgeserver." + i + ".user.id");
            String password = System.getProperty("naver.edgeserver." + i + ".password");
            EdgeServer tmp = getDataOfEdgeServer(host,user,password,ID);
            if( cpuLimit < tmp.getCpuIdle() && storageLimit < tmp.getStorageIdle() && memoryLimit < tmp.getMemoryIdle()){
                edgeServers.add(tmp);
            }
            // System.out.println(System.getProperty("naver.edgeserver." + i + ".ip"));
            // System.out.println(System.getProperty("naver.edgeserver." + i + ".id"));
            // System.out.println(System.getProperty("naver.edgeserver." + i + ".user.id"));
            // System.out.println(System.getProperty("naver.edgeserver." + i + ".password"));
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