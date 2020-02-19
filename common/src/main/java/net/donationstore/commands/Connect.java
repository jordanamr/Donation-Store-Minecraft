package net.donationstore.commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.donationstore.exception.InvalidCommandUseException;
import net.donationstore.exception.WebstoreAPIException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

// Must handle logic in each plugin that says the following:
// If this throws an exception then we can't continue.
// If it doesn't save the passed in applicationAPILocation and secretKey
public class Connect implements Command {

    private HttpClient httpClient;
    private ArrayList<String> logs;

    public Connect() {
        logs = new ArrayList<>();
        httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    @Override
    public ArrayList<String> runCommand(String[] args) throws Exception {

        if (args.length != 2) {
            logs.add("Invalid usage of command. Help Info: ");
            logs.add(this.helpInfo());
            throw new InvalidCommandUseException(logs);
        } else {
            String webstoreAPILocation = args[0];
            String secretKey = args[1];

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("%s/information", webstoreAPILocation)))
                    .header("secret-key", secretKey)
                    .build();
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JsonObject jsonResponse = new JsonParser().parse(response.body()).getAsJsonObject();

                    JsonObject storeInformation = jsonResponse.getAsJsonObject("webstore");

                    JsonObject serverInformation = jsonResponse.getAsJsonObject("server");

                    logs.add(String.format("Connected to Webstore: @s", storeInformation.get("name").getAsString()));
                    logs.add("Commands will now be executed when packages are purchased.");
                } else {
                    logs.add("Invalid webstore API response:");
                    logs.add(response.body());
                    throw new WebstoreAPIException(logs);
                }
            } catch (IOException exception) {
                throw new IOException("IOException when contacting the webstore API when running the connect command");
            } catch (InterruptedException exception) {
                throw new InterruptedException("InterruptedException when contacting the webstore API when running the connect command");
            }
        }

        return logs;
    }

    @Override
    public String helpInfo() {
        return "This command is used to connect the Donation Store plugin to your webstore.\n" +
                " Usage: /ds connect <application_api_location> <secret_key>";
    }

    @Override
    public CommandType commandType() {
        return CommandType.CONSOLE;
    }
}