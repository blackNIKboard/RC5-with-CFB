package main;

import rc5.*;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        // write your code here
        String key = "testo4kozhopa";
        String key1 = "sdjashfdkjagtejhwabkjgshag";
        var test64 = "tX5JrH7OiDXEpsrm".getBytes();
        var test128 = "tX5JrH7OiDXEpsrmtX5JrH7OiDXEpsrmtX5JrH7OiDXEpsrmtX5JrH7OiDXEpsrm".getBytes();
        RC5 test = new RC5(key1.getBytes(), 12);
        CFB test1 = new CFB(key1.getBytes(), 12, 2);
//        byte[] encBlock = test.encryptBlock(test64);
        byte[] encBlock = test1.encipherCFB(test128);
//        byte[] decBlock = test.decryptBlock(encBlock);
        byte[] decBlock = test1.decipherCFB(encBlock);

        System.out.println(new String(test128));
        System.out.println(new String(encBlock));
        System.out.println(new String(decBlock));
    }
}
