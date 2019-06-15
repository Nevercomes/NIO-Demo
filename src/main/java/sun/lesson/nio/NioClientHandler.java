package sun.lesson.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @author: sun
 * @date: 2019/6/15
 */
public class NioClientHandler implements Runnable {

    private Selector selector;

    public NioClientHandler(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            System.out.println("Client listening");
            /**
             * 6 在死循环里面调用selector的select方法 检测就绪情况
             */
            for (; ; ) {
                int readyChannels = selector.select();
                if (readyChannels == 0) continue;

                /**
                 * 调用selectionKeys获取channel的就绪集合
                 */
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    // SelectionKey的实例
                    SelectionKey selectionKey = iterator.next();

                    // 移除Set中的当前Key
                    iterator.remove();

                    /**
                     * 判断就绪事件种类 处理对应的业务逻辑
                     */
                    // 可读事件
                    if (selectionKey.isReadable()) {
                        readHandler(selectionKey, selector);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
         * 循环读取服务端的响应
         */
        String response = "";
        while (socketChannel.read(byteBuffer) > 0) {
            // 切换写模式为读模式
            byteBuffer.flip();

            // 读取buffer中的内容
            response += Charset.forName("UTF-8").decode(byteBuffer);
        }

        /**
         * 再次注册到selector上 并且监听 可读事件
         */
        socketChannel.register(selector, SelectionKey.OP_READ);

        /**
         * 打印服务端的响应到本地
         */
        if (response.length() > 0) {
            System.out.println(response);
        }
    }
}
