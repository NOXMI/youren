package com.noxmi.youren.basicmap;

public class weatherpic {
    public static int picpick(String Str) {
        switch (Str) {
            case "晴":
                return 22;
            case "少云":
                return 8;
            case "晴间多云":
                return 24;
            case "多云":
                return 19;
            case "阴":
                return 8;
            case "有风":
                return 51;
            case "平静":
                return 2;
            case "微风":
                return 51;
            case "和风":
                return 51;
            case "清风":
                return 51;
            case "强风/劲风":
                return 51;
            case "疾风":
                return 30;
            case "大风":
                return 30;
            case "烈风":
                return 30;
            case "风暴":
                return 30;
            case "狂爆风":
                return 30;
            case "飓风":
                return 30;
            case "热带风暴":
                return 51;
            case "中度霾":
                return 7;
            case "重度霾":
                return 6;
            case "严重霾":
                return 6;
            case "阵雨":
                return 12;
            case "雷阵雨":
                return 10;
            case "雷阵雨并伴有冰雹":
                return 21;
            case "小雨":
                return 11;
            case "中雨":
                return 12;
            case "大雨":
                return 12;
            case "暴雨":
                return 12;
            case "大暴雨":
                return 12;
            case "特大暴雨":
                return 12;
            case "强阵雨":
                return 12;
            case "强雷阵雨":
                return 21;
            case "极端降雨":
                return 12;
            case "毛毛雨/细雨":
                return 11;
            case "小雨-中雨":
                return 11;
            case "中雨-大雨":
                return 12;
            case "大雨-暴雨":
                return 12;
            case "暴雨-大暴雨":
                return 12;
            case "大暴雨-特大暴雨":
                return 12;
            case "雨雪天气":
                return 15;
            case "雨夹雪":
                return 18;
            case "阵雨夹雪":
                return 18;
            case "冻雨":
                return 18;
            case "阵雪":
                return 17;
            case "小雪":
                return 16;
            case "中雪":
                return 17;
            case "大雪":
                return 17;
            case "暴雪":
                return 17;
            case "小雪-中雪":
                return 16;
            case "中雪-大雪":
                return 17;
            case "大雪-暴雪":
                return 17;
            case "浮尘":
                return 34;
            case "扬沙":
                return 30;
            case "沙尘暴":
                return 30;
            case "强沙尘暴":
                return 30;
            case "龙卷风":
                return 30;
            case "雾":
                return 7;
            case "浓雾":
                return 7;
            case "强浓雾":
                return 6;
            case "轻雾":
                return 7;
            case "大雾":
                return 7;
            case "特强浓雾":
                return 6;
            case "热":
                return 22;
            case "冷":
                return 1;
            case "未知":
                return 39;
        }
        return 39;
    }
}
