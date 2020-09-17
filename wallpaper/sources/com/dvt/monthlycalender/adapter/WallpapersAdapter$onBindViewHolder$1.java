package com.dvt.monthlycalender.adapter;

import android.view.View;
import kotlin.Metadata;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u0003H\n¢\u0006\u0002\b\u0005"}, d2 = {"<anonymous>", "", "it", "Landroid/view/View;", "kotlin.jvm.PlatformType", "onClick"}, k = 3, mv = {1, 1, 11})
/* compiled from: WallpapersAdapter.kt */
final class WallpapersAdapter$onBindViewHolder$1 implements View.OnClickListener {
    final /* synthetic */ int $position;
    final /* synthetic */ WallpapersAdapter this$0;

    WallpapersAdapter$onBindViewHolder$1(WallpapersAdapter wallpapersAdapter, int i) {
        this.this$0 = wallpapersAdapter;
        this.$position = i;
    }

    public final void onClick(View it) {
        this.this$0.onClick.onItemClickListener(this.$position);
    }
}
