package kotlin.collections;

import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.experimental.Continuation;
import kotlin.coroutines.experimental.SequenceBuilder;
import kotlin.coroutines.experimental.jvm.internal.CoroutineImpl;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u0014\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u00040\u0003H@ø\u0001\u0000¢\u0006\u0004\b\u0005\u0010\u0006"}, d2 = {"<anonymous>", "", "T", "Lkotlin/coroutines/experimental/SequenceBuilder;", "", "invoke", "(Lkotlin/coroutines/experimental/SequenceBuilder;Lkotlin/coroutines/experimental/Continuation;)Ljava/lang/Object;"}, k = 3, mv = {1, 1, 11})
/* compiled from: SlidingWindow.kt */
final class SlidingWindowKt$windowedIterator$1 extends CoroutineImpl implements Function2<SequenceBuilder<? super List<? extends T>>, Continuation<? super Unit>, Object> {
    final /* synthetic */ Iterator $iterator;
    final /* synthetic */ boolean $partialWindows;
    final /* synthetic */ boolean $reuseBuffer;
    final /* synthetic */ int $size;
    final /* synthetic */ int $step;
    int I$0;
    int I$1;
    Object L$0;
    Object L$1;
    Object L$2;
    Object L$3;
    private SequenceBuilder p$;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    SlidingWindowKt$windowedIterator$1(int i, int i2, Iterator it, boolean z, boolean z2, Continuation continuation) {
        super(2, continuation);
        this.$step = i;
        this.$size = i2;
        this.$iterator = it;
        this.$reuseBuffer = z;
        this.$partialWindows = z2;
    }

    @NotNull
    public final Continuation<Unit> create(@NotNull SequenceBuilder<? super List<? extends T>> sequenceBuilder, @NotNull Continuation<? super Unit> continuation) {
        Intrinsics.checkParameterIsNotNull(sequenceBuilder, "$receiver");
        Intrinsics.checkParameterIsNotNull(continuation, "continuation");
        SlidingWindowKt$windowedIterator$1 slidingWindowKt$windowedIterator$1 = new SlidingWindowKt$windowedIterator$1(this.$step, this.$size, this.$iterator, this.$reuseBuffer, this.$partialWindows, continuation);
        slidingWindowKt$windowedIterator$1.p$ = sequenceBuilder;
        return slidingWindowKt$windowedIterator$1;
    }

