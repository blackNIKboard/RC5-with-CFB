package main;

import static main.Analysis.errorDistribution;
import static main.CypherTester.*;

public class Main {
    public static void main(String[] args) {
        testECBImage();
        System.out.println("------------------");

        testCFBImage();
        System.out.println("------------------");

        testECBTxt();
        System.out.println("------------------");

        testCFBTxt();
        System.out.println("------------------");

        testCFB(20);
        System.out.println("------------------");

        errorDistribution(3);
    }
}
