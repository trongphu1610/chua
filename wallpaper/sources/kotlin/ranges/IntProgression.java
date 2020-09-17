package kotlin.ranges;

import kotlin.Metadata;
import kotlin.collections.IntIterator;
import kotlin.internal.ProgressionUtilKt;
import kotlin.jvm.internal.markers.KMappedMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u001c\n\u0002\u0010\b\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0016\u0018\u0000 \u00172\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u0017B\u001f\b\u0000\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002¢\u0006\u0002\u0010\u0006J\u0013\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0002J\b\u0010\u0011\u001a\u00020\u0002H\u0016J\b\u0010\u0012\u001a\u00020\u000eH\u0016J\t\u0010\u0013\u001a\u00020\u0014H\u0002J\b\u0010\u0015\u001a\u00020\u0016H\u0016R\u0011\u0010\u0007\u001a\u00020\u0002¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\n\u001a\u00020\u0002¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0002¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\t¨\u0006\u0018"}, d2 = {"Lkotlin/ranges/IntProgression;", "", "", "start", "endInclusive", "step", "(III)V", "first", "getFirst", "()I", "last", "getLast", "getStep", "equals", "", "other", "", "hashCode", "isEmpty", "iterator", "Lkotlin/collections/IntIterator;", "toString", "", "Companion", "kotlin-stdlib"}, k = 1, mv = {1, 1, 11})
/* compiled from: Progressions.kt */
public class IntProgression implements Iterable<Integer>, KMappedMarker {
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    private final int first;
    private final int last;
    private final int step;

    public IntProgression(int start, int endInclusive, int step2) {
        if (step2 == 0) {
            throw new IllegalArgumentException("Step must be non-zero");
        }
        this.first = start;
        this.last = ProgressionUtilKt.getProgressionLastElement(start, endInclusive, step2);
        this.step = step2;
    }

    public final int getFirst() {
        return this.first;
    }

    public final int getLast() {
        return this.last;
    }

    public final int getStep() {
        return this.step;
    }

    @NotNull
    public IntIterator iterator() {
        return new IntProgressionIterator(this.first, this.last, this.step);
    }

    /* JADX WARNING: Removed duplicated region for block: B:4:0x000c A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isEmpty() {
        /*
            r4 = this;
            int r0 = r4.step
            r1 = 0
            r2 = 1
            if (r0 <= 0) goto L_0x000f
            int r0 = r4.first
            int r3 = r4.last
            if (r0 <= r3) goto L_0x000e
        L_0x000c:
            r1 = 1
            goto L_0x0016
        L_0x000e:
            goto L_0x0016
        L_0x000f:
            int r0 = r4.first
            int r3 = r4.last
            if (r0 >= r3) goto L_0x000e
            goto L_0x000c
        L_0x0016:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.ranges.IntProgression.isEmpty():boolean");
    }

    public boolean equals(@Nullable Object other) {
        return (other instanceof IntProgression) && ((isEmpty() && ((IntProgression) other).isEmpty()) || (this.first == ((IntProgression) other).first && this.last == ((IntProgression) other).last && this.step == ((IntProgression) other).step));
    }

    public int hashCode() {
        if (isEmpty()) {
            return -1;
        }
        return (((this.first * 31) + this.last) * 31) + this.step;
    }

    @NotNull
    public String toString() {
        StringBuilder sb;
        int i;
        if (this.step > 0) {
            sb = new StringBuilder();
            sb.append(this.first);
            sb.append("..");
            sb.append(this.last);
            sb.append(" step ");
            i = this.step;
        } else {
            sb = new StringBuilder();
            sb.append(this.first);
            sb.append(" downTo ");
            sb.append(this.last);
            sb.append(" step ");
            i = -this.step;
        }
        sb.append(i);
        return sb.toString();
    }

    @Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u001e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u0006¨\u0006\t"}, d2 = {"Lkotlin/ranges/IntProgression$Companion;", "", "()V", "fromClosedRange", "Lkotlin/ranges/IntProgression;", "rangeStart", "", "rangeEnd", "step", "kotlin-stdlib"}, k = 1, mv = {1, 1, 11})
    /* compiled from: Progressions.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        @NotNull
        public final IntProgression fromClosedRange(int rangeStart, int rangeEnd, int step) {
            return new IntProgression(rangeStart, rangeEnd, step);
        }
    }
}
