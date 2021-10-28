package com.example.testalgorithm;

import com.example.library.LogUtil;

import java.util.Arrays;

public class AlgorithmUtil {
    private static final String TAG = AlgorithmUtil.class.getSimpleName();

    /**
     * QuickSort
     */
    public static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int index = partition(arr, low, high);
            quickSort(arr, low, index - 1);
            quickSort(arr, index + 1, high);
        }

    }

    private static int partition(int[] arr, int start, int end) {
        int left = start;
        int right = end;
        int mid = arr[start];
        LogUtil.e(TAG, "left:" + start + " right:" + end);
        while (left < right) {
            while (left < right && arr[right] >= mid) {
                right--;
            }
            arr[left] = arr[right];
            while (left < right && arr[left] <= mid) {
                left++;
            }
            arr[right] = arr[left];
        }
        arr[left] = mid;
        LogUtil.e(TAG, Arrays.toString(arr));
        return left;

    }

}
