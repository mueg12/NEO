package com.neo.back;

import com.neo.back.server.entity.EdgeServer;
import com.neo.back.server.repository.EdgeServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
// import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class BackApplication {
//	private final EdgeServerRepository edgeServerRepository;
//
//	@Value("#{'${edgeservers.ip}'.split(',')}")private List<String> hostsTest;
//	@Value("#{'${edgeservers.id}'.split(',')}")private List<String> IDsTest;
//
//    public BackApplication(EdgeServerRepository edgeServerRepository) {
//        this.edgeServerRepository = edgeServerRepository;
//    }


    public static void main(String[] args) {

//		EdgeServer edgeServer = new EdgeServer();
//		edgeServer.setEdgeServerName(IDsTest.get(0));
//		edgeServer.setIp(hostsTest.get(0));
//		edgeServerRepository.save(dockerServer);
//		edgeServer.setEdgeServerName(IDsTest.get(1));
//		edgeServer.setIp(hostsTest.get(1));
//		edgeServerRepository.save(dockerServer);
		// Dotenv dotenv = Dotenv.load();
        // dotenv.entries().forEach(entry -> {
        //     // .env 파일의 키를 Spring 프로퍼티 명명 규칙에 맞게 변환
        //     // 예: SPRING_DATASOURCE_URL -> spring.datasource.url
        //     String propName = entry.getKey().toLowerCase().replace('_', '.');
        //     System.setProperty(propName, entry.getValue());
        // });
		SpringApplication.run(BackApplication.class, args);
	}

}
