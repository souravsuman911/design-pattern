package internal.designPattern.external.interview;//package internal;
//
//public class MainClient {
//    static int hIndex = Integer.MIN_VALUE;
//    public static void findHIndex(int[] arr, int start, int end, int n){
//        if(start > end){
//            return;
//        }
//        int mid = (start + end) / 2;
//
////        System.out.println(" " + mid);
//
//        if(arr[mid] >= mid){
//            hIndex = Math.max(hIndex, mid);
//            findHIndex(arr, mid + 1, end, n);
//        }
//        else{
//            findHIndex(arr, start, mid - 1, n);
//        }
//    }
//
//    public static void main(String[] args) {
//
//        int[] arr = {0,1,4,5,6};
//        int n = 5;
//
//        int[] arr1 = {1, 1, 3};
//        findHIndex(arr1, 0, 2, 3);
////
//        System.out.println(hIndex - 1);
//
//    }
//}
