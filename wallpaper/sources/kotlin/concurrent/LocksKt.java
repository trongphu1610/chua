package kotlin.concurrent;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import kotlin.Metadata;
import kotlin.internal.InlineOnly;
import kotlin.jvm.JvmName;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.InlineMarker;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u001a\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a&\u0010\u0000\u001a\u0002H\u0001\"\u0004\b\u0000\u0010\u0001*\u00020\u00022\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u0002H\u00010\u0004H\b¢\u0006\u0002\u0010\u0005\u001a&\u0010\u0006\u001a\u0002H\u0001\"\u0004\b\u0000\u0010\u0001*\u00020\u00072\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u0002H\u00010\u0004H\b¢\u0006\u0002\u0010\b\u001a&\u0010\t\u001a\u0002H\u0001\"\u0004\b\u0000\u0010\u0001*\u00020\u00022\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u0002H\u00010\u0004H\b¢\u0006\u0002\u0010\u0005¨\u0006\n"}, d2 = {"read", "T", "Ljava/util/concurrent/locks/ReentrantReadWriteLock;", "action", "Lkotlin/Function0;", "(Ljava/util/concurrent/locks/ReentrantReadWriteLock;Lkotlin/jvm/functions/Function0;)Ljava/lang/Object;", "withLock", "Ljava/util/concurrent/locks/Lock;", "(Ljava/util/concurrent/locks/Lock;Lkotlin/jvm/functions/Function0;)Ljava/lang/Object;", "write", "kotlin-stdlib"}, k = 2, mv = {1, 1, 11})
@JvmName(name = "LocksKt")
/* compiled from: Locks.kt */
public final class LocksKt {
    @InlineOnly
    private static final <T> T withLock(@NotNull Lock $receiver, Function0<? extends T> action) {
        $receiver.lock();
        try {
            return action.invoke();
        } finally {
            InlineMarker.finallyStart(1);
            $receiver.unlock();
            InlineMarker.finallyEnd(1);
        }
    }

    @InlineOnly
    private static final <T> T read(@NotNull ReentrantReadWriteLock $receiver, Function0<? extends T> action) {
        ReentrantReadWriteLock.ReadLock rl = $receiver.readLock();
        rl.lock();
        try {
            return action.invoke();
        } finally {
            InlineMarker.finallyStart(1);
            rl.unlock();
            InlineMarker.finallyEnd(1);
        }
    }

    /*  JADX ERROR: StackOverflow in pass: MarkFinallyVisitor
        jadx.core.utils.exceptions.JadxOverflowException: 
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:47)
        	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:81)
        */
    @kotlin.internal.InlineOnly
    private static final <T> T write(@org.jetbrains.annotations.NotNull java.util.concurrent.locks.ReentrantReadWriteLock r9, kotlin.jvm.functions.Function0<? extends T> r10) {
        /*
            r0 = 0
            java.util.concurrent.locks.ReentrantReadWriteLock$ReadLock r1 = r9.readLock()
            int r2 = r9.getWriteHoldCount()
            r3 = 0
            if (r2 != 0) goto L_0x0011
            int r2 = r9.getReadHoldCount()
            goto L_0x0012
        L_0x0011:
            r2 = 0
        L_0x0012:
            r4 = 0
            r5 = 0
        L_0x0014:
            if (r4 >= r2) goto L_0x001d
            r6 = r4
            r1.unlock()
            int r4 = r4 + 1
            goto L_0x0014
        L_0x001d:
            java.util.concurrent.locks.ReentrantReadWriteLock$WriteLock r4 = r9.writeLock()
            r4.lock()
            r5 = 1
            java.lang.Object r6 = r10.invoke()     // Catch:{ all -> 0x003e }
            kotlin.jvm.internal.InlineMarker.finallyStart(r5)
            r7 = 0
        L_0x002e:
            if (r3 >= r2) goto L_0x0037
            r8 = r3
            r1.lock()
            int r3 = r3 + 1
            goto L_0x002e
        L_0x0037:
            r4.unlock()
            kotlin.jvm.internal.InlineMarker.finallyEnd(r5)
            return r6
        L_0x003e:
            r6 = move-exception
            kotlin.jvm.internal.InlineMarker.finallyStart(r5)
            r7 = 0
        L_0x0043:
            if (r3 >= r2) goto L_0x004c
            r8 = r3
            r1.lock()
            int r3 = r3 + 1
            goto L_0x0043
        L_0x004c:
            r4.unlock()
            kotlin.jvm.internal.InlineMarker.finallyEnd(r5)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.concurrent.LocksKt.write(java.util.concurrent.locks.ReentrantReadWriteLock, kotlin.jvm.functions.Function0):java.lang.Object");
    }
}
