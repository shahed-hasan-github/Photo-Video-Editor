package infiapp.com.videomaker.gridview;

import android.view.View;
import java.util.List;

public class AbsGridUtils {


    private AbsGridUtils() {
    }

    public static void reorder(List<Object> list, int indexFrom, int indexTwo) {
        Object obj = list.remove(indexFrom);
        list.add(indexTwo, obj);
    }

    /**
     * Swap item in <code>list</code> at position <code>firstIndex</code> with item at position <code>secondIndex</code>
     *
     * @param list        The list in which to swap the items.
     * @param firstIndex  The position of the first item in the list.
     * @param secondIndex The position of the second item in the list.
     */
    public static void swap(List<Object> list, int firstIndex, int secondIndex) {
        Object firstObject = list.get(firstIndex);
        Object secondObject = list.get(secondIndex);
        list.set(firstIndex, secondObject);
        list.set(secondIndex, firstObject);
    }

    public static float getViewX(View view) {
        return Math.abs((view.getRight() - view.getLeft()) / 2);
    }

    public static float getViewY(View view) {
        return Math.abs((view.getBottom() - view.getTop()) / 2);
    }
}
