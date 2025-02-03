package com.example.graphqlchatbot;

import java.util.Scanner;
import okhttp3.OkHttpClient;

public class Main {
    public static void main(String[] args) {
        GraphQLQueryGenerator bot = new GraphQLQueryGenerator("src/main/java/com/example/graphqlchatbot/api-sdl.graphql");

        Scanner scanner = new Scanner(System.in); 
        OkHttpClient client = new OkHttpClient();

        while (true) {
            try {
                System.out.print("User: ");
                String userMessage = scanner.nextLine();
                if (userMessage.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting...");
                    break;
                }
                String response = bot.chat(userMessage);
                System.out.println("Response: " + response);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                break;
            }
        }

        scanner.close(); 
        client.dispatcher().executorService().shutdown(); client.connectionPool().evictAll();
    }
}