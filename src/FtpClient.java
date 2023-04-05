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
                    retr(newFile,  inStream);
                }
                else if (messageToServer.startsWith("STOR")) {
                    String fileToSendName = messageToServer.replace("STOR ", "");
                    outStream.writeUTF(messageToServer);
                    File file = new File("client_folder/"+fileToSendName);
                    FileInputStream fileInputStream;
                    try {
                        fileInputStream = new FileInputStream(file);
                        stor(file, outStream, fileInputStream);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
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

    /**
     * Sends the specified file from the client folder to the server folder
     * @param file File to be sent
     * @param outStream Reference to the general DataOutputStream from the socket used to send data to server
     * @param fileStream Reads the data from the specified file
     * @throws IOException if it cannot read or write to the streams
     */
    public static void stor(File file, DataOutputStream outStream, FileInputStream fileStream) throws IOException {
        int bytes = 0;
        outStream.writeLong(file.length());
        byte[] buffer = new byte[1024];
        while ((bytes = fileStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytes);
            outStream.flush();
        }
        System.out.println(file.getName() + " stored correctly!\n");
    }

    /**
     * Retrieves file from the server folder and puts in client folder
     * @param file File to be retrieved
     * @param inStream Reference to general DataInputStream used by the socket
     * @throws IOException if it can't read from the input stream/write to output stream
     */
    public static void retr(File file, DataInputStream inStream) throws IOException {
        int bytes = 0;
        FileOutputStream fileOutputStream = new FileOutputStream("client_folder/" + file.getName());

        long size = inStream.readLong(); // read file size
        byte[] buffer = new byte[1024];
        while (size > 0 && (bytes = inStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
            fileOutputStream.write(buffer, 0, bytes);
            size -= bytes;
        }
        System.out.println("File received!\n");
    }
}
