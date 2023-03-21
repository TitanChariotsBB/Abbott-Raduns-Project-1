/** Author:  Christian Abbott, Ben Raduns
 * Course:  COMP 342 Data Communications and Networking
 * Date:    5 April 2023
 * Description: Server side of FTP protocol
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class FtpServer {
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
                if (messageFromClient.equals("QUIT")) break;
                System.out.println("Received command " + messageFromClient);


            } while (true);

            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
