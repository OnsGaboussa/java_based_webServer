package webServer;


import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

class Main {
    public static void main(String[] args) throws Exception {
        // Start receiving messages - ready to receive messages!
        try (ServerSocket serverSocket = new ServerSocket(8083)) {
            System.out.println("Server started.\nListening for messages.");

            while (true) {
                // Handle a new incoming message
                try (Socket client = serverSocket.accept()) {
                    // client <-- messages queued up in it!!
                    System.out.println("Debug: got new message " + client.toString());

                    // Read the request - listen to the message
                    InputStreamReader isr = new InputStreamReader(client.getInputStream());
                    BufferedReader br = new BufferedReader(isr);

                    // Read the first request from the client
                    StringBuilder request = new StringBuilder();
                    String line; // Temp variable to hold one line at a time of our message
                    line = br.readLine();
                    while (line != null && !line.isBlank()) {  // First check for null, then isBlank
                        request.append(line).append("\r\n");
                        line = br.readLine();
                    }


                    // Parse the resource requested
                    String firstLine = request.toString().split("\n")[0];
                    String resource = firstLine.split(" ")[1]; // Get the "resource"
                    System.out.println("Requested resource: " + resource);

                    OutputStream clientOutput = client.getOutputStream();

                    // Handle the request based on the resource
                    if (resource.equals("/jess")) {
                        // Serve the image "fav.jpg"
                        try (FileInputStream image = new FileInputStream("C:\\mine\\java\\webServer\\fav.jpg")) {
                            clientOutput.write(("HTTP/1.1 200 OK\r\n").getBytes());
                            clientOutput.write(("Content-Type: image/jpeg\r\n").getBytes());
                            clientOutput.write(("\r\n").getBytes());
                            clientOutput.write(image.readAllBytes());  // Write image bytes
                        } catch (FileNotFoundException e) {
                            clientOutput.write(("HTTP/1.1 404 Not Found\r\n").getBytes());
                            clientOutput.write(("Content-Type: text/plain\r\n\r\n").getBytes());
                            clientOutput.write(("Image not found").getBytes());
                        }
                    } else if (resource.equals("/hello")) {
                        // Serve a "Hello World" text response
                        clientOutput.write(("HTTP/1.1 200 OK\r\n").getBytes());
                        clientOutput.write(("Content-Type: text/plain\r\n\r\n").getBytes());
                        clientOutput.write(("Hello World").getBytes());
                    } else {
                        // Default response for other requests
                        clientOutput.write(("HTTP/1.1 200 OK\r\n").getBytes());
                        clientOutput.write(("Content-Type: text/plain\r\n\r\n").getBytes());
                        clientOutput.write(("What ya lookin' for?").getBytes());
                    }

                    // Flush the output stream and close the client connection
                    clientOutput.flush();
                    client.close();
                }
            }
        }
    }
}

