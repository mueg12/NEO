package com.neo.back.docker.service;

import java.io.Serializable;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import com.neo.back.docker.dto.StartGameServerDto;
import com.neo.back.docker.entity.DockerServer;
import com.neo.back.docker.repository.DockerServerRepository;

import reactor.core.publisher.Mono;

@Service
public class StartAndStopGameServerService {
        private final DockerServerRepository dockerServerRepo;
    private final WebClient.Builder webClientBuilder;
    private WebClient dockerWebClient;

    public StartAndStopGameServerService(WebClient.Builder webClientBuilder, DockerServerRepository dockerServerRepo) {
        this.dockerServerRepo = dockerServerRepo;
        this.webClientBuilder = webClientBuilder;
    }
    
    public ExchangeFilterFunction logRequestAndResponse() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            System.out.println("Request: " + clientRequest.method() + " " + clientRequest.url());
            clientRequest.headers().forEach((name, values) ->
                    values.forEach(value -> System.out.println(name + ": " + value)));
            return Mono.just(clientRequest);
        }).andThen(ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            System.out.println("Response: Status code " + clientResponse.statusCode());
            clientResponse.headers().asHttpHeaders().forEach((name, values) ->
                    values.forEach(value -> System.out.println(name + ": " + value)));
            return Mono.just(clientResponse);
        }));
    }

    private Mono<String> execCmdInst(String dockerId,Map<String, Serializable> getfileAndFolder) {
        return dockerWebClient.post()
        .uri("/containers/"+ dockerId +"/exec")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(getfileAndFolder))
        .retrieve()
        .bodyToMono(String.class)
        .flatMap(fileAndFolder -> Mono.fromCallable(() -> {
            return fileAndFolder;
        }));
    }
    
    private Mono<String> execCmd(String CmdInstId) {
        var MapFile = Map.of(
            "Detach", false,
            "Tty", true,
            "ConsoleSize", new int[]{80,64}
        );
        return dockerWebClient.post()
        .uri("/exec/"+ CmdInstId +"/start")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(MapFile))
        .retrieve()
        .bodyToMono(String.class)
        .flatMap(fileAndFolder -> Mono.fromCallable(() -> {
            return fileAndFolder;
        }));
    }

    private String parseExecInstanceId(String response) {
        JSONObject jsonObject = new JSONObject(response);
        return jsonObject.getString("Id");
    }

    public StartGameServerDto getStartGameServer() {
        DockerServer dockerServer = dockerServerRepo.findByUser(null);
        String ip = dockerServer.getEdgeServer().getIp();
        String dockerId = dockerServer.getDockerId();
        int memory = dockerServer.getRAMCapacity();
        StartGameServerDto startGameServerDto = new StartGameServerDto();
        this.dockerWebClient =  this.webClientBuilder.baseUrl("http://" + ip +":2375").filter(logRequestAndResponse()).build();
        
        String[] setMeoStr = new String[]{"sh","-c","echo 'java,-Xmx" + memory + "G,-jar,/server/craftbukkit-1.20.4.jar' >  meomory.txt"};
        //nohup java -Xmx2G -jar craftbukkit-1.20.4.jar &
        var setMeo = Map.of(
            "AttachStdin", false,
            "AttachStdout", true,
            "AttachStderr", true,
            "DetachKeys", "ctrl-p,ctrl-q",
            "Tty", false,
            "Cmd", setMeoStr,
            "Env", new String[]{"FOO=bar", "BAZ=quux"}
        );
        Mono<String> setMeoMesInst = execCmdInst(dockerId,setMeo)
        .flatMap(response -> Mono.fromCallable(() -> {
            return response;
        }));

        execCmd(parseExecInstanceId(setMeoMesInst.block())).block();

        String[] startStr = new String[]{"sh","-c","echo 'start' > input.txt"};
        //nohup java -Xmx2G -jar craftbukkit-1.20.4.jar &
        var startGameServer = Map.of(
            "AttachStdin", false,
            "AttachStdout", true,
            "AttachStderr", true,
            "DetachKeys", "ctrl-p,ctrl-q",
            "Tty", false,
            "Cmd", startStr,
            "Env", new String[]{"FOO=bar", "BAZ=quux"}
        );
        Mono<String> startInst = execCmdInst(dockerId,startGameServer)
        .flatMap(response -> Mono.fromCallable(() -> {
            return response;
        }));
        execCmd(parseExecInstanceId(startInst.block())).block();

        String[] startAck = new String[]{"sh","-c","/server/start.sh"};
        var startackIns = Map.of(
            "AttachStdin", false,
            "AttachStdout", true,
            "AttachStderr", true,
            "DetachKeys", "ctrl-p,ctrl-q",
            "Tty", false,
            "Cmd", startAck,
            "Env", new String[]{"FOO=bar", "BAZ=quux"}
        );
        
        Mono<String> startAckInst = execCmdInst(dockerId,startackIns)
        .flatMap(response -> Mono.fromCallable(() -> {
            return response;
        }));

        String[] ACK = execCmd(parseExecInstanceId(startAckInst.block())).block().split("\\n");
        startGameServerDto.setIsWorking(ACK[0].equals("startAck"));
        return startGameServerDto;
    }

    public StartGameServerDto getStopGameServer() {
        DockerServer dockerServer = dockerServerRepo.findByUser(null);
        String ip = dockerServer.getEdgeServer().getIp();
        String dockerId = dockerServer.getDockerId();
        int memory = dockerServer.getRAMCapacity();
        StartGameServerDto startGameServerDto = new StartGameServerDto();
        this.dockerWebClient =  this.webClientBuilder.baseUrl("http://" + ip +":2375").filter(logRequestAndResponse()).build();

        String[] stopStr = new String[]{"sh","-c","echo 'input stop' > input.txt"};
        //nohup java -Xmx2G -jar craftbukkit-1.20.4.jar &
        var stopGameServer = Map.of(
            "AttachStdin", false,
            "AttachStdout", true,
            "AttachStderr", true,
            "DetachKeys", "ctrl-p,ctrl-q",
            "Tty", false,
            "Cmd", stopStr,
            "Env", new String[]{"FOO=bar", "BAZ=quux"}
        );
        Mono<String> stopInst = execCmdInst(dockerId,stopGameServer)
        .flatMap(response -> Mono.fromCallable(() -> {
            return response;
        }));
        execCmd(parseExecInstanceId(stopInst.block())).block();

        String[] stopAck = new String[]{"sh","-c","/server/stop.sh"};
        var stopAckIns = Map.of(
            "AttachStdin", false,
            "AttachStdout", true,
            "AttachStderr", true,
            "DetachKeys", "ctrl-p,ctrl-q",
            "Tty", false,
            "Cmd", stopAck,
            "Env", new String[]{"FOO=bar", "BAZ=quux"}
        );
        
        Mono<String> stopAckInst = execCmdInst(dockerId,stopAckIns)
        .flatMap(response -> Mono.fromCallable(() -> {
            return response;
        }));

        String[] ACK = execCmd(parseExecInstanceId(stopAckInst.block())).block().split("\\n");
        startGameServerDto.setIsWorking(ACK[0].equals("stopAck"));
        return startGameServerDto;
    }
}
