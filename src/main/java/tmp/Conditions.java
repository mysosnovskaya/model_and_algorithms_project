package tmp;

public class Conditions {
    public static int main(String[] args) {
        int film1 = 100;
        int film2 = 200;
        int film3 = 150;

        int maxIncome = 0;
        if (film1 > film2 && film1 > film3) {
            maxIncome = film1;
        } else if (film2 > film1 && film2 > film3) {
            maxIncome = film2;
        } else {
            maxIncome = film3;
        }
        return 0;
    }
}
