package internal.designPattern.external.concepts.stream;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamPractice1 {
    public static void main(String[] args) {
        // Convert a List<Integer> into a List<Integer> containing only even numbers using Stream API.
        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 2, 4, 1, 5);
        List<Integer> evenIntegers = integers.stream()
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toList());

        System.out.println(evenIntegers);

        // Given a List<String>, convert all strings to uppercase.
        List<String> fruits = Arrays.asList("apple", "banana", "cherry");
        fruits = fruits.stream()
                .map(fruit -> fruit.toUpperCase())
                .collect(Collectors.toList());

        System.out.println(fruits);

        // Count how many numbers in a list are greater than 50.
        List<Integer> integerList = Stream.generate(() -> (int) (Math.random() * 100))
                .limit(10)
                .collect(Collectors.toList());
        System.out.println(integerList);

        long countG50 = integerList.stream().filter(n -> n > 50).count();
        System.out.println(countG50);

        // Remove duplicate elements from a list using streams.
        System.out.println(integers);
        List<Integer> distinctIntegers = integers.stream()
                .distinct().collect(Collectors.toList());

        System.out.println(distinctIntegers);

        // Sort a list of integers in ascending order using streams.
        System.out.println(integerList);

        List<Integer> sortedInteger = integerList.stream().sorted().collect(Collectors.toList());
        System.out.println(sortedInteger);

        // Sort a list of strings in descending order.
        List<String> words = Arrays.asList("aa", "ab", "ca", "cc", "bb", "a", "bca");
        List<String> revSortedWords = words.stream().sorted((a, b) -> b.compareTo(a)).collect(Collectors.toList());
        System.out.println(words);
        System.out.println(revSortedWords);
        List<String> sortedWords = words.stream().sorted().collect(Collectors.toList());
        System.out.println(sortedWords);

        // Find the first element in a list that is divisible by 5.
        System.out.println(integerList);
        int fDiv5 = integerList.stream().filter(n -> n % 5 == 0).findFirst().orElse(-1);
        System.out.println(fDiv5);
        // can use Optional<Integer> also in case no integer found div by 5
        Optional<Integer> fDiv5o = integerList.stream().filter(n -> n % 5 == 0).findFirst();
        System.out.println(fDiv5o);

        // Check if any number in a list is negative.
        List<Integer> randomNumbers = Stream.generate(() -> (int) (Math.random() * 100) - 50).limit(10).collect(Collectors.toList());
        System.out.println(randomNumbers);
        boolean haveNegative = randomNumbers.stream().anyMatch(n -> n < 0);
        System.out.println(haveNegative);

        // Print all elements of a list using Stream API.
        System.out.println(fruits);
        fruits.stream().forEach(System.out::println);


        // prints duplicate elements
        List<Integer> arr = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 2, 4, 1, 5);
        Set<Integer> visited = new HashSet<>();
        Set<Integer> duplicate =  arr.stream()
                .filter(n -> !visited.add(n))
                .collect(Collectors.toSet());

        System.out.println(duplicate);
    }
}
