package com.tjardas.iisapi;

import com.tjardas.iisapi.grpc.WeatherGrpcServiceImpl;
import com.tjardas.iisapi.service.WeatherService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class WeatherServer {

    public static void main(String[] args) {
        final int port = 9090;

        try {
            Server server = ServerBuilder.forPort(port)
                    .addService(new WeatherGrpcServiceImpl(new WeatherService()))
                    .build()
                    .start();

            System.out.println("gRPC weather server started on port " + port + ".");

            Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
            server.awaitTermination();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
