/** Author:  Christian Abbott, Ben Raduns
 * Course:  COMP 342 Data Communications and Networking
 * Date:    5 April 2023
 * Description: Server side of FTP protocol
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class FtpServer {
    //Current working directory of Project
    public static Path path = Paths.get("server_folder").toAbsolutePath();
    public static File currentDirectory = new File(path.toString());

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
                System.out.println("Received command " + messageFromClient);
                if (messageFromClient.equals("QUIT")) {
                    System.out.println("Connection terminated by client");
                    break;
                }
                else if (messageFromClient.equals("LIST")) {
                    outStream.writeUTF(list());
                }
                else if (messageFromClient.equals("PWD")) {
                    outStream.writeUTF(pwd());
                }
                else if (messageFromClient.equals("STOR")) {
                    System.out.println("Store");
                }
                else if (messageFromClient.startsWith("RETR")) {
                    String fileToSendName = messageFromClient.replace("RETR ", "");
                    System.out.println("Sending " + fileToSendName);

                    File file = new File("server_folder/"+fileToSendName);
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
                    System.out.println(fileToSendName + " sent!");
                }
            } while (true);

            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lists the files found in the current directory
     * @return a string listing the files
     * @throws IOException if the directory cannot be found
     */
    public static String list() throws IOException {
        File[] files = currentDirectory.listFiles();
        StringBuilder sb = new StringBuilder();
        if (files != null) {
            for (File i : files) {
                if (i.isFile()) {
                    sb.append(i.getName());
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }

    /**
     * @return The current working directory as a String
     */
    public static String pwd(){
        return currentDirectory.toString() + "\n";
    }
}
