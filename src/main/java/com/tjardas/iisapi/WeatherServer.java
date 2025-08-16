package com.tjardas.iisapi;

import com.tjardas.iisapi.service.WeatherService;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

public class WeatherServer {

    public static void main(String[] args) {
        try {
            WebServer webServer = new WebServer(9090);
            XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();

            PropertyHandlerMapping phm = new PropertyHandlerMapping();
            phm.addHandler("WeatherService", WeatherService.class);
            xmlRpcServer.setHandlerMapping(phm);

            XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
            serverConfig.setEnabledForExtensions(true);
            serverConfig.setContentLengthOptional(false);

            webServer.start();
            System.out.println("XML-RPC server started successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
