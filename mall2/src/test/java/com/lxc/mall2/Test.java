package com.lxc.mall2;

import java.util.LinkedHashMap;

/**
 * Created by 82138 on 2019/5/12.
 */
public class Test {
    static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= 65535) ? 65535 : n + 1;
    }

    public static void main(String[] args) {
        System.out.println(tableSizeFor(9) );
        LinkedHashMap linkedHashMap = new LinkedHashMap();
    }
}
