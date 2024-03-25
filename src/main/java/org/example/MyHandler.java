package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MyHandler implements HttpHandler {

    private List<Wine> redWines;
    private List<Wine> whiteWines;

    public MyHandler() {
        redWines = new ArrayList<>();
        redWines.add(new Wine("Chianti", 20.0));
        redWines.add(new Wine("Barolo", 50.0));
        redWines.add(new Wine("Merlot", 30.0));

        whiteWines = new ArrayList<>();
        whiteWines.add(new Wine("Chardonnay", 25.0));
        whiteWines.add(new Wine("Pinot Grigio", 15.0));
        whiteWines.add(new Wine("Sauvignon Blanc", 35.0));
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI uri = exchange.getRequestURI();
        System.out.println(uri);

        String command = exchange.getRequestURI().getQuery();
        if (command != null) {
            String[] params = command.split("=");
            if (params.length == 2 && params[0].equals("command")) {
                String s = params[1];

                System.out.println(s);

                String response;
                try {
                    switch (s) {
                        case "red":
                            response = getWineList(redWines);
                            break;
                        case "white":
                            response = getWineList(whiteWines);
                            break;
                        case "sorted_by_name":
                            response = getSortedWineListByName();
                            break;
                        case "sorted_by_price":
                            response = getSortedWineListByPrice();
                            break;
                        case "all":
                            response = getAllWineList();
                            break;
                        default:
                            response = "Invalid command";
                            break;
                    }
                } catch (Exception e) {
                    response = "Error processing request: " + e.getMessage();
                    e.printStackTrace();
                }
                extracted(exchange, 200, response);
            }
            else {
                String response = "Invalid command format";
                extracted(exchange, 400, response);
            }
        }
        else {
            String response = "Missing 'command' parameter";
            extracted(exchange, 400, response);
        }
    }

    private String getAllWineList() {
        List<Wine> allWines = new ArrayList<>();
        allWines.addAll(redWines);
        allWines.addAll(whiteWines);
        StringBuilder response = new StringBuilder();
        for (Wine wine : allWines) {
            response.append(wine.getName()).append("\n");
        }
        return response.toString();
    }

    private static void extracted(HttpExchange exchange, int rCode, String response) {
        try {
            exchange.sendResponseHeaders(rCode, response.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getWineList(List<Wine> wineList) {
        StringBuilder response = new StringBuilder();
        for (Wine wine : wineList) {
            response.append(wine.getName()).append("\n");
        }
        return response.toString();
    }

    private String getSortedWineListByName() {
        List<Wine> allWines = new ArrayList<>();
        allWines.addAll(redWines);
        allWines.addAll(whiteWines);
        Collections.sort(allWines, Comparator.comparing(Wine::getName));
        StringBuilder response = new StringBuilder();
        for (Wine wine : allWines) {
            response.append(wine.getName()).append("\n");
        }
        return response.toString();
    }

    private String getSortedWineListByPrice() {
        List<Wine> allWines = new ArrayList<>();
        allWines.addAll(redWines);
        allWines.addAll(whiteWines);
        Collections.sort(allWines, Comparator.comparing(Wine::getPrice));
        StringBuilder response = new StringBuilder();
        for (Wine wine : allWines) {
            response.append(wine.getName()).append(" - €").append(wine.getPrice()).append("\n");
        }
        return response.toString();
    }

    static class Wine {
        private String name;
        private double price;

        public Wine(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }
    }
}