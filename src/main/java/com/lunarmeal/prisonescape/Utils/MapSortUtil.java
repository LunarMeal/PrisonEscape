package com.lunarmeal.prisonescape.Utils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Map排序工具类
 *
 * @author liuwangyanghdu@163.com  明明如月
 */
public class MapSortUtil {

    private static final Comparator<Map.Entry> comparatorByKeyAsc = (Map.Entry o1, Map.Entry o2) -> {
        if (o1.getKey() instanceof Comparable) {
            return ((Comparable) o1.getKey()).compareTo(o2.getKey());
        }
        throw new UnsupportedOperationException("键的类型尚未实现Comparable接口");
    };


    private static final Comparator<Map.Entry> comparatorByKeyDesc = (Map.Entry o1, Map.Entry o2) -> {
        if (o1.getKey() instanceof Comparable) {
            return ((Comparable) o2.getKey()).compareTo(o1.getKey());
        }
        throw new UnsupportedOperationException("键的类型尚未实现Comparable接口");
    };


    private static final Comparator<Map.Entry> comparatorByValueAsc = (Map.Entry o1, Map.Entry o2) -> {
        if (o1.getValue() instanceof Comparable) {
            return ((Comparable) o1.getValue()).compareTo(o2.getValue());
        }
        throw new UnsupportedOperationException("值的类型尚未实现Comparable接口");
    };


    private static final Comparator<Map.Entry> comparatorByValueDesc = (Map.Entry o1, Map.Entry o2) -> {
        if (o1.getValue() instanceof Comparable) {
            return ((Comparable) o2.getValue()).compareTo(o1.getValue());
        }
        throw new UnsupportedOperationException("值的类型尚未实现Comparable接口");
    };

    /**
     * 按键升序排列
     */
    public static <K, V> Map<K, V> sortByKeyAsc(Map<K, V> originMap) {
        if (originMap == null) {
            return null;
        }
        return sort(originMap, comparatorByKeyAsc);
    }

    /**
     * 按键降序排列
     */
    public static <K, V> Map<K, V> sortByKeyDesc(Map<K, V> originMap) {
        if (originMap == null) {
            return null;
        }
        return sort(originMap, comparatorByKeyDesc);
    }


    /**
     * 按值升序排列
     */
    public static <K, V> Map<K, V> sortByValueAsc(Map<K, V> originMap) {
        if (originMap == null) {
            return null;
        }
        return sort(originMap, comparatorByValueAsc);
    }

    /**
     * 按值降序排列
     */
    public static <K, V> Map<K, V> sortByValueDesc(Map<K, V> originMap) {
        if (originMap == null) {
            return null;
        }
        return sort(originMap, comparatorByValueDesc);
    }

    public static List<Map.Entry<String, Integer>> getTopThreeEntries(Map<String, Integer> map) {
        // 创建一个列表并将 Map 条目添加到列表中
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(map.entrySet());

        // 使用比较器按值降序排序
        entryList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        // 获取前三个条目（如果有的话）
        return entryList.subList(0, Math.min(3, entryList.size()));
    }

    private static <K, V> Map<K, V> sort(Map<K, V> originMap, Comparator<Map.Entry> comparator) {
        return originMap.entrySet()
                .stream()
                .sorted(comparator)
                .collect(
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));
    }

}
