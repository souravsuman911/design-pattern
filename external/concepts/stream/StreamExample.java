package internal.designPattern.external.concepts.stream;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamExample {
    public static void main(String[] args) {
        // Defining Stream

        // Collection class directly to Stream
        List<String> fruits = Arrays.asList("Apple", "Banana", "Cherry");
        Stream<String> fruitsStream = fruits.stream();
        System.out.println(fruitsStream.collect(Collectors.toList()));

        // use Arrays to Stream
        String[] fruitsArr = {"Apple", "Banana", "Cherry"};
        Stream<String> fruitsArrStream = Arrays.stream(fruitsArr);
        System.out.println(fruitsArrStream.collect(Collectors.toList()));

        // Directly creating Stream
        Stream<String> fruitsDStream = Stream.of("Apple", "Banana", "Cherry");
        System.out.println(fruitsDStream.collect(Collectors.toList()));

        // Stream with the help of iterator(seed, unaryOperator<Integer>)
        Stream<Integer> integerStream = Stream.iterate(0, n -> n + 1).limit(50);
        System.out.println(integerStream.collect(Collectors.toList()));

        // Stream with the help of iterator(seed, predicate, unaryOperator<Integer>)
        // predicate - (if this returns false at any case, iteration stops) - basically like loop condition
        Stream<Integer> integerStream1 = Stream.iterate(0, n -> n < 50, n -> n + 1);
        System.out.println(integerStream1.collect(Collectors.toList()));

        // Stream with the help of generate(Supplier<?>)
        Stream<Integer> integerStreamG = Stream.generate(() -> (int) (Math.random() * 100)).limit(100);
        System.out.println(integerStreamG.collect(Collectors.toList()));


    }
}
