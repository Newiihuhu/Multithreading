import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static ServerSocket serversocket;
    private static Socket socket;
    //config color
    public static final String GREEN = "\u001B[32m";

    public static void main(String[] args) {
        try {
            // create socket server
            serversocket = new ServerSocket(1234);
            System.out.println(GREEN+"Server is Running . . .");
        } catch (Exception e) {
            System.out.println("error start server " + e);
            System.exit(1);
        }
        while (true) {
            try {
                // get connected from client
                socket = serversocket.accept();
                System.out.print(GREEN+"Conection Accept : ");
                System.out.println(GREEN+socket);
                Thread t = new Thread(new ClientHandler(socket));
                t.start();

            } catch (Exception e) {
                System.err.println("Error while get input or output from/to client " + e);
            }
        }
    }

}

class ClientHandler extends Thread {

    private final Socket clientSocket;
    private DataOutputStream dataOutputStream = null;
    private DataInputStream dataInputStream = null;
    private String path = "files";

    public ClientHandler(Socket client) {
        this.clientSocket = client;
    }

    @Override
    public void run() {
        try{
            // send list files to client
        dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
        dataOutputStream.writeUTF(showFileName().toString());
        // get filename that client choose from client
        dataInputStream = new DataInputStream(clientSocket.getInputStream());
        String message_in = dataInputStream.readUTF();
        // send file client choose to client
        sentFile(message_in);
        }catch(Exception e){
            
        }
        
    }
    public  void sentFile(String filename) throws FileNotFoundException {
        try {
            File file = new File(path + "\\" + filename); // ดึงไฟล์
            byte[] mybytearray = new byte[(int) file.length()]; // สร้าง byte array ให้ขนาดเท่ากับ ความยาวไฟล์ไว้ก่อน
            FileInputStream fis = new FileInputStream(file); // นำไฟล์มาทำเป็น stream
            BufferedInputStream bis = new BufferedInputStream(fis); // นำไฟล์ stream มาเป็น buffer
            DataInputStream dis = new DataInputStream(bis); // สร้าง DatainputStream จาก buffer
            dis.readFully(mybytearray, 0, mybytearray.length); // เปลี่ยน buffer ให้เป็น byte array
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            dataOutputStream.writeUTF(file.getName());
            dataOutputStream.writeLong(mybytearray.length);
            dataOutputStream.write(mybytearray, 0, mybytearray.length);
            dataOutputStream.flush();
            dis.close();
        } catch (Exception e) {
            System.err.println("Error send file " + e);
        }
    }
    public List<String> showFileName() {
        File dir = new File(path);
        File[] fList = dir.listFiles();
        List<String> listFile = new ArrayList<>();
        for (File file : fList) {
            listFile.add(file.getName());
        }
        return listFile;
    }
}
