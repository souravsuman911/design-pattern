package internal.designPattern.abstractFactoryPattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainClient {

//    GuiFactory guiFactory =

    public static void main(String[] args) {
        int[] a = {1, 2, 3};
        Arrays.sort(a);
        int idx = Arrays.binarySearch(a, 2);

        List<Integer> b = new ArrayList<>();
        int idx2 = Collections.binarySearch(b, 10);


    }


}
