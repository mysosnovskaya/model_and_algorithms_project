package tmp;

import java.util.Scanner;

public class AreaOfVisibility {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Какой сегдоня день?");
        String day = scanner.next();

        String mainVariable = "Method main";
        for (int i = 0; i < 3; i++) {
            String mainForVariable = "Method main, for";
            if (i > 1) {
                String mainForIfVariable = "Method main, for, if";
            } else {
                String mainForIfVariable = "Method main, for, if->else";
            }
        }
    }

    public static void test() {
        String testVariable = "Method test";
        int i = 0;
        while (i < 5) {
            String testWhileVariable = "Method test, while";
            i++;
        }
    }
}
