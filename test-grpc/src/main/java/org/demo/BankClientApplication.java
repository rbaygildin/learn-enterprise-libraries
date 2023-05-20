package org.demo;

import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.demo.bank.client.ClientGrpcService;
import org.demo.bank.client.ClientOuterClass;
import org.demo.bank.client.ClientServiceGrpc;
import org.demo.bank.client.dao.ClientService;

/**
 * @author Roman Baygildin (RVBaygildin@sberbank.ru)
 */
@Slf4j
public class BankClientApplication {
    public static void main(String[] args) {
        var channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext().build();
        ClientServiceGrpc.ClientServiceBlockingStub stub = ClientServiceGrpc.newBlockingStub(channel);

        //create clients
        stub.createClient(ClientOuterClass.CreateClientReq.newBuilder()
                .setClientId("bank-client")
                .setPayload(ClientOuterClass.CreateClientReqPayload.newBuilder()
                        .setName("Peter")
                        .setSurname("Johnson")
                        .setAge(35)
                        .setGender(ClientOuterClass.Gender.MALE)
                        .setEmail("peter@mail.ru")
                        .build())
                .build());

        //Find clients
        var clients = stub.findClients(ClientOuterClass.SearchClientReq.newBuilder()
                .setClientId("bank-client")
                .build());
        clients.getClientsList().forEach(c -> {
            log.info("Found client (client = {})", c);
        });


        channel.shutdown();
    }
}
