package com.example;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

public class Main {

    public static void main(String[] args) {
        String requestBody = createTransaction();
        String apiKey = "1f53760c33f04a898b6d81d31bdf059e0fa11b08271d48f882b05d338c7ff665";
        String accountId = "584146918017388545";

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.tarvent.com/graphql"))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("X-API-KEY", apiKey)
                .header("Account", accountId)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            System.out.println(responseBody);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    static String createTransaction() {
        Map<String, Object> tarventTxSettings = new HashMap<>();

        Map<String, Object> tracking = new HashMap<>();
        tracking.put("opens", true);
        tracking.put("clicks", true);
        tarventTxSettings.put("tracking", tracking);
        tarventTxSettings.put("ignoreSuppressCheck", false);

        Map<String, Object> tarventTxHeader = new HashMap<>();
        Map<String, Object> from = new HashMap<>();
        from.put("name", "Tarvent Team");
        from.put("emailAddress", "hello@tarvent.com");
        tarventTxHeader.put("from", from);
        tarventTxHeader.put("subject", "This is a test");

        Map<String, Object> tarventTxContents = new HashMap<>();
        tarventTxContents.put("templateId", "null");

        List<Map<String, Object>> contentBodies = new ArrayList<>();
        Map<String, Object> contentBody0 = new HashMap<>();
        contentBody0.put("clickTracking", true);
        contentBody0.put("mimeType", "HTML");
        contentBody0.put("charset", "UTF8");
        contentBody0.put("bodyContent", "<html><body><p>Hello {{Tx.VariableData.FirstName}},</p><p>OMG, it's working!</p></body></html>");
        contentBodies.add(contentBody0);
        tarventTxContents.put("contentBodies", contentBodies);

        List<Map<String, Object>> recipientList = new ArrayList<>();
        recipientList
                .add(addRecipient("Developer", "derekj@tarvent.com", null, "TO", new ArrayList<Map<String, String>>(), List.of(Map.of("name", "FirstName", "value", "Developer"))));

        Map<String, Object> transactionRequest = new HashMap<>();
        transactionRequest.put("groupName", "Notification Email");
        transactionRequest.put("settings", tarventTxSettings);
        transactionRequest.put("header", tarventTxHeader);
        transactionRequest.put("content", tarventTxContents);
        transactionRequest.put("recipients", recipientList);

        String requestJsonString = "";
        try {
            requestJsonString = new com.fasterxml.jackson.databind.ObjectMapper()
                    .writeValueAsString(transactionRequest);
        } catch (JsonProcessingException e) { }

        // Create JSON
        String jsonString = "{\"query\": \"mutation createTransaction($input: CreateTransactionInput!) { createTransaction(input: $input) { emailAddress errorCode errorMsg requestId transactionId }}\", "
                +
                "\"variables\": {\"input\": " + requestJsonString + "}}";
        System.out.println(jsonString);
        return jsonString;
    }

    static Map<String, Object> addRecipient(String name, String email, String contactId, String type,
            List<Map<String, String>> metadata, List<Map<String, String>> variables) {
        Map<String, Object> recipient = new HashMap<>();
        recipient.put("name", name);
        recipient.put("emailAddress", email);
        recipient.put("contactId", contactId);
        recipient.put("type", type);
        recipient.put("metadata", metadata);
        recipient.put("variables", variables);

        return recipient;
    }
}
