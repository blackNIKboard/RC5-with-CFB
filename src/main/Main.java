package main;

import java.util.Arrays;

import static main.Analyzer.*;
import static main.CypherTester.*;

public class Main {
    public static void main(String[] args) {
//        testECBImage();
//        System.out.println("------------------");
//
//        testCFBImage();
//        System.out.println("------------------");
//
//        testECBTxt();
//        System.out.println("------------------");
//
//        testCFBTxt();
//        System.out.println("------------------");
//
//        testCFB(5);
//        System.out.println("------------------");

//        testCorrelation();
//        System.out.println("------------------");

//        errorDistribution(3);

        byte[] data = StringGenerator.generateString(16).getBytes();
        System.out.println(new String(data));
        testAutoCorrelation(new byte[]{0x00, 0x00}, data, "zeros.txt");
        testAutoCorrelation(new byte[]{(byte) 0xFF, (byte) 0xFF}, data, "ones.txt");
        testAutoCorrelation(StringGenerator.generateString(16).getBytes(), data, "random.txt");

    }
}
