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
 * @param maximum  候选记录最多可显示的条数(现在定的是3)
 */
public static int setDropDownHeight(AutoCompleteTextView textView, int maximum) {
    try {
        // 1 反射获取ListPopupWindow对象
        ListPopupWindow mPopup = getListPopupWindow(textView);
        if (mPopup == null) return -1;
        // 2 反射调用buildDropDown方法获取列表高度
        int buildDropDown = buildDropDown(mPopup);
        if (buildDropDown < 0) buildDropDown = Integer.MAX_VALUE;
        // 3 反射获取DropDownListView对象(DropDownListView被标注了@hide, 故只好用其父类ListView接收[向上转型])
        ListView mDropDownList = getDropDownListView(mPopup);
        if (mDropDownList == null) return -1;
        // 4 测量出一条item的高度
        int itemHeight = getListViewItemHeight(mDropDownList);
        // 5 关键代码: 列表总高度 和 单条Item高度*设定的Item数量 之间取最小值
        int height = Math.min(buildDropDown, itemHeight * maximum);
        textView.setDropDownHeight(height);
        return height;
    } catch (Exception ignore) {
    }
    return -1;
}

    /**
     * 调用ListPopupWindow的buildDropDown()方法
     * ListPopupWindow#buildDropDown():int
     *
     * @param popup ListPopupWindow
     * @return 高度
     */
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
            // 当被调用的方法的内部抛出了异常而没有被捕获时，将由此异常接收！！！
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
     * @return 一条item的高度
     */
    private static int getListViewItemHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter(); //得到ListView 添加的适配器
        if (listAdapter == null) return -1;
        View itemView = listAdapter.getView(0, null, listView); // 获取其中的一项
        itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED); // 进行这一项的测量
        return itemView.getMeasuredHeight();
    }
}
