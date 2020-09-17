package android.databinding;

import android.view.View;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MergedDataBinderMapper extends DataBinderMapper {
    private List<DataBinderMapper> mMappers = new CopyOnWriteArrayList();

    /* access modifiers changed from: protected */
    public void addMapper(DataBinderMapper mapper) {
        this.mMappers.add(mapper);
    }

    public ViewDataBinding getDataBinder(DataBindingComponent bindingComponent, View view, int layoutId) {
        for (DataBinderMapper mapper : this.mMappers) {
            ViewDataBinding result = mapper.getDataBinder(bindingComponent, view, layoutId);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public ViewDataBinding getDataBinder(DataBindingComponent bindingComponent, View[] view, int layoutId) {
        for (DataBinderMapper mapper : this.mMappers) {
            ViewDataBinding result = mapper.getDataBinder(bindingComponent, view, layoutId);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public int getLayoutId(String tag) {
        for (DataBinderMapper mapper : this.mMappers) {
            int result = mapper.getLayoutId(tag);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    public String convertBrIdToString(int id) {
        for (DataBinderMapper mapper : this.mMappers) {
            String result = mapper.convertBrIdToString(id);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
