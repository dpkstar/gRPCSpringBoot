package com.optum.comlayer.service;

import com.comlayer.OnCloudServiceGrpc;
import com.comlayer.SendJsonRequest;
import com.comlayer.SendJsonResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.io.IOException;
import java.io.InputStream;

@Service
public class ComLayerService {

    @Value("${oncloud.host}")
    private String onCloudHost = "localhost";

    @Value("${oncloud.port}")
    private int onCloudPort = 6565;

    public Mono<String> sendJsonToOnCloud() {
        return Mono.create(sink -> {
            String jsonFileName = "data.json";
            System.out.println("Sending JSON file: " + jsonFileName);
            System.out.println("to host: " + onCloudHost + ", port: " + onCloudPort);

            // Load the JSON file from resources
            try {
                ClassPathResource resource = new ClassPathResource(jsonFileName);
                InputStream inputStream = resource.getInputStream();

                // Build gRPC channel and stub
                ManagedChannel channel = ManagedChannelBuilder.forAddress(onCloudHost, onCloudPort)
                        .usePlaintext() // Remove this line for production use with TLS
                        .build();

                OnCloudServiceGrpc.OnCloudServiceStub stub = OnCloudServiceGrpc.newStub(channel);

                // Create a StreamObserver for handling responses
                StreamObserver<SendJsonResponse> responseObserver = new StreamObserver<SendJsonResponse>() {
                    @Override
                    public void onNext(SendJsonResponse response) {
                        sink.success(response.getResponse());
                    }

                    @Override
                    public void onError(Throwable t) {
                        sink.error(t);
                    }

                    @Override
                    public void onCompleted() {
                        // No action needed here
                    }
                };

                // Create request with JSON file content
                SendJsonRequest request = SendJsonRequest.newBuilder()
                        .setJson(new String(inputStream.readAllBytes(), "UTF-8"))
                        .build();

                // Send request to OnCloud service
                stub.sendJsonToOnCloud(request, responseObserver);

                // Close channel after processing
                channel.shutdown();

            } catch (IOException e) {
                sink.error(e);
            }
        });
    }
}
