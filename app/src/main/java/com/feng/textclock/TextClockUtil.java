package com.feng.textclock;

/**
 * @author Feng Zhaohao
 * Created on 2019/9/21
 */
public class TextClockUtil {
    /**
     * 将数字转换为文字形式，数字范围：1 - 59
     *
     * @param num
     * @return
     */
    public static String numToText(int num) {
        String[] strs = {"", "一", "二", "三", "四", "五", "六", "七", "八", "九"};

        if (num < 10) {
            return strs[num];
        } else if (num < 20) {
            // 10, 11, ..., 19
            return "十" + strs[num-10];
        } else if (num < 30) {
            return "二十" + strs[num-20];
        } else if (num < 40) {
            return "三十" + strs[num-30];
        } else if (num < 50) {
            return "四十" + strs[num-40];
        } else {
            return "五十" + strs[num-50];
        }
    }
}
