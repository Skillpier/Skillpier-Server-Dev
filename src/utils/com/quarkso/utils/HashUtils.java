package com.quarkso.utils;

public class HashUtils {
	// 32位FNV算法
    static int M_SHIFT = 0;
    static int M_MASK = 0x8765fed1;
    /**//**
    * 32位的FNV算法
    * @param data 数组
    * @return int值
    */
    public static int FNVHash(byte[] data)
    {
        int hash = (int)2166136261L;
        for(byte b : data)
            hash = (hash * 16777619) ^ b;
        if (M_SHIFT == 0)
            return hash;
        return (hash ^ (hash >> M_SHIFT)) & M_MASK;
    }
    /**//**
    * 改进的32位FNV算法1
    * @param data 数组
    * @return int值
    */
    public static int FNVHash1(byte[] data)
    {
        final int p = 16777619;
        int hash = (int)2166136261L;
        for(byte b:data)
            hash = (hash ^ b) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        return hash;
    }
    /**//**
    * 改进的32位FNV算法1
    * @param data 字符串
    * @return int值
    */
    public static int FNVHash1(String data)
    {
        final int p = 16777619;
        int hash = (int)2166136261L;
        for(int i=0;i<data.length();i++)
            hash = (hash ^ data.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        return hash;
    }
    
    /**//**
     * 混合hash算法，输出64位的值
     */
	public static long mixHash(String str)
    {
        long hash = str.hashCode();
        hash <<= 32;
        hash |= FNVHash1(str);
        return hash;
    }
}
