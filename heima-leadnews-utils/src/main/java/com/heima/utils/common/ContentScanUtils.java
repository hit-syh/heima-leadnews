package com.heima.utils.common;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ContentScanUtils {
    public static void main(String[] args) {
        List<String> strings = Arrays.asList("史云浩", "我,毛主席","天安门","傻逼");
        System.out.println(ContentScanUtils.textScan(strings));
    }
    private static final SensitiveWordBs textScanUtils = SensitiveWordBs.newInstance();

    public static List<String> textScan(String text)
    {
        return textScanUtils.findAll(text.replaceAll (" [a-zA-Z]",""));
    }
    public static List<String> textScan(List<String> text)
    {
        List<String> collect = text.stream().map(s ->textScanUtils.findAll(s.replaceAll(" [a-zA-Z]", ""))).flatMap(List::stream).collect(Collectors.toList());
        return collect;
    }
    public static boolean imageScan(String image)
    {
        return true;
    }
    public static boolean imageScan(List<String> image)
    {
        return true;
    }

}
