package wang.relish.textsample;

import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.ListView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import androidx.annotation.NonNull;

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
     * @param maximum  候选记录最多可显示的条数(现在定的是3,不知道以后会不会改)
     */
    public static int setDropDownHeight(AutoCompleteTextView textView, int maximum) {
        try {
            ListPopupWindow mPopup = getListPopupWindow(textView);
            if (mPopup == null) return -1;
            int buildDropDown = buildDropDown(mPopup);
            int height = setListItemMaximum(maximum, mPopup);
            if (buildDropDown < 0 && height < 0) return -1;
            int finalHeight;
            if (buildDropDown < 0) {
                finalHeight = height;
            } else if (height < 0) {
                finalHeight = buildDropDown;
            } else {
                finalHeight = height;//Math.min(height, buildDropDown);
            }
            textView.setDropDownHeight(finalHeight);
            return maxHeight;
        } catch (Exception ignore) {
        }
        return -1;
    }

    // ListPopupWindow#buildDropDown():int
    private static int buildDropDown(@NonNull ListPopupWindow popup) {
        try {
            Class<? extends ListPopupWindow> clazz = popup.getClass();
            Method buildDropDown = clazz.getDeclaredMethod("buildDropDown");
            buildDropDown.setAccessible(true);
            return (int) buildDropDown.invoke(popup);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return -2;// -1 代表已存在；-2代表异常
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

    private static int setListItemMaximum(@SuppressWarnings("SameParameterValue") int maximum, ListPopupWindow object) {
        try {
            Class<?> aClass = object.getClass();
            Field field = aClass.getDeclaredField("mDropDownList");
            field.setAccessible(true);
            ListView dropDownListView = (ListView) field.get(object);
            return setListViewHeight(dropDownListView, maximum);
        } catch (Exception ignore) {
        }
        return -1;
    }


    private static int maxHeight = 0;

    /**
     * 设置ListView的最大高度为{@param count}条数据的高度
     *
     * @param listView AutoCompleteTextView 的 mPopup(ListPopupWindow) 的 mDropDownList(DropDownListView是个ListView)
     * @param count    item数量
     */
    private static int setListViewHeight(ListView listView, int count) {
        int height = -1;
        ListAdapter listAdapter = listView.getAdapter(); //得到ListView 添加的适配器
        if (listAdapter == null) return height;

        View itemView = listAdapter.getView(0, null, listView); //获取其中的一项
        //进行这一项的测量，为什么加这一步，具体分析可以参考 https://www.jianshu.com/p/dbd6afb2c890这篇文章
        itemView.measure(0, 0);
        int itemHeight = itemView.getMeasuredHeight(); //一项的高度
        maxHeight = itemHeight * count;
        int itemCount = listAdapter.getCount();//得到总的项数
        if (itemCount <= count) {
            height = itemCount * itemHeight;
        } else {
            height = count * itemHeight;
        }
        return height;
    }
}
