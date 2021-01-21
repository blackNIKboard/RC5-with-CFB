package main;

import static main.Analysis.errorDistribution;
import static main.CypherTester.*;

public class Main {
    public static void main(String[] args) {
        testImage();
        System.out.println("------------------");

        testTxt();
        System.out.println("------------------");

        testCFB(20);
        System.out.println("------------------");

        errorDistribution(1);
    }
}
