package com.neo.back.control_edge.service;

import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import java.lang.reflect.Field;

@Configuration
public class SSHService {
    
    private static final Logger logger = LoggerFactory.getLogger(SSHService.class);
    
    // Map으로 관리된 각각의 엣지 서버의 데이터를 실제 EdgeServer 클래스에 넣는 함수
    private void setCpuEdgeServer(EdgeServer ES, Map<String, Double> lines){
        ES.setCpuUse(100 - lines.get("cpu"));
        ES.setCpuIdle( lines.get("cpu") );
    }

    private void setMemoryEdgeServer(EdgeServer ES, Map<String, Double> lines){
        ES.setMemoryUse(lines.get("memoryTotal")-lines.get("memoryFree"));
        ES.setMemoryIdle(lines.get("memoryFree"));
    }
    
    private void setStorageEdgeServer(EdgeServer ES, Map<String, Double> lines){
        ES.setStorageUse(lines.get("storageTotal") - lines.get("storageAvailable"));
        ES.setStorageIdle( lines.get("storageAvailable") );
    }

    private void setPortEdgeServer(EdgeServer edgeServer, BufferedReader reader, List<String> lines) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        edgeServer.setPortUses(lines);
    }

    private String getCMDExceptPort(Field[] fields_tmp,EdgeServerCmd cmd_tmp) throws IllegalArgumentException, IllegalAccessException, StreamReadException, DatabindException, IOException{
        String cmdTotalExceptPort = "";
        fields_tmp[5].setAccessible(true);
        String portCMD = (String)fields_tmp[5].get(cmd_tmp);
        for(Field field : fields_tmp){
            field.setAccessible(true);
            if(portCMD.equals((String) field.get(cmd_tmp))){
            }
            else{
                cmdTotalExceptPort += (String) field.get(cmd_tmp) + " ; "; 
            }
        }
        return cmdTotalExceptPort;
    }
    private String getCMDPort(Field[] fields_tmp, EdgeServerCmd cmd_tmp) throws IllegalArgumentException, IllegalAccessException, StreamReadException, DatabindException, IOException{
        fields_tmp[5].setAccessible(true);
        String portCMD = (String)fields_tmp[5].get(cmd_tmp);
        return portCMD;
    }

    private Session getJsch(String host, String user, String password) throws JSchException {
        Session session;
        JSch jsch = new JSch();
        session = jsch.getSession(user, host, 22);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        return session;
    }

    private Map<String, Double> getLines(Field[] fields, BufferedReader reader) throws IOException {
        Map<String, Double> lines = new HashMap<>();
        String line;
        for(int i = 0; (line = reader.readLine()) != null;i++){
            Field field = fields[i];
            lines.put(field.getName(), Double.valueOf(line)) ;
        }
        return lines;
    }

    private void getLinesByCMDExceptPortWithChannel(EdgeServer edgeServer, Session session, String command, Field[] fields)
                                throws JSchException, IOException {
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);
        InputStream commandOutput = channel.getInputStream();
        channel.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(commandOutput));
        Map<String, Double> lines = getLines(fields, reader);
        
        setCpuEdgeServer(edgeServer, lines);
        setMemoryEdgeServer(edgeServer, lines);
        setStorageEdgeServer(edgeServer, lines);

        reader.close();
        channel.disconnect();
    }


    private void getLinesByCMDPortWithChannel(EdgeServer edgeServer, Session session, String command, Field[] fields) 
                                                throws JSchException, IOException {
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);
        InputStream commandOutput = channel.getInputStream();
        channel.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(commandOutput));
        List<String> lines =  new ArrayList<>();
        setPortEdgeServer(edgeServer, reader, lines);

        reader.close();
        channel.disconnect();
    }
    
    public static List<Integer> convertStringListToIntList(List<String> stringList) {
        List<Integer> integerList = new ArrayList<>();
        for (String str : stringList) {
            integerList.add(Integer.parseInt(str));
        }
        return integerList;
    }

    private int selectRandomNumber(int minRange, int maxRange, List<Integer> integerList) {
        Random random = new Random();
        int randomNumber;

        do {
            randomNumber = random.nextInt(maxRange - minRange + 1) + minRange;
        } while (integerList.contains(randomNumber));

        return randomNumber;
    }

    private void selectPort(EdgeServer edgeServer) {
        List<Integer> integerList = convertStringListToIntList(edgeServer.getPortUses());
        int minRange = 0;
        int maxRange = 25565;
        int randomNumber = selectRandomNumber(minRange, maxRange, integerList);
        edgeServer.setPortSelect(randomNumber);
    }



    public EdgeServer getDataOfEdgeServer(String host, String user, String password, String ID){
        EdgeServer edgeServer = new EdgeServer(ID); 
        Session session = null;
        String command = "";
        String portCMD = "";
        ObjectMapper objectMapper = new ObjectMapper();
        String edgeCmdControlPath = "edgeServer/control_edgeCmd.json";
        ClassPathResource jsonFile = new ClassPathResource(edgeCmdControlPath);
        try {
            EdgeServerCmd cmd = objectMapper.readValue(jsonFile.getInputStream(), EdgeServerCmd.class);
            Class<?> clazz = cmd.getClass();
            Field[] fields = clazz.getDeclaredFields();

            command = getCMDExceptPort(fields,cmd);
            portCMD = getCMDPort(fields,cmd);
            // System.out.println(portCMD);
            // System.out.println("test");
            // System.out.println(command);
            session = getJsch(host, user, password);

            getLinesByCMDExceptPortWithChannel(edgeServer, session, command, fields);
            getLinesByCMDPortWithChannel(edgeServer, session, portCMD, fields);
            selectPort(edgeServer);
            
            logger.info(edgeServer.getEdgeServerID() +" portUses 테스트 " + edgeServer.getPortUses());
            logger.info(edgeServer.getEdgeServerID() +" portSelect 테스트 " + edgeServer.getPortSelect());

            logger.info(edgeServer.getEdgeServerID() +" CPU USE 테스트 " + edgeServer.getCpuUse());
            logger.info(edgeServer.getEdgeServerID() +" CPU IDLE 테스트 " + edgeServer.getCpuIdle());

            logger.info(edgeServer.getEdgeServerID() +" MEMORY USE 테스트 " + edgeServer.getMemoryUse());
            logger.info(edgeServer.getEdgeServerID() +" MEMORY IDLE 테스트 " + edgeServer.getMemoryIdle());
            logger.info(edgeServer.getEdgeServerID() +" MEMORY PERCENT 테스트 " + edgeServer.getMemoryUsePercent()+ " & " + edgeServer.getMemoryIdlePercent());
        
            logger.info(edgeServer.getEdgeServerID() +" STORAGE USE 테스트 " + edgeServer.getStorageUse());
            logger.info(edgeServer.getEdgeServerID() +" STORAGE IDLE 테스트 " + edgeServer.getStorageIdle());
            logger.info(edgeServer.getEdgeServerID() +" STORAGE PERCENT 테스트 " + edgeServer.getStorageUsePercent()+ " & " + edgeServer.getStorageIdlePercent());

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

    public synchronized EdgeServer selectingEdgeServer(List<String> host,List<String> ID,List<String> user,List<String> password){
        int edgeServerNumber = ID.size();
        List<EdgeServer> edgeServers = new ArrayList<>();
        EdgeServer selecteEdgeServer = null; 

        int cpuLimit = 10; // 향후 개인 사용자가 사용할 Cpu의 최소 기준 퍼센트
        int memoryLimit = 2000; // 향후 개인 사용자가 사용할 Memory의 최소 기준 MiB
        int storageLimit = 3000; // 향후 개인 사용자가 사용할 Storage의 최소 기준 MiB

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
            EdgeServer tmp = getDataOfEdgeServer(hostIndex,userIndex,passwordIndex,IDIndex);
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