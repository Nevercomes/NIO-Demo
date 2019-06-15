package sun.lesson.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
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
            if(readyChannels == 0) continue;

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

                // TODO 接入事件

                // TODO 可读事件

            }
        }

        /**
         * 9 根据业务情况判断是否要再次注册到selector上 重复执行第三步
         */

    }


    public static void main(String[] args) {

    }

}
