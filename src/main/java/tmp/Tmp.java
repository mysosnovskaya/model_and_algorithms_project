package tmp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class Tmp {
    public static void main(String[] args) throws IOException {
        try (FileReader bufferedReader = new FileReader("result.txt")) {

            var map = Map.of(
                    1, List.of("один", "единица", "раз"),
                    2, List.of("два", "двойка")
            );

            Map<Integer, List<String>> map2 = Map.of(
                    1, List.of("один", "единица", "раз"),
                    2, List.of("два", "двойка")
            );




        }
    }
}
