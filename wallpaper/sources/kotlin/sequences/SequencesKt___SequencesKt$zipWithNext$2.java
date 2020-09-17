package kotlin.sequences;

import java.util.Iterator;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.experimental.Continuation;
import kotlin.coroutines.experimental.SequenceBuilder;
import kotlin.coroutines.experimental.intrinsics.IntrinsicsKt;
import kotlin.coroutines.experimental.jvm.internal.CoroutineImpl;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0003*\b\u0012\u0004\u0012\u0002H\u00030\u0004H@ø\u0001\u0000¢\u0006\u0004\b\u0005\u0010\u0006"}, d2 = {"<anonymous>", "", "T", "R", "Lkotlin/coroutines/experimental/SequenceBuilder;", "invoke", "(Lkotlin/coroutines/experimental/SequenceBuilder;Lkotlin/coroutines/experimental/Continuation;)Ljava/lang/Object;"}, k = 3, mv = {1, 1, 11})
/* compiled from: _Sequences.kt */
final class SequencesKt___SequencesKt$zipWithNext$2 extends CoroutineImpl implements Function2<SequenceBuilder<? super R>, Continuation<? super Unit>, Object> {
    final /* synthetic */ Function2 $transform;
    Object L$0;
    Object L$1;
    Object L$2;
    Object L$3;
    private SequenceBuilder p$;
    final /* synthetic */ Sequence receiver$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    SequencesKt___SequencesKt$zipWithNext$2(Sequence sequence, Function2 function2, Continuation continuation) {
        super(2, continuation);
        this.receiver$0 = sequence;
        this.$transform = function2;
    }

    @NotNull
    public final Continuation<Unit> create(@NotNull SequenceBuilder<? super R> sequenceBuilder, @NotNull Continuation<? super Unit> continuation) {
        Intrinsics.checkParameterIsNotNull(sequenceBuilder, "$receiver");
        Intrinsics.checkParameterIsNotNull(continuation, "continuation");
        SequencesKt___SequencesKt$zipWithNext$2 sequencesKt___SequencesKt$zipWithNext$2 = new SequencesKt___SequencesKt$zipWithNext$2(this.receiver$0, this.$transform, continuation);
        sequencesKt___SequencesKt$zipWithNext$2.p$ = sequenceBuilder;
        return sequencesKt___SequencesKt$zipWithNext$2;
    }

    @Nullable
    public final Object invoke(@NotNull SequenceBuilder<? super R> sequenceBuilder, @NotNull Continuation<? super Unit> continuation) {
        return ((SequencesKt___SequencesKt$zipWithNext$2) create(sequenceBuilder, continuation)).doResume(Unit.INSTANCE, (Throwable) null);
    }

    @Nullable
    public final Object doResume(@Nullable Object obj, @Nullable Throwable th) {
        Object obj2;
        SequencesKt___SequencesKt$zipWithNext$2 sequencesKt___SequencesKt$zipWithNext$2;
        SequenceBuilder sequenceBuilder;
        Iterator it;
        Object obj3;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (this.label) {
            case 0:
                if (th == null) {
                    SequenceBuilder sequenceBuilder2 = this.p$;
                    Iterator it2 = this.receiver$0.iterator();
                    if (it2.hasNext()) {
                        sequenceBuilder = sequenceBuilder2;
                        obj2 = it2.next();
                        it = it2;
                        obj3 = coroutine_suspended;
                        sequencesKt___SequencesKt$zipWithNext$2 = this;
                        break;
                    } else {
                        return Unit.INSTANCE;
                    }
                } else {
                    throw th;
                }
            case 1:
                Object obj4 = this.L$3;
                Object obj5 = this.L$2;
                it = (Iterator) this.L$1;
                sequenceBuilder = (SequenceBuilder) this.L$0;
                if (th == null) {
                    obj2 = obj4;
                    obj3 = coroutine_suspended;
                    sequencesKt___SequencesKt$zipWithNext$2 = this;
                    break;
                } else {
                    throw th;
                }
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        while (it.hasNext()) {
            Object next = it.next();
            Object invoke = sequencesKt___SequencesKt$zipWithNext$2.$transform.invoke(obj2, next);
            sequencesKt___SequencesKt$zipWithNext$2.L$0 = sequenceBuilder;
            sequencesKt___SequencesKt$zipWithNext$2.L$1 = it;
            sequencesKt___SequencesKt$zipWithNext$2.L$2 = obj2;
            sequencesKt___SequencesKt$zipWithNext$2.L$3 = next;
            sequencesKt___SequencesKt$zipWithNext$2.label = 1;
            if (sequenceBuilder.yield(invoke, sequencesKt___SequencesKt$zipWithNext$2) == obj3) {
                return obj3;
            }
            obj2 = next;
        }
        return Unit.INSTANCE;
    }
}
