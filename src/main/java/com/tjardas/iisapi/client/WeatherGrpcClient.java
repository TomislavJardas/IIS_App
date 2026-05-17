package com.tjardas.iisapi.client;

import hr.algebra.weather.grpc.TemperatureRequest;
import hr.algebra.weather.grpc.TemperatureResponse;
import hr.algebra.weather.grpc.WeatherGrpcServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class WeatherGrpcClient implements AutoCloseable {

    private final ManagedChannel channel;
    private final WeatherGrpcServiceGrpc.WeatherGrpcServiceBlockingStub blockingStub;

    public WeatherGrpcClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.blockingStub = WeatherGrpcServiceGrpc.newBlockingStub(channel);
    }

    public List<String> getTemperature(String cityName) {
        TemperatureRequest request = TemperatureRequest.newBuilder()
                .setCityName(cityName)
                .build();

        TemperatureResponse response = blockingStub.getTemperature(request);
        return response.getResultsList();
    }

    @Override
    public void close() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}
