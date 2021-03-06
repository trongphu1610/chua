package kotlin;

import kotlin.internal.InlineOnly;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000:\n\u0000\n\u0002\u0010\u0001\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0002\u001a\t\u0010\u0000\u001a\u00020\u0001H\b\u001a\u0011\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\b\u001a0\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00050\tH\b\u0002\b\n\u0006\b\u0001\u0012\u0002\u0010\u0002\u001a/\u0010\n\u001a\u0002H\u000b\"\u0004\b\u0000\u0010\u000b2\f\u0010\f\u001a\b\u0012\u0004\u0012\u0002H\u000b0\rH\b\u0002\n\n\b\b\u0001\u0012\u0002\u0010\u0001 \u0001¢\u0006\u0002\u0010\u000e\u001aH\u0010\u000f\u001a\u0002H\u000b\"\u0004\b\u0000\u0010\u0010\"\u0004\b\u0001\u0010\u000b2\u0006\u0010\u0011\u001a\u0002H\u00102\u0017\u0010\f\u001a\u0013\u0012\u0004\u0012\u0002H\u0010\u0012\u0004\u0012\u0002H\u000b0\t¢\u0006\u0002\b\u0012H\b\u0002\n\n\b\b\u0001\u0012\u0002\u0010\u0002 \u0001¢\u0006\u0002\u0010\u0013\u001a9\u0010\u0014\u001a\u0002H\u0010\"\u0004\b\u0000\u0010\u0010*\u0002H\u00102\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u0002H\u0010\u0012\u0004\u0012\u00020\u00050\tH\b\u0002\n\n\b\b\u0001\u0012\u0002\u0010\u0001 \u0001¢\u0006\u0002\u0010\u0013\u001a>\u0010\u0015\u001a\u0002H\u0010\"\u0004\b\u0000\u0010\u0010*\u0002H\u00102\u0017\u0010\f\u001a\u0013\u0012\u0004\u0012\u0002H\u0010\u0012\u0004\u0012\u00020\u00050\t¢\u0006\u0002\b\u0012H\b\u0002\n\n\b\b\u0001\u0012\u0002\u0010\u0001 \u0001¢\u0006\u0002\u0010\u0013\u001a?\u0010\u0016\u001a\u0002H\u000b\"\u0004\b\u0000\u0010\u0010\"\u0004\b\u0001\u0010\u000b*\u0002H\u00102\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u0002H\u0010\u0012\u0004\u0012\u0002H\u000b0\tH\b\u0002\n\n\b\b\u0001\u0012\u0002\u0010\u0001 \u0001¢\u0006\u0002\u0010\u0013\u001aD\u0010\n\u001a\u0002H\u000b\"\u0004\b\u0000\u0010\u0010\"\u0004\b\u0001\u0010\u000b*\u0002H\u00102\u0017\u0010\f\u001a\u0013\u0012\u0004\u0012\u0002H\u0010\u0012\u0004\u0012\u0002H\u000b0\t¢\u0006\u0002\b\u0012H\b\u0002\n\n\b\b\u0001\u0012\u0002\u0010\u0001 \u0001¢\u0006\u0002\u0010\u0013\u001a;\u0010\u0017\u001a\u0004\u0018\u0001H\u0010\"\u0004\b\u0000\u0010\u0010*\u0002H\u00102\u0012\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u0002H\u0010\u0012\u0004\u0012\u00020\u00190\tH\b\u0002\n\n\b\b\u0001\u0012\u0002\u0010\u0001 \u0001¢\u0006\u0002\u0010\u0013\u001a;\u0010\u001a\u001a\u0004\u0018\u0001H\u0010\"\u0004\b\u0000\u0010\u0010*\u0002H\u00102\u0012\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u0002H\u0010\u0012\u0004\u0012\u00020\u00190\tH\b\u0002\n\n\b\b\u0001\u0012\u0002\u0010\u0001 \u0001¢\u0006\u0002\u0010\u0013¨\u0006\u001b"}, d2 = {"TODO", "", "reason", "", "repeat", "", "times", "", "action", "Lkotlin/Function1;", "run", "R", "block", "Lkotlin/Function0;", "(Lkotlin/jvm/functions/Function0;)Ljava/lang/Object;", "with", "T", "receiver", "Lkotlin/ExtensionFunctionType;", "(Ljava/lang/Object;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "also", "apply", "let", "takeIf", "predicate", "", "takeUnless", "kotlin-stdlib"}, k = 5, mv = {1, 1, 11}, xi = 1, xs = "kotlin/StandardKt")
/* compiled from: Standard.kt */
class StandardKt__StandardKt {
    @InlineOnly
    private static final Void TODO() {
        throw new NotImplementedError((String) null, 1, (DefaultConstructorMarker) null);
    }

    @InlineOnly
    private static final Void TODO(String reason) {
        throw new NotImplementedError("An operation is not implemented: " + reason);
    }

    @InlineOnly
    private static final <R> R run(Function0<? extends R> block) {
        return block.invoke();
    }

    @InlineOnly
    private static final <T, R> R run(T $receiver, Function1<? super T, ? extends R> block) {
        return block.invoke($receiver);
    }

    @InlineOnly
    private static final <T, R> R with(T receiver, Function1<? super T, ? extends R> block) {
        return block.invoke(receiver);
    }

    @InlineOnly
    private static final <T> T apply(T $receiver, Function1<? super T, Unit> block) {
        block.invoke($receiver);
        return $receiver;
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final <T> T also(T $receiver, Function1<? super T, Unit> block) {
        block.invoke($receiver);
        return $receiver;
    }

    @InlineOnly
    private static final <T, R> R let(T $receiver, Function1<? super T, ? extends R> block) {
        return block.invoke($receiver);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final <T> T takeIf(T $receiver, Function1<? super T, Boolean> predicate) {
        if (predicate.invoke($receiver).booleanValue()) {
            return $receiver;
        }
        return null;
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final <T> T takeUnless(T $receiver, Function1<? super T, Boolean> predicate) {
        if (!predicate.invoke($receiver).booleanValue()) {
            return $receiver;
        }
        return null;
    }

    @InlineOnly
    private static final void repeat(int times, Function1<? super Integer, Unit> action) {
        for (int index = 0; index < times; index++) {
            action.invoke(Integer.valueOf(index));
        }
    }
}
