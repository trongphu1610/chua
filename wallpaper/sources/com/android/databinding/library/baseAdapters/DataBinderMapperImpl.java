package com.android.databinding.library.baseAdapters;

import android.databinding.DataBinderMapper;
import android.databinding.DataBindingComponent;
import android.databinding.ViewDataBinding;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;

public class DataBinderMapperImpl extends DataBinderMapper {
    private static final SparseIntArray INTERNAL_LAYOUT_ID_LOOKUP = new SparseIntArray(0);

    public ViewDataBinding getDataBinder(DataBindingComponent component, View view, int layoutId) {
        if (INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId) <= 0 || view.getTag() != null) {
            return null;
        }
        throw new RuntimeException("view must have a tag");
    }

    public ViewDataBinding getDataBinder(DataBindingComponent component, View[] views, int layoutId) {
        if (views == null || views.length == 0 || INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId) <= 0 || views[0].getTag() != null) {
            return null;
        }
        throw new RuntimeException("view must have a tag");
    }

    public int getLayoutId(String tag) {
        if (tag == null) {
            return 0;
        }
        int hashCode = tag.hashCode();
        return 0;
    }

    public String convertBrIdToString(int id) {
        return InnerBrLookup.sKeys.get(id);
    }

    private static class InnerBrLookup {
        static final SparseArray<String> sKeys = new SparseArray<>(1);

        private InnerBrLookup() {
        }

        static {
            sKeys.put(BR._all, "_all");
        }
    }
}
