package com.neo.back.control_edge;

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
    
    public EdgeServer getDataOfEdgeServer(String host, String user, String password, String filePath){
        
        EdgeServer edgeServer = new EdgeServer(); 
        ObjectMapper objectMapper = new ObjectMapper();
        Session session = null;
        String command = "";

        try {
            ClassPathResource jsonFile = new ClassPathResource("edgeServer/control_edgeCmd.json");
            EdgeServerCmd cmd = objectMapper.readValue(jsonFile.getInputStream(), EdgeServerCmd.class);

            // print of cmd
            // System.out.println(cmd.getCpu());
            // System.out.println(cmd.getMemoryFree());
            // System.out.println(cmd.getMemoryTotal());
            // System.out.println(cmd.getStorageAvailable());
            // System.out.println(cmd.getStorageTotal());

            Class<?> clazz = cmd.getClass();
            Field[] fields = clazz.getDeclaredFields();

            for(Field field : fields){
                field.setAccessible(true); // 필드의 접근성 설정
                command += (String) field.get(cmd) + " ; "; // 변수의 값 가져오기
            }

            // System.out.println();
            // System.out.println(command);
            // System.out.println();

            JSch jsch = new JSch();
            session = jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            InputStream commandOutput = channel.getInputStream();
            channel.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(commandOutput));
            String line;
            Map<String, Double> lines = new HashMap<>();
            
            for(int i = 0;(line = reader.readLine()) != null;i++){
                Field field = fields[i];
                // System.out.println(line); // 출력 로그로 출력
                // System.out.println(field.getName()); // 출력 로그로 출력
                // System.out.println(line.getClass()); // 출력 로그로 출력
                lines.put(field.getName(), Double.valueOf(line)) ;
            }

            setCpu(edgeServer, lines);
            setMemory(edgeServer, lines);
            setStorage(edgeServer, lines);
            
            // System.out.println("테스트입니다." + edgeServer.getCpuUse());
            // System.out.println("테스트입니다." + edgeServer.getCpuIdle());
            logger.info("테스트입니다." + edgeServer.getCpuUse());
            logger.info("테스트입니다." + edgeServer.getCpuIdle());

            // System.out.println("테스트입니다." + edgeServer.getMemoryUse());
            // System.out.println("테스트입니다." + edgeServer.getMemoryIdle());
            // System.out.println("테스트입니다." + edgeServer.getMemoryUsePercent()+ " a " + edgeServer.getMemoryIdlePercent());
            logger.info("테스트입니다." + edgeServer.getMemoryUse());
            logger.info("테스트입니다." + edgeServer.getMemoryIdle());
            logger.info("테스트입니다." + edgeServer.getMemoryUsePercent()+ " a " + edgeServer.getMemoryIdlePercent());

            // System.out.println("테스트입니다." + edgeServer.getStorageUse());
            // System.out.println("테스트입니다." + edgeServer.getStorageIdle());
            // System.out.println("테스트입니다." + edgeServer.getStorageUsePercent()+ " a " + edgeServer.getStorageIdlePercent());
            
            logger.info("테스트입니다." + edgeServer.getStorageUse());
            logger.info("테스트입니다." + edgeServer.getStorageIdle());
            logger.info("테스트입니다." + edgeServer.getStorageUsePercent()+ " a " + edgeServer.getStorageIdlePercent());
            
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
}