/** Author:  Christian Abbott, Ben Raduns
 * Course:  COMP 342 Data Communications and Networking
 * Date:    5 April 2023
 * Description: Client side of FTP protocol
 */

//test to see if I can make commits, will delete

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class FtpClient {

    public static Path path = Paths.get("client_folder").toAbsolutePath();
    public static File currentDirectory = new File(path.toString());

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
                if (messageToServer.equals("QUIT")) {
                    outStream.writeUTF(messageToServer);
                    break;
                }
                else if (messageToServer.startsWith("RETR")) {
                    // Prepare to receive file
                    String newFileName = messageToServer.replace("RETR ","");

                    outStream.writeUTF(messageToServer);

                    int bytes = 0;
                    FileOutputStream fileOutputStream = new FileOutputStream("client_folder/"+newFileName);

                    long size = inStream.readLong(); // read file size
                    System.out.println("Size of incoming file: " + size);
                    byte[] buffer = new byte[1024];
                    while (size > 0 && (bytes = inStream.read(buffer,
                            0, (int)Math.min(buffer.length, size))) != -1) {
                        fileOutputStream.write(buffer, 0, bytes);
                        size -= bytes;
                    }
                    System.out.println("Here are the file contents!");
                }
                else if (messageToServer.equals("STOR")) {
                    String fileToSendName = messageToServer.replace("STOR ", "");
                    File fileToSend = new File(fileToSendName);
                }
                // LIST and PWD
                else {
                    outStream.writeUTF(messageToServer);
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
