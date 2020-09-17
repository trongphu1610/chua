package com.dvt.monthlycalender.model;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0003¢\u0006\u0002\u0010\bR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0007\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\n¨\u0006\u000f"}, d2 = {"Lcom/dvt/monthlycalender/model/WallPaper;", "", "imageId", "", "name", "", "author", "backgroudColor", "(ILjava/lang/String;Ljava/lang/String;I)V", "getAuthor", "()Ljava/lang/String;", "getBackgroudColor", "()I", "getImageId", "getName", "app_debug"}, k = 1, mv = {1, 1, 11})
/* compiled from: WallPaper.kt */
public final class WallPaper {
    @NotNull
    private final String author;
    private final int backgroudColor;
    private final int imageId;
    @NotNull
    private final String name;

    public WallPaper(int imageId2, @NotNull String name2, @NotNull String author2, int backgroudColor2) {
        Intrinsics.checkParameterIsNotNull(name2, "name");
        Intrinsics.checkParameterIsNotNull(author2, "author");
        this.imageId = imageId2;
        this.name = name2;
        this.author = author2;
        this.backgroudColor = backgroudColor2;
    }

    @NotNull
    public final String getAuthor() {
        return this.author;
    }

    public final int getBackgroudColor() {
        return this.backgroudColor;
    }

    public final int getImageId() {
        return this.imageId;
    }

    @NotNull
    public final String getName() {
        return this.name;
    }
}
