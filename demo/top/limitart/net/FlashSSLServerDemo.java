package top.limitart.net;

import top.limitart.net.flashssl.FlashSSLEndPoint;

/**
 * @author hank
 * @version 2018/10/15 0015 21:23
 */
public class FlashSSLServerDemo {
    public static void main(String[] args) throws Exception {
        FlashSSLEndPoint.builder().build().start(AddressPair.withPort(843));
    }
}
