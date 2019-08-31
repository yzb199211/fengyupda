package com.yyy.fengyupda.font;

import android.content.Context;
import android.graphics.Typeface;

import com.yyy.fengyupda.R;


public class FontUtil {
    public static Typeface getTypeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "iconfont.ttf");
    }

    public static int getFont(String string) {
        switch (string) {
            case "icon-barcode":
                return R.string.icon_barcode;
            case "icon-warehouse":
                return R.string.icon_warehouse;
            case "icon-word":
                return R.string.icon_word;
            case "icon-order":
                return R.string.icon_order;
            case "icon-document":
                return R.string.icon_document;
            case "icon-finishWork":
                return R.string.icon_finishWork;
            case "icon-finance":
                return R.string.icon_finance;
            case "icon-stock":
                return R.string.icon_stock;
            case "icon-sale":
                return R.string.icon_sale;
            case "icon-purchase":
                return R.string.icon_purchase;
            case "icon-basedata":
                return R.string.icon_basedata;
            case "icon-system":
                return R.string.icon_system;
            case "icon-all":
                return R.string.icon_all;
            case "icon-add":
                return R.string.icon_add;
            case "icon-edit":
                return R.string.icon_edit;
            case "icon-copy":
                return R.string.icon_copy;
            case "icon-clear":
                return R.string.icon_clear;
            case "icon-remove":
                return R.string.icon_remove;
            case "icon-save":
                return R.string.icon_save;
            case "icon-cut":
                return R.string.icon_cut;
            case "icon-ok":
                return R.string.icon_ok;
            case "icon-no":
                return R.string.icon_no;
            case "icon-import":
                return R.string.icon_import;
            case "icon-cancel":
                return R.string.icon_cancel;
            case "icon-reload":
                return R.string.icon_reload;
            case "icon-search":
                return R.string.icon_refresh;
            case "icon-print":
                return R.string.icon_print;
            case "icon-help":
                return R.string.icon_help;
            case "icon-undo":
                return R.string.icon_undo;
            case "icon-redo":
                return R.string.icon_redo;
            case "icon-back":
                return R.string.icon_back;
            case "icon-sum":
                return R.string.icon_sum;
            case "icon-tip":
                return R.string.icon_tip;
            case "icon-filter":
                return R.string.icon_filter;
            case "icon-man":
                return R.string.icon_man;
            case "icon-lock":
                return R.string.icon_lock;
            case "icon-next":
                return R.string.icon_next;
            case "icon-preview":
                return R.string.icon_preview;
            case "icon-todo":
                return R.string.icon_todo;
            case "icon-remind":
                return R.string.icon_remind;
            case "icon-gg":
                return R.string.icon_gg;
            case "icon-calendar":
                return R.string.icon_calendar;
            case "icon-job":
                return R.string.icon_job;
            case "icon-chart":
                return R.string.icon_chart;
            case "icon-home":
                return R.string.icon_home;
            case "icon-associate":
                return R.string.icon_associate;
            case "icon-export":
                return R.string.icon_export;
            case "icon-default":
                return R.string.icon_default;
            case "icon-book":
                return R.string.icon_book;
            case "icon-list":
                return R.string.icon_list;
            case "icon-group":
                return R.string.icon_group;
            default:
                return R.string.icon_all;
        }
    }
//    public static int getImg(String string) {
//        switch (string) {
//            case "icon-barcode":
//                return R.mipmap.icon_barcode;
//            case "icon-warehouse":
//                return R.mipmap.icon_warehouse;
//            case "icon-word":
//                return R.mipmap.icon_word;
//            case "icon-order":
//                return R.mipmap.icon_order;
//            case "icon-document":
//                return R.string.icon_document;
//            case "icon-finishWork":
//                return R.mipmap.icon_finishwork;
//            case "icon-finance":
//                return R.mipmap.icon_finance;
//            case "icon-stock":
//                return R.mipmap.icon_stock;
//            case "icon-sale":
//                return R.mipmap.icon_sale;
//            case "icon-purchase":
//                return R.mipmap.icon_purchase;
//            case "icon-basedata":
//                return R.mipmap.icon_basedata;
//            case "icon-system":
//                return R.mipmap.icon_system;
//            case "icon-all":
//                return R.mipmap.icon_all;
//            case "icon-add":
//                return R.mipmap.icon_add;
//            case "icon-edit":
//                return R.mipmap.icon_edit3 ;
//            case "icon-copy":
//                return R.mipmap.icon_copy;
//            case "icon-clear":
//                return R.mipmap.icon_clear;
//            case "icon-remove":
//                return R.mipmap.icon_remove;
//            case "icon-save":
//                return R.mipmap.icon_save;
//            case "icon-cut":
//                return R.mipmap.icon_cut;
//            case "icon-ok":
//                return R.mipmap.icon_ok;
//            case "icon-no":
//                return R.mipmap.icon_no;
//            case "icon-import":
//                return R.mipmap.icon_import;
//            case "icon-cancel":
//                return R.mipmap.icon_cancel;
//            case "icon-reload":
//                return R.mipmap.icon_reload;
//            case "icon-search":
//                return R.mipmap.icon_refresh;
//            case "icon-print":
//                return R.mipmap.icon_print;
//            case "icon-help":
//                return R.mipmap.icon_help;
//            case "icon-undo":
//                return R.mipmap.icon_undo;
//            case "icon-redo":
//                return R.mipmap.icon_redo;
//            case "icon-back":
//                return R.mipmap.icon_back;
//            case "icon-sum":
//                return R.mipmap.icon_sum;
//            case "icon-tip":
//                return R.mipmap.icon_tip;
//            case "icon-filter":
//                return R.mipmap.icon_filter;
//            case "icon-man":
//                return R.mipmap.icon_man;
//            case "icon-lock":
//                return R.mipmap.icon_lock;
//            case "icon-next":
//                return R.mipmap.icon_next;
//            case "icon-preview":
//                return R.mipmap.icon_preview;
//            case "icon-todo":
//                return R.mipmap.icon_todo;
//            case "icon-remind":
//                return R.mipmap.icon_remind;
//            case "icon-gg":
//                return R.mipmap.icon_gg;
//            case "icon-calendar":
//                return R.mipmap.icon_calendar;
//            case "icon-job":
//                return R.mipmap.icon_job;
//            case "icon-chart":
//                return R.mipmap.icon_chart;
//            case "icon-home":
//                return R.mipmap.icon_home;
//            case "icon-associate":
//                return R.mipmap.icon_associate;
//            case "icon-export":
//                return R.mipmap.icon_export;
//            case "icon-default":
//                return R.mipmap.icon_default;
//            case "icon-book":
//                return R.mipmap.icon_book;
//            case "icon-list":
//                return R.mipmap.icon_list;
//            case "icon-group":
//                return R.mipmap.icon_group;
//            default:
//                return R.mipmap.icon_all;
//        }
//    }
}
