package com.neo.back.docker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.neo.back.docker.dto.EdgeServerCmdDTO;
import com.neo.back.docker.dto.EdgeServerInfoDTO;
import com.neo.back.docker.entity.EdgeServerEntity;
import java.lang.reflect.Field;

@Service
public class GetEdgeServerService {

    private final SshService sshService;

    @Autowired
    public GetEdgeServerService(SshService sshService) {
        this.sshService = sshService;
    }

    public EdgeServerInfoDTO changeEdgeServerEntityTODTO(EdgeServerEntity edgeServer) {
        EdgeServerInfoDTO edgedgeServerDTO = new EdgeServerInfoDTO(edgeServer.getEdgeServerName());
        double memoryIdle = edgeServer.getMemoryTotal() - edgeServer.getMemoryUse();
        Session session = null;
        String portCMD = "";
        try {
            
            EdgeServerCmdDTO cmd = sshService.getDataFromJson();
            Class<?> clazz = cmd.getClass();
            Field[] fields = clazz.getDeclaredFields();

            portCMD = sshService.getCMDPort(fields,cmd);
            session = sshService.getJsch(edgeServer.getIp(), edgeServer.getUser(), edgeServer.getPassWord());

            sshService.getLinesByCMDPortWithChannel(edgedgeServerDTO, session, portCMD, fields);
            sshService.selectPort(edgedgeServerDTO);
            edgedgeServerDTO.setMemoryUse(edgeServer.getMemoryUse());
            edgedgeServerDTO.setMemoryIdle(memoryIdle);
            edgedgeServerDTO.setIP(edgeServer.getIp());

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
        return edgedgeServerDTO;
    }

    
}
