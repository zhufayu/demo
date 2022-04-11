package test.dmall.nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NIOServer {
    private static Charset charset = Charset.forName("UTF-8");
    private static int i = 0;
    private static SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss", java.util.Locale.US);
    private static void handle(Map<String, SocketChannel> clientsMap, Selector selector, SelectionKey selectionKey) throws IOException {

         ByteBuffer sBuffer = ByteBuffer.allocate(1024);
         ByteBuffer rBuffer = ByteBuffer.allocate(1024);


        ServerSocketChannel server = null;
        SocketChannel client = null;

        int count = 0;
        if (selectionKey.isAcceptable()) {
            /*
             * 客户端请求连接事件
             * serversocket为该客户端建立socket连接，将此socket注册READ事件，监听客户端输入
             * READ事件：当客户端发来数据，并已被服务器控制线程正确读取时，触发该事件
             */
            server = (ServerSocketChannel) selectionKey.channel();
            client = server.accept();
            client.configureBlocking(false);
            clientsMap.put(client.getLocalAddress().toString().substring(1) + i++, client);
            client.register(selector, SelectionKey.OP_READ);

        } else if (selectionKey.isReadable()) {
            /*
             * READ事件，收到客户端发送数据，读取数据后继续注册监听客户端
             */
            client = (SocketChannel) selectionKey.channel();
            rBuffer.clear();
            count = client.read(rBuffer);
            if (count > 0) {
                rBuffer.flip();
                String   receiveText = charset.decode(rBuffer.asReadOnlyBuffer()).toString();

                System.out.println(sdf.format(new Date()) + "received message: "
                        + receiveText + " from:" + client.getRemoteAddress());

                sBuffer.clear();
                sBuffer.put(("your message is " + receiveText).getBytes("UTF-8"));
                sBuffer.flip();
                client.write(sBuffer);

                new Thread() {
                    @Override
                    public void run() {
                        while (true) {
                            try {
                                //String name = "["+client.getInetAddress().toString().substring(1)+"]";
                                InputStreamReader input = new InputStreamReader(System.in);
                                BufferedReader br = new BufferedReader(input);
                                String  sendText = br.readLine();

                                if (!clientsMap.isEmpty()) {
                                    for (Map.Entry<String, SocketChannel> entry : clientsMap.entrySet()) {
                                        SocketChannel temp = entry.getValue();
                                        sBuffer.clear();
                                        sBuffer.put((temp.getLocalAddress() + ":" + sendText).getBytes("UTF-8"));
                                        sBuffer.flip();
                                        //输出到通道
                                        temp.write(sBuffer);

//                                        temp.register(selector, SelectionKey.OP_READ);
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                break;
                            }
                        }
                    }
                }.start();

//                client = (SocketChannel) selectionKey.channel();
//                client.register(selector, SelectionKey.OP_READ);
            }
        }


    }

    public static void main(String[] args) throws IOException {
        int port = 6001;

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(port));

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("server start on port:" + port);

        Map<String, SocketChannel> clientsMap = new HashMap<String, SocketChannel>();

        while (true) {
            try {
                selector.select();//返回值为本次触发的事件数
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey key : selectionKeys) {
                    handle(clientsMap, selector, key);
                }
                selectionKeys.clear();//清除处理过的事件
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }

    }
}
