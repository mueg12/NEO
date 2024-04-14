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

    public synchronized EdgeServer selectingEdgeServer(){
        int edgeServerNumber = Integer.parseInt(System.getProperty("naver.edgeserver.number"));
        Map<String, EdgeServer> edgeServers = new HashMap<>();

        int cpuLimit = 10; // 향후 개인 사용자가 사용할 Cpu의 최소 기준
        int memoryLimit = 10; // 향후 개인 사용자가 사용할 Memory의 최소 기준
        int storageLimit = 1000; // 향후 개인 사용자가 사용할 Storage의 최소 기준

        // 모든 edgeServer에 대한 데이터를 Map 형태로 구조화
        for(int i = 1 ; i < edgeServerNumber + 1 ;i++){
            String host = System.getProperty("naver.edgeserver." + i + ".ip");
            String ID = System.getProperty("naver.edgeserver." + i + ".id");
            String user = System.getProperty("naver.edgeserver." + i + ".user.id");
            String password = System.getProperty("naver.edgeserver." + i + ".password");	
            edgeServers.put("edgeServer" + i, getDataOfEdgeServer(host,user,password,ID));

            // System.out.println(System.getProperty("naver.edgeserver." + i + ".ip"));
            // System.out.println(System.getProperty("naver.edgeserver." + i + ".id"));
            // System.out.println(System.getProperty("naver.edgeserver." + i + ".user.id"));
            // System.out.println(System.getProperty("naver.edgeserver." + i + ".password"));
        }

        /*
         * 모든 edgeServer에 대한 데이터를 기반으로 선정 알고리즘 실행
         * 현 상황에서는 각각의 edgeServer에 들어가서 위의 사용자가 사용할 최소 기준을 만족하면,
         * 서버를 개설시키도록 한다.
         * 만약 최소 기준을 만족하는게 없다면, NULL값을 리턴한다. 
         */
        for(Map.Entry<String, EdgeServer> entry : edgeServers.entrySet()){
            String key = entry.getKey(); // 현재 순회 중인 엔트리의 키
            EdgeServer edgeServer = entry.getValue(); // 현재 순회 중인 엔트리의 값
            logger.info("checking of appropriate EdgeServer" + key);
            if(cpuLimit < edgeServer.getCpuIdle() && memoryLimit < edgeServer.getMemoryIdle() && storageLimit < edgeServer.getStorageIdle()){
                return edgeServer;
            }
        }

        return null;
    }
}