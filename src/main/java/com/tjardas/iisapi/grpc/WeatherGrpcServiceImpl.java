package com.tjardas.iisapi.grpc;

import com.tjardas.iisapi.service.WeatherService;
import hr.algebra.weather.grpc.TemperatureRequest;
import hr.algebra.weather.grpc.TemperatureResponse;
import hr.algebra.weather.grpc.WeatherGrpcServiceGrpc;
import io.grpc.stub.StreamObserver;

public class WeatherGrpcServiceImpl extends WeatherGrpcServiceGrpc.WeatherGrpcServiceImplBase {

    private final WeatherService weatherService;

    public WeatherGrpcServiceImpl(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Override
    public void getTemperature(TemperatureRequest request, StreamObserver<TemperatureResponse> responseObserver) {
        TemperatureResponse response = TemperatureResponse.newBuilder()
                .addAllResults(weatherService.getTemperature(request.getCityName()))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
