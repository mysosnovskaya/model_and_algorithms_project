package tmp;

public class Praktikum {
    public static void main(String[] args) {
        byte a = 10;
        byte b = 20;
        System.out.println(findMax(a, b));
    }
    private static byte findMax(byte firstNumber, byte secondNumber) {
        int findOfMax = Integer.max(firstNumber, secondNumber);
        return (byte) Integer.max(firstNumber, secondNumber);
    }
}