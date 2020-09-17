package android.databinding;

import android.view.View;
import com.dvt.monthlycalender.R;
import com.dvt.monthlycalender.databinding.FragmentRateUsBinding;
import com.dvt.monthlycalender.databinding.FragmentWallpaperBinding;
import com.dvt.monthlycalender.databinding.FragmentWallpaperDetailBinding;
import com.dvt.monthlycalender.databinding.ItemWallpaperBinding;

class DataBinderMapperImpl extends DataBinderMapper {
    public ViewDataBinding getDataBinder(DataBindingComponent bindingComponent, View view, int layoutId) {
        switch (layoutId) {
            case R.layout.fragment_rate_us:
                Object tag = view.getTag();
                if (tag == null) {
                    throw new RuntimeException("view must have a tag");
                } else if ("layout/fragment_rate_us_0".equals(tag)) {
                    return new FragmentRateUsBinding(bindingComponent, view);
                } else {
                    throw new IllegalArgumentException("The tag for fragment_rate_us is invalid. Received: " + tag);
                }
            case R.layout.fragment_wallpaper:
                Object tag2 = view.getTag();
                if (tag2 == null) {
                    throw new RuntimeException("view must have a tag");
                } else if ("layout/fragment_wallpaper_0".equals(tag2)) {
                    return new FragmentWallpaperBinding(bindingComponent, view);
                } else {
                    throw new IllegalArgumentException("The tag for fragment_wallpaper is invalid. Received: " + tag2);
                }
            case R.layout.fragment_wallpaper_detail:
                Object tag3 = view.getTag();
                if (tag3 == null) {
                    throw new RuntimeException("view must have a tag");
                } else if ("layout/fragment_wallpaper_detail_0".equals(tag3)) {
                    return new FragmentWallpaperDetailBinding(bindingComponent, view);
                } else {
                    throw new IllegalArgumentException("The tag for fragment_wallpaper_detail is invalid. Received: " + tag3);
                }
            case R.layout.item_wallpaper:
                Object tag4 = view.getTag();
                if (tag4 == null) {
                    throw new RuntimeException("view must have a tag");
                } else if ("layout/item_wallpaper_0".equals(tag4)) {
                    return new ItemWallpaperBinding(bindingComponent, view);
                } else {
                    throw new IllegalArgumentException("The tag for item_wallpaper is invalid. Received: " + tag4);
                }
            default:
                return null;
        }
    }

    public ViewDataBinding getDataBinder(DataBindingComponent bindingComponent, View[] views, int layoutId) {
        return null;
    }

    public int getLayoutId(String tag) {
        if (tag == null) {
            return 0;
        }
        int code = tag.hashCode();
        if (code != -1132667228) {
            if (code != -2978231) {
                if (code != 255754121) {
                    if (code == 1095415372 && tag.equals("layout/item_wallpaper_0")) {
                        return R.layout.item_wallpaper;
                    }
                } else if (tag.equals("layout/fragment_wallpaper_0")) {
                    return R.layout.fragment_wallpaper;
                }
            } else if (tag.equals("layout/fragment_wallpaper_detail_0")) {
                return R.layout.fragment_wallpaper_detail;
            }
        } else if (tag.equals("layout/fragment_rate_us_0")) {
            return R.layout.fragment_rate_us;
        }
        return 0;
    }

    public String convertBrIdToString(int id) {
        if (id < 0 || id >= InnerBrLookup.sKeys.length) {
            return null;
        }
        return InnerBrLookup.sKeys[id];
    }

    private static class InnerBrLookup {
        static String[] sKeys = {"_all", "viewModel"};

        private InnerBrLookup() {
        }
    }
}
