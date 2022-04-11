package test.dmall.netty.socket.echodemo;


import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class EchoClient {
    public static void main(String[] args) throws Exception {
        try {
            Socket so = new Socket("127.0.0.1", 8081);
            OutputStream os = so.getOutputStream();
            InputStream is = so.getInputStream();
            PrintStream ps = new PrintStream(os);

            BufferedReader br = new BufferedReader(new InputStreamReader(is)); //客户从server的输入流
            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader key = new BufferedReader(isr);//从键盘的输入流
            while (true) {
                //读取服务器返回的消息数据
                String data = br.readLine();
                while (true) {
                    if (is.available() > 0) {
                        data += br.readLine();
                    } else {
                        break;
                    }
                }
                System.out.println("get server data>>" + data);
                String temp = key.readLine();
                ps.println(temp);//因为server用的是readline,必须用println
            }
        } catch (
                UnknownHostException e) {
            e.printStackTrace();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
}