    @Nullable
    public final Object invoke(@NotNull SequenceBuilder<? super List<? extends T>> sequenceBuilder, @NotNull Continuation<? super Unit> continuation) {
        return ((SlidingWindowKt$windowedIterator$1) create(sequenceBuilder, continuation)).doResume(Unit.INSTANCE, (Throwable) null);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0087, code lost:
        if (r0.hasNext() == false) goto L_0x00c4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0089, code lost:
        r6 = r0.next();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x008d, code lost:
        if (r3 <= 0) goto L_0x0092;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x008f, code lost:
        r3 = r3 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0092, code lost:
        r2.add(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x009b, code lost:
        if (r2.size() != r11.$size) goto L_0x0083;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x009d, code lost:
        r11.L$0 = r4;
        r11.I$0 = r12;
        r11.L$1 = r2;
        r11.I$1 = r3;
        r11.L$2 = r6;
        r11.L$3 = r0;
        r11.label = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00af, code lost:
        if (r4.yield(r2, r11) != r5) goto L_0x00b2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00b1, code lost:
        return r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00b4, code lost:
        if (r11.$reuseBuffer == false) goto L_0x00ba;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00b6, code lost:
        r2.clear();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00ba, code lost:
        r2 = new java.util.ArrayList(r11.$size);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00c1, code lost:
        r3 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00cc, code lost:
        if ((!r2.isEmpty()) == false) goto L_0x018a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00d0, code lost:
        if (r11.$partialWindows != false) goto L_0x00da;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00d8, code lost:
        if (r2.size() != r11.$size) goto L_0x018a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00da, code lost:
        r11.I$0 = r12;
        r11.L$0 = r2;
        r11.I$1 = r3;
        r11.label = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00e7, code lost:
        if (r4.yield(r2, r11) != r5) goto L_0x018a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00e9, code lost:
        return r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00ff, code lost:
        if (r0.hasNext() == false) goto L_0x013a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0101, code lost:
        r5 = r0.next();
        r2.add(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x010c, code lost:
        if (r2.isFull() == false) goto L_0x00fb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x0110, code lost:
        if (r11.$reuseBuffer == false) goto L_0x0116;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x0112, code lost:
        r6 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x0116, code lost:
        r6 = new java.util.ArrayList(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x0120, code lost:
        r11.L$0 = r4;
        r11.I$0 = r3;
        r11.L$1 = r2;
        r11.L$2 = r5;
        r11.L$3 = r0;
        r11.label = 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x0131, code lost:
        if (r4.yield(r6, r11) != r12) goto L_0x0134;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x0133, code lost:
        return r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x0134, code lost:
        r2.removeFirst(r11.$step);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x013c, code lost:
        if (r11.$partialWindows == false) goto L_0x018a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x013e, code lost:
        r0 = r2;
        r2 = r3;
        r3 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x0147, code lost:
        if (r0.size() <= r11.$step) goto L_0x0171;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x014b, code lost:
        if (r11.$reuseBuffer == false) goto L_0x0151;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x014d, code lost:
        r4 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0151, code lost:
        r4 = new java.util.ArrayList(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x015b, code lost:
        r11.L$0 = r3;
        r11.I$0 = r2;
        r11.L$1 = r0;
        r11.label = 4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x0168, code lost:
        if (r3.yield(r4, r11) != r12) goto L_0x016b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x016a, code lost:
        return r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x016b, code lost:
        r0.removeFirst(r11.$step);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x0179, code lost:
        if ((true ^ r0.isEmpty()) == false) goto L_0x018a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x017b, code lost:
        r11.I$0 = r2;
        r11.L$0 = r0;
        r11.label = 5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x0186, code lost:
        if (r3.yield(r0, r11) != r12) goto L_0x018a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x0188, code lost:
        return r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x018c, code lost:
        return kotlin.Unit.INSTANCE;
     */
    @org.jetbrains.annotations.Nullable
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final java.lang.Object doResume(@org.jetbrains.annotations.Nullable java.lang.Object r11, @org.jetbrains.annotations.Nullable java.lang.Throwable r12) {
        /*
            r10 = this;
            java.lang.Object r11 = kotlin.coroutines.experimental.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r0 = r10.label
            r1 = 1
            switch(r0) {
                case 0: goto L_0x0067;
                case 1: goto L_0x004e;
                case 2: goto L_0x0043;
                case 3: goto L_0x002c;
                case 4: goto L_0x001b;
                case 5: goto L_0x0012;
                default: goto L_0x000a;
            }
        L_0x000a:
            java.lang.IllegalStateException r11 = new java.lang.IllegalStateException
            java.lang.String r12 = "call to 'resume' before 'invoke' with coroutine"
            r11.<init>(r12)
            throw r11
        L_0x0012:
            java.lang.Object r11 = r10.L$0
            kotlin.collections.RingBuffer r11 = (kotlin.collections.RingBuffer) r11
            int r11 = r10.I$0
            if (r12 == 0) goto L_0x0189
            throw r12
        L_0x001b:
            java.lang.Object r0 = r10.L$1
            kotlin.collections.RingBuffer r0 = (kotlin.collections.RingBuffer) r0
            int r2 = r10.I$0
            java.lang.Object r3 = r10.L$0
            kotlin.coroutines.experimental.SequenceBuilder r3 = (kotlin.coroutines.experimental.SequenceBuilder) r3
            if (r12 == 0) goto L_0x0028
            throw r12
        L_0x0028:
            r12 = r11
            r11 = r10
            goto L_0x016b
        L_0x002c:
            java.lang.Object r0 = r10.L$3
            java.util.Iterator r0 = (java.util.Iterator) r0
            java.lang.Object r2 = r10.L$2
            java.lang.Object r2 = r10.L$1
            kotlin.collections.RingBuffer r2 = (kotlin.collections.RingBuffer) r2
            int r3 = r10.I$0
            java.lang.Object r4 = r10.L$0
            kotlin.coroutines.experimental.SequenceBuilder r4 = (kotlin.coroutines.experimental.SequenceBuilder) r4
            if (r12 == 0) goto L_0x003f
            throw r12
        L_0x003f:
            r12 = r11
            r11 = r10
            goto L_0x0134
        L_0x0043:
            int r11 = r10.I$1
            java.lang.Object r11 = r10.L$0
            java.util.ArrayList r11 = (java.util.ArrayList) r11
            int r11 = r10.I$0
            if (r12 == 0) goto L_0x00ea
            throw r12
        L_0x004e:
            java.lang.Object r0 = r10.L$3
            java.util.Iterator r0 = (java.util.Iterator) r0
            java.lang.Object r2 = r10.L$2
            int r2 = r10.I$1
            java.lang.Object r2 = r10.L$1
            java.util.ArrayList r2 = (java.util.ArrayList) r2
            int r3 = r10.I$0
            java.lang.Object r4 = r10.L$0
            kotlin.coroutines.experimental.SequenceBuilder r4 = (kotlin.coroutines.experimental.SequenceBuilder) r4
            if (r12 == 0) goto L_0x0063
            throw r12
        L_0x0063:
            r5 = r11
            r12 = r3
            r11 = r10
            goto L_0x00b2
        L_0x0067:
            if (r12 == 0) goto L_0x006a
            throw r12
        L_0x006a:
            kotlin.coroutines.experimental.SequenceBuilder r12 = r10.p$
            int r0 = r10.$step
            int r2 = r10.$size
            int r0 = r0 - r2
            if (r0 < 0) goto L_0x00ec
            java.util.ArrayList r2 = new java.util.ArrayList
            int r3 = r10.$size
            r2.<init>(r3)
            r3 = 0
            java.util.Iterator r4 = r10.$iterator
            r5 = r11
            r11 = r10
            r9 = r4
            r4 = r12
            r12 = r0
            r0 = r9
        L_0x0083:
            boolean r6 = r0.hasNext()
            if (r6 == 0) goto L_0x00c4
            java.lang.Object r6 = r0.next()
            if (r3 <= 0) goto L_0x0092
            int r3 = r3 + -1
            goto L_0x00c3
        L_0x0092:
            r2.add(r6)
            int r7 = r2.size()
            int r8 = r11.$size
            if (r7 != r8) goto L_0x00c3
            r11.L$0 = r4
            r11.I$0 = r12
            r11.L$1 = r2
            r11.I$1 = r3
            r11.L$2 = r6
            r11.L$3 = r0
            r11.label = r1
            java.lang.Object r3 = r4.yield(r2, r11)
            if (r3 != r5) goto L_0x00b2
            return r5
        L_0x00b2:
            boolean r3 = r11.$reuseBuffer
            if (r3 == 0) goto L_0x00ba
            r2.clear()
            goto L_0x00c1
        L_0x00ba:
            java.util.ArrayList r2 = new java.util.ArrayList
            int r3 = r11.$size
            r2.<init>(r3)
        L_0x00c1:
            r3 = r12
        L_0x00c3:
            goto L_0x0083
        L_0x00c4:
            r0 = r2
            java.util.Collection r0 = (java.util.Collection) r0
            boolean r0 = r0.isEmpty()
            r0 = r0 ^ r1
            if (r0 == 0) goto L_0x018a
            boolean r0 = r11.$partialWindows
            if (r0 != 0) goto L_0x00da
            int r0 = r2.size()
            int r1 = r11.$size
            if (r0 != r1) goto L_0x018a
        L_0x00da:
            r11.I$0 = r12
            r11.L$0 = r2
            r11.I$1 = r3
            r12 = 2
            r11.label = r12
            java.lang.Object r11 = r4.yield(r2, r11)
            if (r11 != r5) goto L_0x00ea
            return r5
        L_0x00ea:
            goto L_0x018a
        L_0x00ec:
            kotlin.collections.RingBuffer r2 = new kotlin.collections.RingBuffer
            int r3 = r10.$size
            r2.<init>(r3)
            java.util.Iterator r3 = r10.$iterator
            r4 = r12
            r12 = r11
            r11 = r10
            r9 = r3
            r3 = r0
            r0 = r9
        L_0x00fb:
            boolean r5 = r0.hasNext()
            if (r5 == 0) goto L_0x013a
            java.lang.Object r5 = r0.next()
            r2.add(r5)
            boolean r6 = r2.isFull()
            if (r6 == 0) goto L_0x0139
            boolean r6 = r11.$reuseBuffer
            if (r6 == 0) goto L_0x0116
            r6 = r2
            java.util.List r6 = (java.util.List) r6
            goto L_0x0120
        L_0x0116:
            java.util.ArrayList r6 = new java.util.ArrayList
            r7 = r2
            java.util.Collection r7 = (java.util.Collection) r7
            r6.<init>(r7)
            java.util.List r6 = (java.util.List) r6
        L_0x0120:
            r11.L$0 = r4
            r11.I$0 = r3
            r11.L$1 = r2
            r11.L$2 = r5
            r11.L$3 = r0
            r5 = 3
            r11.label = r5
            java.lang.Object r5 = r4.yield(r6, r11)
            if (r5 != r12) goto L_0x0134
            return r12
        L_0x0134:
            int r5 = r11.$step
            r2.removeFirst(r5)
        L_0x0139:
            goto L_0x00fb
        L_0x013a:
            boolean r0 = r11.$partialWindows
            if (r0 == 0) goto L_0x018a
            r0 = r2
            r2 = r3
            r3 = r4
        L_0x0141:
            int r4 = r0.size()
            int r5 = r11.$step
            if (r4 <= r5) goto L_0x0171
            boolean r4 = r11.$reuseBuffer
            if (r4 == 0) goto L_0x0151
            r4 = r0
            java.util.List r4 = (java.util.List) r4
            goto L_0x015b
        L_0x0151:
            java.util.ArrayList r4 = new java.util.ArrayList
            r5 = r0
            java.util.Collection r5 = (java.util.Collection) r5
            r4.<init>(r5)
            java.util.List r4 = (java.util.List) r4
        L_0x015b:
            r11.L$0 = r3
            r11.I$0 = r2
            r11.L$1 = r0
            r5 = 4
            r11.label = r5
            java.lang.Object r4 = r3.yield(r4, r11)
            if (r4 != r12) goto L_0x016b
            return r12
        L_0x016b:
            int r4 = r11.$step
            r0.removeFirst(r4)
            goto L_0x0141
        L_0x0171:
            r4 = r0
            java.util.Collection r4 = (java.util.Collection) r4
            boolean r4 = r4.isEmpty()
            r1 = r1 ^ r4
            if (r1 == 0) goto L_0x018a
            r11.I$0 = r2
            r11.L$0 = r0
            r1 = 5
            r11.label = r1
            java.lang.Object r11 = r3.yield(r0, r11)
            if (r11 != r12) goto L_0x0189
            return r12
        L_0x0189:
        L_0x018a:
            kotlin.Unit r11 = kotlin.Unit.INSTANCE
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.collections.SlidingWindowKt$windowedIterator$1.doResume(java.lang.Object, java.lang.Throwable):java.lang.Object");
    }
}
