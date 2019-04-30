package android.widget;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.ListView;

import java.lang.reflect.Field;

/**
 * 动态修改AutoCompleteTextView的账号候选列表的高度
 *
 * @author Relish Wang
 * @since 2018/12/11
 */
public class ACTVHeightUtil {

    /**
     * 设置AutoCompleteTextView的候选列表高度
     *
     * @param textView AutoCompleteTextView
     * @param maxCount 候选记录最多可显示的条数(现在定的是3,不知道以后会不会改)
     */
    public static boolean setDropDownHeight(AutoCompleteTextView textView, int maxCount) {
        // 反射获取ListPopupWindow实例
        ListPopupWindow mPopup = getListPopupWindow(textView);
        if (mPopup == null) {
            Log.d("setDropDownHeight","mPopup == null");
            return false;
        }
        // 反射获取DropDownListView实例
        ListView mDropDownList = getDropDownListView(mPopup);
        if (mDropDownList == null) {
            Log.d("setDropDownHeight","mDropDownList == null");
            return false;
        }
        // 获取高度(候选列表项数小于maxCount时返回WRAP_CONTENT)
        int itemHeight = getListViewItemHeight(mDropDownList, maxCount);
        if (itemHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
            textView.setDropDownHeight(itemHeight);
        } else {
            textView.setDropDownHeight(itemHeight * maxCount);
        }
        return true;
    }

    /**
     * 获取ACTV的ListPopupWindow对象
     *
     * @param textView AutoCompleteTextView
     * @return ListPopupWindow对象
     */
    private static ListPopupWindow getListPopupWindow(AutoCompleteTextView textView) {
        try {
            Class<?> aClass = textView.getClass();
            Field field = null;
            while (aClass != null) {
                try {
                    field = aClass.getDeclaredField("mPopup");
                } catch (NoSuchFieldException ignore) {
                } finally {
                    aClass = aClass.getSuperclass();
                }
                if (field != null) break;
            }
            if (field == null) return null;
            field.setAccessible(true);
            return (ListPopupWindow) field.get(textView);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取DropDownListView对象
     *
     * @param lpw ListPopupWindow
     * @return DropDownListView对象
     */
    private static ListView getDropDownListView(ListPopupWindow lpw) {
        try {
            Class<?> aClass = lpw.getClass();
            Field field = aClass.getDeclaredField("mDropDownList");
            field.setAccessible(true);
            return (ListView) field.get(lpw);
        } catch (NoSuchFieldException ignore) {
        } catch (IllegalAccessException ignore) {
        }
        return null;
    }


    /**
     * 获取ListView的一条item的高度
     *
     * @param listView DropDownListView
     * @param maxCount 候选记录最多可显示的条数(现在定的是3,不知道以后会不会改)
     * @return -2:WRAP_CONTENT; 其他值一条item的高度
     */
    private static int getListViewItemHeight(ListView listView, int maxCount) {
        ListAdapter listAdapter = listView.getAdapter(); //得到ListView 添加的适配器
        if (listAdapter == null) return -1;
        if (listAdapter.getCount() < maxCount) {
            return ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            View itemView = listAdapter.getView(0, null, listView); //获取其中的一项
            //进行这一项的测量，为什么加这一步，具体分析可以参考 https://www.jianshu.com/p/dbd6afb2c890这篇文章
            itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            return itemView.getMeasuredHeight(); //item的高度
        }
    }
}
