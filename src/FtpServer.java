/** Author:  Christian Abbott, Ben Raduns
 * Course:  COMP 342 Data Communications and Networking
 * Date:    5 April 2023
 * Description: Server side of FTP protocol
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class FtpServer {
    public static Path currentDirectory = Paths.get("server_folder");
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Welcome to GCC FTP service!");
        System.out.println("Waiting for client commands...");
        try {
            ServerSocket serverSocket = new ServerSocket(9001);
            Socket socket = serverSocket.accept();
            DataInputStream inStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());

            do {
                String messageFromClient = inStream.readUTF();
                if (messageFromClient.equals("QUIT")) {
                    System.out.println("Connection terminated by client");
                    break;
                }
                if (messageFromClient.equals("LIST")) {
                    System.out.println(list());
                }
                System.out.println("Received command " + messageFromClient);


            } while (true);

            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String list() throws IOException {
        Set<String> files = new HashSet<>();
        try { DirectoryStream<Path> stream = Files.newDirectoryStream(currentDirectory);
            for (Path path : stream) {
                files.add(path.getFileName().toString());
            }}
        catch (IOException e) {
            System.out.println("Directory could not be found");
        }
        return files.toString();
    }
}
