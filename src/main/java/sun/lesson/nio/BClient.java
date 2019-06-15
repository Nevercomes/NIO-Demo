package sun.lesson.nio;

import java.io.IOException;

/**
 * @author: sun
 * @date: 2019/6/15
 */
public class BClient {

    public static void main(String[] args) throws IOException {
        new NioClient().start("BClient");
    }

}
