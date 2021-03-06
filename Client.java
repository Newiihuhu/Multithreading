
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class Client {

	private static Socket socket;
	private static DataInputStream dataInput = null;
	private static DataOutputStream dataOutput = null;
	private static BufferedReader BR;
        
        //config color
        public static final String GREEN = "\u001B[32m";
        public static final String BLUE = "\u001B[34m";

	public static void main(String[] args) {
		try {
			// create connection
			socket = new Socket("localhost", 1234);
			System.out.println(GREEN+"Connection Success!!!");
			// get list files from server
			dataInput = new DataInputStream(socket.getInputStream());
			String msg_in = dataInput.readUTF();
                        String submsg_in = msg_in.substring(1, msg_in.length()-1);
                        String[] listmsg = submsg_in.split(",");
                        System.out.println(GREEN+"Server : ");
                        for(String msg : listmsg){
                            System.out.println(BLUE+msg);
                        }
			// choose file to get File
			System.out.print(GREEN+"Choose File to Download : ");
			BR = new BufferedReader(new InputStreamReader(System.in));
			String fileName = BR.readLine();
			// send filename that choose
			dataOutput = new DataOutputStream(socket.getOutputStream());
			dataOutput.writeUTF(fileName);
			// รับไฟล์ที่เป็น byte array จาก server
			dataInput = new DataInputStream(socket.getInputStream());
			receiveFile();
                        System.out.println(GREEN+"Download Success!");

		} catch (Exception e) {
			System.out.println("error client" + e);
			System.exit(1);
		}

	}

	
	public static void receiveFile() throws IOException {
		try {
			int bytesRead;
			String fileName = dataInput.readUTF();
			FileOutputStream output = new FileOutputStream("resources/" + fileName);
			long size = dataInput.readLong();
			byte[] buffer = new byte[1024];
			while (size > 0 && (bytesRead = dataInput.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {// แปลง byte array กลับเป็นเนื้อหาไฟล์
				output.write(buffer, 0, bytesRead);
				size -= bytesRead;
			}
			output.flush();
		} catch (Exception e) {
			System.err.println("error downloadFile" + e);
		}

	}
}
