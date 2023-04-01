/** Author:  Christian Abbott, Ben Raduns
 * Course:  COMP 342 Data Communications and Networking
 * Date:    5 April 2023
 * Description: Client side of FTP protocol
 */

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

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
                if (messageToServer.equals("QUIT")) {
                    outStream.writeUTF(messageToServer);
                    break;
                }
                else if (messageToServer.startsWith("RETR")) {
                    // Prepare to receive file
                    String newFileName = messageToServer.replace("RETR ","");
                    File newFile = new File("client_folder/"+newFileName);
                    if (!newFile.exists()) {
                        newFile.createNewFile();
                    }

                    outStream.writeUTF(messageToServer);

                    int bytes = 0;
                    FileOutputStream fileOutputStream = new FileOutputStream("client_folder/" + newFileName);

                    long size = inStream.readLong(); // read file size
                    byte[] buffer = new byte[1024];
                    while (size > 0 && (bytes = inStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
                        fileOutputStream.write(buffer, 0, bytes);
                        size -= bytes;
                    }
                    System.out.println("File received!\n");

                }
                else if (messageToServer.startsWith("STOR")) {
                    String fileToSendName = messageToServer.replace("STOR ", "");

                    outStream.writeUTF(messageToServer);

                    File file = new File("client_folder/"+fileToSendName);
                    FileInputStream fileInputStream = null;
                    try {
                        fileInputStream = new FileInputStream(file);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    int bytes = 0;
                    outStream.writeLong(file.length());
                    byte[] buffer = new byte[1024];
                    while ((bytes = fileInputStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, bytes);
                        outStream.flush();
                    }
                    System.out.println(fileToSendName + " stored correctly!\n");
                }
                else if (messageToServer.equals("LIST") || messageToServer.equals("PWD")) {
                    outStream.writeUTF(messageToServer);
                    String messageFromServer = inStream.readUTF();
                    System.out.println(messageFromServer);
                }
                else {
                    System.out.println("Invalid command.\n");
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
