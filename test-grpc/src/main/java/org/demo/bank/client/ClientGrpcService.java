package org.demo.bank.client;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.demo.bank.client.dao.ClientService;
import org.demo.bank.client.domain.Client;
import org.demo.bank.client.domain.Gender;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Roman Baygildin (RVBaygildin@sberbank.ru)
 */
@Slf4j
public class ClientGrpcService extends ClientServiceGrpc.ClientServiceImplBase {

    private ClientService service = ClientService.getInstance();

    @Override
    public void createClient(ClientOuterClass.CreateClientReq request, StreamObserver<ClientOuterClass.CreateClientRes> responseObserver) {
        var client = new Client();
        client.setName(request.getPayload().getName());
        client.setSurname(request.getPayload().getSurname());
        client.setGender(request.getPayload().getGender() == ClientOuterClass.Gender.MALE ? Gender.MALE : Gender.FEMALE);
        client.setAge(request.getPayload().getAge());
        client.setEmail(request.getPayload().getEmail());
        var id = service.create(client);
        var res = ClientOuterClass.CreateClientRes.newBuilder()
                .setStatus(ClientOuterClass.Status.newBuilder()
                        .setCode(ClientOuterClass.StatusCode.SUCCESS)
                        .setInfo("success")
                        .build())
                .setPayload(ClientOuterClass.CreateClientResPayload.newBuilder().setId(id.toString()).build())
                .build();

        log.info("Created client (id = {})", id);
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    public void findClients(ClientOuterClass.SearchClientReq request, StreamObserver<ClientOuterClass.SearchClientRes> responseObserver) {
        var clients = service.findAll();
        var dtoClients = clients.stream()
                .map(c -> {
                    return ClientOuterClass.Client.newBuilder()
                            .setId(c.getId().toString())
                            .setName(c.getName())
                            .setSurname(c.getSurname())
                            .setAge(c.getAge())
                            .setEmail(c.getEmail())
                            .setGender(c.getGender() == Gender.MALE ? ClientOuterClass.Gender.MALE : ClientOuterClass.Gender.FEMALE)
                            .setCreatedAt(c.getCreatedAt().toString())
                            .build();
                })
                .collect(Collectors.toList());
        var res = ClientOuterClass.SearchClientRes.newBuilder()
                .setStatus(ClientOuterClass.Status.newBuilder()
                        .setCode(ClientOuterClass.StatusCode.SUCCESS)
                        .setInfo("success")
                        .build())
                .addAllClients(dtoClients)
                .build();

        log.info("Found clients (size = {})", clients.size());
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }
}
