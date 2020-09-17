package com.dvt.monthlycalender.viewmodel;

import com.dvt.monthlycalender.R;
import com.dvt.monthlycalender.model.WallPaper;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001H\n¢\u0006\u0004\b\u0003\u0010\u0004"}, d2 = {"<anonymous>", "", "Lcom/dvt/monthlycalender/model/WallPaper;", "invoke", "()[Lcom/dvt/monthlycalender/model/WallPaper;"}, k = 3, mv = {1, 1, 11})
/* compiled from: WallpapersViewModel.kt */
final class WallpapersViewModel$arrWallpapers$2 extends Lambda implements Function0<WallPaper[]> {
    final /* synthetic */ WallpapersViewModel this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    WallpapersViewModel$arrWallpapers$2(WallpapersViewModel wallpapersViewModel) {
        super(0);
        this.this$0 = wallpapersViewModel;
    }

    @NotNull
    public final WallPaper[] invoke() {
        WallPaper[] wallPaperArr = new WallPaper[12];
        int length = wallPaperArr.length;
        for (int i$iv = 0; i$iv < length; i$iv++) {
            int i = i$iv;
            wallPaperArr[i$iv] = new WallPaper(((Number) this.this$0.groupOfImage[i].getFirst()).intValue(), (String) this.this$0.groupOfImage[i].getSecond(), "Đoan", R.color.colorAccent);
        }
        return wallPaperArr;
    }
}
