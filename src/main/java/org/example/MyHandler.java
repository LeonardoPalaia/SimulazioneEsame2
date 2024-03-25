package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MyHandler implements HttpHandler {

    // Lista di esempio dei vini
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

        InputStream is = exchange.getRequestBody();

        URI uri = exchange.getRequestURI();
        System.out.println(uri);

        String method = exchange.getRequestMethod();
        System.out.println(method);

        String s = read(is);

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
                case "stored_by_name":
                    response = getSortedWineListByName();
                    break;
                case "stored_by_price":
                    response = getSortedWineListByPrice();
                    break;
                default:
                    response = "Invalid command";
                    break;
            }
        } catch (Exception e) {
            response = "Error processing request: " + e.getMessage();
            e.printStackTrace();
        }

        try {
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String read(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        System.out.println("\n");
        StringBuilder received = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
            received.append(line);
        }
        return received.toString();
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
