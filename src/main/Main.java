package main;

import rc5.*;

public class Main {

    public static void main(String[] args) {
        // write your code here
        String key = "testo4kozhopa";
        String key1 = "sdjashfdkjagtejhwabkjgshag";
        var test64 = "tX5JrH7OiDXEpsrm".getBytes();
        RC5 test = new RC5(key1.getBytes(), 12);
        byte[] encBlock = test.encryptBlock(test64);
        byte[] decBlock = test.decryptBlock(encBlock);

        System.out.println(new String(test64));
        System.out.println(new String(encBlock));
        System.out.println(new String(decBlock));
    }
}
