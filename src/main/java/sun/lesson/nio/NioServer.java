package sun.lesson.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @author: sun
 * @date: 2019/6/15
 */
public class NioServer {

    /**
     * 启动服务
     */
    private void start() throws IOException {
        /**
         * 1 创建Selector
         */
        Selector selector = Selector.open();

        /**
         * 2 创建serverSocketChannel
         */
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        /**
         * 3 绑定serverSocketChannel的端口
         */
        serverSocketChannel.bind(new InetSocketAddress(8000));

        /**
         * 4 设置channel为非阻塞模式
         */
        serverSocketChannel.configureBlocking(false);

        /**
         * 5 将channel注册到Selector上 状态为接受
         */
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server start successfully");

        /**
         * 6 在死循环里面调用selector的select方法 检测就绪情况
         */
        for (; ; ) {
            /**
             * TODO 获取可用的channel数量
             */
            int readyChannels = selector.select();

            /**
             * TODO 为什么这里是continue
             */
            if (readyChannels == 0) continue;

            /**
             * 7 调用selectionKeys获取channel的就绪集合
             */
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                // SelectionKey的实例
                SelectionKey selectionKey = iterator.next();

                // 移除Set中的当前Key
                iterator.remove();

                /**
                 * 8 判断就绪事件种类 处理对应的业务逻辑
                 */

                // 接入事件
                if (selectionKey.isAcceptable()) {
                    acceptHandler(serverSocketChannel, selector);
                }

                // 可读事件
                if (selectionKey.isReadable()) {
                    readHandler(selectionKey, selector);
                }

            }
        }
        /**
         * 9 根据业务情况判断是否要再次注册到selector上 重复执行第三步
         */
    }

    /**
     * 接入事件处理器
     */
    private void acceptHandler(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        /**
         * 如果是接入事件 首先应该要创建socketChannel
         */
        SocketChannel socketChannel = serverSocketChannel.accept();

        /**
         * 将socketChannel设置为非阻塞模式
         */
        socketChannel.configureBlocking(false);

        /**
         * 将socketChannel注册到 selector上 并且设置为监听 可读 事件
         */
        socketChannel.register(selector, SelectionKey.OP_READ);

        /**
         * 回复客户端消息
         */
        socketChannel.write(Charset.forName("UTF-8").encode("Connect successfully"));
    }

    /**
     * 可读事件处理器
     */
    private void readHandler(SelectionKey selectionKey, Selector selector) throws IOException {
        /**
         * 从selectionKey中获取到就绪的channel
         */
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

        /**
         * 创建buffer
         */
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        /**
         * 循环读取客户端的请求信息
         */
        String request = "";
        while (socketChannel.read(byteBuffer) > 0) {
            // 切换写模式为读模式
            byteBuffer.flip();

            // 读取buffer中的内容
            request += Charset.forName("UTF-8").decode(byteBuffer);
        }

        /**
         * 再次注册到selector上 并且监听 可读事件
         */
        socketChannel.register(selector, SelectionKey.OP_READ);

        /**
         * 广播客户端的请求信息 （这里假装是一个聊天室）
         * TODO 广播
         */
        if (request.length() > 0) {
            System.out.println(request);
        }
    }


    public static void main(String[] args) throws IOException {
        new NioServer().start();
    }

}
