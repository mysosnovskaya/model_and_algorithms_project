package tmp;/*
+ 1. Что значит на примере "передать значение в метод/конструктор другого класса"
+ 2. "Обратиться к нужным полям"
+ 3. "Полю класса name должно быть присвоено переданное значение hamsterName" - что-то из такого. я не пойму никак что к чему относится и как это исправить
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) throws IOException, URISyntaxException {
//        System.out.
//        URL url = Test.class.getClassLoader().getResource("test.csv");
//        File file = null;
//        try {
//            file = new File(url.toURI());
//        } catch (URISyntaxException e) {
//            file = new File(url.getPath());
//        }


        try (var stream = Files.list(Path.of(""))) {
            stream.collect(Collectors.toList());
        } catch (IOException e) {
           e.printStackTrace();
        }






        Path path = Path.of(ClassLoader.getSystemClassLoader().getResource("test.csv").toURI());
        try (FileWriter fileWriter = new FileWriter(String.valueOf(path))) {
            fileWriter.write("jk");
        }
        new OutputStreamWriter(System.out, StandardCharsets.UTF_8);
        System.out.println(isPalindromeLine(" Молебен о коне белом "));
        HashMap<Integer, Cat> cats = new HashMap<>();
        cats.put(1, new Cat());
        cats.get(1).list.add("fdjks");
        cats.put(2, new Cat());

        Cat cat = cats.get(1);
        cats.remove(1);
        System.out.println(cat.list.get(0));

        ArrayList<String>[] array = new ArrayList[2];
        array[0] = new ArrayList<>();
        array[1] = new ArrayList<>();
        array[0].add("123");

        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(1, 5);



        Map<Integer, Integer> map = Map.of(1, 1, 2, 2, 3, 3, 4, 4, 5, 5);

        List<Integer> sum = map.values().stream()
                .sorted()
                .peek(aLong -> aLong = aLong + 10)
                .collect(Collectors.toList());


        Scanner scanner = new Scanner(System.in);
        int i1 = scanner.nextInt();
        System.out.println(i1);
        String s1 = scanner.nextLine();
        System.out.println(s1);
        int i2 = scanner.nextInt();
        System.out.println(i2);

        ArrayList<Integer> arrayList2 =  new ArrayList<>();
        arrayList2.contains(3);







        arrayList2.add(1);
        arrayList2.add(0);
        arrayList2.add(290);
        arrayList2.add(500);
        arrayList2.remove((Integer) 1);
        double maxSpeed = Double.parseDouble("0.0");






        String fileContent = readFileContentsOrNull("test.csv");
        String separator = ";";
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.clear();
        Collections.addAll(arrayList, fileContent.split(separator));


        int a;
        a = 10;

        System.out.println("Привет!");

        Hamster hamster = new Hamster("Женя", 10, 100, "Рыжий");
        Hamster hamster2 = new Hamster("Коля", 3, 50, "Серый");
        System.out.println(hamster.age);

//        hamster.age = 10;
//        hamster.name = "Женя";
        hamster.eat(5);
        hamster.sleep();

        Cat cat2 = new Cat();
        cat2.age = 5;
    }

    public static boolean isPalindromeLine(String str) {
        str = str.toLowerCase();
        StringBuilder build = new StringBuilder(str);
        int index = 0;
        for (int i = 0; i < build.length(); i++) {
            if (Character.isWhitespace(build.charAt(index))) {
                build.replace(index, index + 1, "");

            }
            index++;
        }

        StringBuilder newString = new StringBuilder(build);
        newString.reverse();
        if (build.toString().replaceAll(" ","").equals(newString.toString().replaceAll(" ",""))) {
            return true;
        } else {
            return false;
        }
    }

    private static String readFileContentsOrNull(String path)
    {
        try {
            return Files.readString(Path.of("D:\\Projects\\processors-energy\\src\\main\\resources\\" + path));
        } catch (IOException e) {
            System.out.println("Невозможно прочитать файл с месячным отчётом. Возможно, файл не находится в нужной директории.");
            return null;
        }
    }

    public void t() {
        for (int i = 0; i < 10; i++) {
            System.out.println(i);
        }
    }
}

class Cat {
    String name;
    int age;
    List<String> list = new ArrayList<>();
}

class Hamster {
    String name;
    int age;
    int weight;
    String color;

    Hamster(String hamsterName, int hamsterAge, int hamsterWeight, String hamsterColor) {
        name = hamsterName;
        age = hamsterAge;
        weight = hamsterWeight;
        color = hamsterColor;
    }

    public void eat(int portion) {
        weight = weight + portion;
    }

    public void sleep() {

    }


}

class Test2 {
}
