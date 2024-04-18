package com.neo.back.control_edge;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;

import com.neo.back.control_edge.service.EdgeServer;
import com.neo.back.control_edge.service.getDataEdgeServer;
import com.neo.back.control_edge.service.selectEdgeServer;
import com.neo.back.config.EnvConfig;


// import io.github.cdimascio.dotenv.Dotenv;


@JsonTest
@ContextConfiguration(classes = {EnvConfig.class})
public class SSHServiceTest {

	@Value("#{'${edgeservers.ip}'.split(',')}")private List<String> hostsTest;
	@Value("#{'${edgeservers.id}'.split(',')}")private List<String> IDsTest;
	@Value("#{'${edgeservers.user.id}'.split(',')}")private List<String> usersTest;
	@Value("#{'${edgeservers.password}'.split(',')}")private List<String> passwordsTest;

	// SSHService 단위 테스트 : 한 엣지 서버에 대한 출력 예시 (디버그를 통해 확인 가능)
    @Test
	void getEdgeServerData(){

		getDataEdgeServer sshService = new getDataEdgeServer();
		int edgeServer_1 = 0;
		sshService.getDataOfEdgeServer(hostsTest.get(edgeServer_1), usersTest.get(edgeServer_1), passwordsTest.get(edgeServer_1), IDsTest.get(edgeServer_1));

	}
	// SSHService 단위 테스트 : 알고리즘에 맞는 엣지 서버 선택 출력 예시 (디버그를 통해 확인 가능)
	@Test
	void selectingEdgeServer(){

		selectEdgeServer selectEdgeServer = new selectEdgeServer();
		double userMemory = 8000;
		EdgeServer selecting = selectEdgeServer.selectingEdgeServer(hostsTest,IDsTest,usersTest,passwordsTest,userMemory);
		if(selecting != null){
			System.out.println("selectingEdgeServer of "+selecting.getEdgeServerID());
		}
		else{
			System.out.println("selectingEdgeServer of NULL");
		}
	}
}
