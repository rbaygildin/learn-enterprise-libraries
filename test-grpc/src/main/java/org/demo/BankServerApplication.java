package org.demo;

import io.grpc.ServerBuilder;
import org.demo.bank.client.ClientGrpcService;

import java.io.IOException;

/**
 * @author Roman Baygildin (RVBaygildin@sberbank.ru)
 */
public class BankServerApplication {
    public static void main(String[] args) throws IOException, InterruptedException {
        var server = ServerBuilder.forPort(8080)
                .addService(new ClientGrpcService())
                .build();
        server.start();
        server.awaitTermination();
    }
}
