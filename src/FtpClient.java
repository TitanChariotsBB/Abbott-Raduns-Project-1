/** Author:  Christian Abbott, Ben Raduns
 * Course:  COMP 342 Data Communications and Networking
 * Date:    5 April 2023
 * Description: Client side of FTP protocol
 */

//test to see if I can make commits, will delete

import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class FtpClient {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Welcome to GCC FTP client!");

        try {
            Socket socket = new Socket("localhost", 9001);
            DataInputStream inStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());

            while (true) {
                System.out.print("Command: ");
                String messageToServer = scan.nextLine().trim();
                outStream.writeUTF(messageToServer);
                if (messageToServer.equals("QUIT")) break;
                if (messageToServer.equals("LIST") || messageToServer.equals("PWD")) {
                    String messageFromServer = inStream.readUTF();
                    System.out.println(messageFromServer);
                }
            }

            socket.close();
            inStream.close();
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
