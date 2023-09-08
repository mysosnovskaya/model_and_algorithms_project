package tmp;

import java.util.Random;
import java.util.Scanner;

public class Commenting {







                /**
                 * Находит максимальное отрицательно число в массиве aa. Если такого нет, то возвращает g.
                 * @param aa - массив целых чисел, в котором нужно найти максимальное отрицательное
                 * @param m - минимальное возможное значение
                 * @param g - значение, которое нужно вернуть, если максимального не будет найдено
                 * @return максимальное отрицательное число из массива aa или g,
                 * если максимального не будет найдено
                 */
                public int doSomething(int[] aa, int m, int g) {
                    int t = m;
                    boolean k = false;
                    for (int i = 0; i < aa.length; i++) {
                        if (aa[i] > t && aa[i] < 0) {
                            t = aa[i];
                            k = true;
                        }
                    }
                    if (k) {
                        return t;
                    } else {
                        return g;
                    }
                }






                public int findMaximumNegativeOrDefault(
                        int[] numbers, int minPossibleValue, int defaultValue
                ) {
                    int maxNumber = minPossibleValue;
                    boolean isMaxFound = false;
                    for (int i = 0; i < numbers.length; i++) {
                        if (numbers[i] > maxNumber && numbers[i] < 0) {
                            maxNumber = numbers[i];
                            isMaxFound = true;
                        }
                    }
                    if (isMaxFound) {
                        return maxNumber;
                    } else {
                        return defaultValue;
                    }
                }

                public void guessAnimal() {
                    Scanner scanner = new Scanner(System.in);
                    String color = scanner.next();
                    String sound = scanner.next();
                    if (sound.equals("Мяу") && color.equals("Серый")) {
                        System.out.println("Это серый кот!");
                    } else if (sound.equals("Гав") && color.equals("Белый")) {
                        System.out.println("Это белая собака!");
                    } else if ((sound.equals("Му") || sound.equals("Му-у"))) {
                        System.out.println("Это точно корова!");
                    }
                }

    public static void sayHelloByTime(int time) {
        if (time < 6) {
            System.out.println("Доброй ночи!");
        } else if (time < 12) {
            System.out.println("Доброе утро!");
        } else if (time < 18) {
            System.out.println("Добрый день!");
        } else if (time < 22) {
            System.out.println("Добрый вечер!");
        } else {
            System.out.println("Доброй ночи!");
        }
    }

    public static void sayHelloByTime2(int time) {
        if (time >= 6 && time < 12) {
            System.out.println("Доброе утро!");
        } else if (time >= 22 || time < 6) {
            System.out.println("Доброй ночи!");
        } else if (time < 18 && time >= 12) {
            System.out.println("Добрый день!");
        } else {
            System.out.println("Доброй вечер!");
        }
    }

    public static void main(String[] args) {
        System.out.println(t(1));
    }

    public static int t(int a) {
                    return a = 5;
    }
}
