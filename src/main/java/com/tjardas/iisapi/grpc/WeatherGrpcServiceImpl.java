package com.tjardas.iisapi.grpc;

import com.tjardas.iisapi.service.WeatherService;
import hr.algebra.weather.grpc.TemperatureRequest;
import hr.algebra.weather.grpc.TemperatureResponse;
import hr.algebra.weather.grpc.WeatherGrpcServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.List;

public class WeatherGrpcServiceImpl extends WeatherGrpcServiceGrpc.WeatherGrpcServiceImplBase {

    private final WeatherService weatherService;

    public WeatherGrpcServiceImpl() {
        this(new WeatherService());
    }

    public WeatherGrpcServiceImpl(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Override
    public void getTemperature(TemperatureRequest request, StreamObserver<TemperatureResponse> responseObserver) {
        List<String> results = weatherService.getTemperature(request.getCityName());
        TemperatureResponse response = TemperatureResponse.newBuilder()
                .addAllResults(results)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
