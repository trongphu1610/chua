package kotlin.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.SequencesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u001e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u000b\u001a!\u0010\u0000\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0002H\u0002¢\u0006\u0002\b\u0004\u001a\u0011\u0010\u0005\u001a\u00020\u0006*\u00020\u0002H\u0002¢\u0006\u0002\b\u0007\u001a\u0014\u0010\b\u001a\u00020\u0002*\u00020\u00022\b\b\u0002\u0010\u0003\u001a\u00020\u0002\u001aJ\u0010\t\u001a\u00020\u0002*\b\u0012\u0004\u0012\u00020\u00020\n2\u0006\u0010\u000b\u001a\u00020\u00062\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00020\u00012\u0014\u0010\r\u001a\u0010\u0012\u0004\u0012\u00020\u0002\u0012\u0006\u0012\u0004\u0018\u00010\u00020\u0001H\b¢\u0006\u0002\b\u000e\u001a\u0014\u0010\u000f\u001a\u00020\u0002*\u00020\u00022\b\b\u0002\u0010\u0010\u001a\u00020\u0002\u001a\u001e\u0010\u0011\u001a\u00020\u0002*\u00020\u00022\b\b\u0002\u0010\u0010\u001a\u00020\u00022\b\b\u0002\u0010\u0012\u001a\u00020\u0002\u001a\n\u0010\u0013\u001a\u00020\u0002*\u00020\u0002\u001a\u0014\u0010\u0014\u001a\u00020\u0002*\u00020\u00022\b\b\u0002\u0010\u0012\u001a\u00020\u0002¨\u0006\u0015"}, d2 = {"getIndentFunction", "Lkotlin/Function1;", "", "indent", "getIndentFunction$StringsKt__IndentKt", "indentWidth", "", "indentWidth$StringsKt__IndentKt", "prependIndent", "reindent", "", "resultSizeEstimate", "indentAddFunction", "indentCutFunction", "reindent$StringsKt__IndentKt", "replaceIndent", "newIndent", "replaceIndentByMargin", "marginPrefix", "trimIndent", "trimMargin", "kotlin-stdlib"}, k = 5, mv = {1, 1, 11}, xi = 1, xs = "kotlin/text/StringsKt")
/* compiled from: Indent.kt */
class StringsKt__IndentKt {
    @NotNull
    public static /* bridge */ /* synthetic */ String trimMargin$default(String str, String str2, int i, Object obj) {
        if ((i & 1) != 0) {
            str2 = "|";
        }
        return StringsKt.trimMargin(str, str2);
    }

    @NotNull
    public static final String trimMargin(@NotNull String $receiver, @NotNull String marginPrefix) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(marginPrefix, "marginPrefix");
        return StringsKt.replaceIndentByMargin($receiver, "", marginPrefix);
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String replaceIndentByMargin$default(String str, String str2, String str3, int i, Object obj) {
        if ((i & 1) != 0) {
            str2 = "";
        }
        if ((i & 2) != 0) {
            str3 = "|";
        }
        return StringsKt.replaceIndentByMargin(str, str2, str3);
    }

    @NotNull
    public static final String replaceIndentByMargin(@NotNull String $receiver, @NotNull String newIndent, @NotNull String marginPrefix) {
        Iterator it;
        Iterable $receiver$iv$iv$iv$iv;
        Collection destination$iv$iv$iv;
        String str;
        String invoke;
        String str2 = $receiver;
        String str3 = marginPrefix;
        Intrinsics.checkParameterIsNotNull(str2, "$receiver");
        Intrinsics.checkParameterIsNotNull(newIndent, "newIndent");
        Intrinsics.checkParameterIsNotNull(str3, "marginPrefix");
        if (!(!StringsKt.isBlank(str3))) {
            throw new IllegalArgumentException("marginPrefix must be non-blank string.".toString());
        }
        List lines = StringsKt.lines(str2);
        int resultSizeEstimate$iv = $receiver.length() + (newIndent.length() * lines.size());
        Function1 indentAddFunction$iv = getIndentFunction$StringsKt__IndentKt(newIndent);
        List $receiver$iv = lines;
        int lastIndex$iv = CollectionsKt.getLastIndex($receiver$iv);
        Iterable destination$iv$iv$iv2 = (Collection) new ArrayList();
        Iterable $receiver$iv$iv$iv$iv2 = $receiver$iv;
        int index$iv$iv$iv$iv = 0;
        Iterator it2 = $receiver$iv$iv$iv$iv2.iterator();
        int $i$a$1$forEachIndexed = 0;
        int $i$a$1$mapIndexedNotNull = 0;
        while (it2.hasNext()) {
            int index$iv$iv$iv$iv2 = index$iv$iv$iv$iv + 1;
            int $i$a$1$forEachIndexed2 = $i$a$1$forEachIndexed;
            String value$iv = (String) it2.next();
            int index$iv = index$iv$iv$iv$iv;
            int $i$a$1$mapIndexedNotNull2 = $i$a$1$mapIndexedNotNull;
            if ((index$iv == 0 || index$iv == lastIndex$iv) && StringsKt.isBlank(value$iv)) {
                it = it2;
                $receiver$iv$iv$iv$iv = $receiver$iv$iv$iv$iv2;
                destination$iv$iv$iv = destination$iv$iv$iv2;
                value$iv = null;
            } else {
                String line = value$iv;
                CharSequence $receiver$iv2 = line;
                int length = $receiver$iv2.length();
                int index$iv2 = 0;
                while (true) {
                    int index$iv3 = index$iv;
                    if (index$iv2 >= length) {
                        index$iv2 = -1;
                        break;
                    } else if ((CharsKt.isWhitespace($receiver$iv2.charAt(index$iv2)) ^ 1) != 0) {
                        break;
                    } else {
                        index$iv2++;
                        index$iv = index$iv3;
                    }
                }
                if (index$iv2 == -1) {
                    String str4 = line;
                    it = it2;
                    $receiver$iv$iv$iv$iv = $receiver$iv$iv$iv$iv2;
                    destination$iv$iv$iv = destination$iv$iv$iv2;
                } else {
                    String line2 = line;
                    it = it2;
                    $receiver$iv$iv$iv$iv = $receiver$iv$iv$iv$iv2;
                    destination$iv$iv$iv = destination$iv$iv$iv2;
                    if (StringsKt.startsWith$default(line, str3, index$iv2, false, 4, (Object) null)) {
                        int length2 = marginPrefix.length() + index$iv2;
                        String line3 = line2;
                        if (line3 == null) {
                            throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
                        }
                        str = line3.substring(length2);
                        Intrinsics.checkExpressionValueIsNotNull(str, "(this as java.lang.String).substring(startIndex)");
                        if (!(str == null || (invoke = indentAddFunction$iv.invoke(str)) == null)) {
                            value$iv = invoke;
                        }
                    }
                }
                str = null;
                value$iv = invoke;
            }
            if (value$iv != null) {
                destination$iv$iv$iv.add(value$iv);
            }
            destination$iv$iv$iv2 = destination$iv$iv$iv;
            $receiver$iv$iv$iv$iv2 = $receiver$iv$iv$iv$iv;
            index$iv$iv$iv$iv = index$iv$iv$iv$iv2;
            $i$a$1$forEachIndexed = $i$a$1$forEachIndexed2;
            $i$a$1$mapIndexedNotNull = $i$a$1$mapIndexedNotNull2;
            it2 = it;
            String str5 = $receiver;
            String str6 = newIndent;
        }
        String sb = ((StringBuilder) CollectionsKt.joinTo$default((List) destination$iv$iv$iv2, new StringBuilder(resultSizeEstimate$iv), "\n", (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, (Function1) null, 124, (Object) null)).toString();
        Intrinsics.checkExpressionValueIsNotNull(sb, "mapIndexedNotNull { inde…\"\\n\")\n        .toString()");
        return sb;
    }

    @NotNull
    public static final String trimIndent(@NotNull String $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return StringsKt.replaceIndent($receiver, "");
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String replaceIndent$default(String str, String str2, int i, Object obj) {
        if ((i & 1) != 0) {
            str2 = "";
        }
        return StringsKt.replaceIndent(str, str2);
    }

    /* JADX WARNING: Removed duplicated region for block: B:30:0x0111  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0115 A[SYNTHETIC] */
    @org.jetbrains.annotations.NotNull
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final java.lang.String replaceIndent(@org.jetbrains.annotations.NotNull java.lang.String r28, @org.jetbrains.annotations.NotNull java.lang.String r29) {
        /*
            r0 = r28
            java.lang.String r1 = "$receiver"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r0, r1)
            java.lang.String r1 = "newIndent"
            r2 = r29
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r2, r1)
            r1 = r0
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            java.util.List r1 = kotlin.text.StringsKt.lines(r1)
            r3 = r1
            java.lang.Iterable r3 = (java.lang.Iterable) r3
            r4 = 0
            r5 = r4
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            java.util.Collection r6 = (java.util.Collection) r6
            r7 = r3
            r8 = r4
            java.util.Iterator r9 = r7.iterator()
            r10 = 0
        L_0x002f:
            boolean r11 = r9.hasNext()
            if (r11 == 0) goto L_0x004b
            java.lang.Object r11 = r9.next()
            r12 = r11
            java.lang.String r12 = (java.lang.String) r12
            r13 = r12
            java.lang.CharSequence r13 = (java.lang.CharSequence) r13
            boolean r13 = kotlin.text.StringsKt.isBlank(r13)
            r12 = r13 ^ 1
            if (r12 == 0) goto L_0x002f
            r6.add(r11)
            goto L_0x002f
        L_0x004b:
            r3 = r6
            java.util.List r3 = (java.util.List) r3
            java.lang.Iterable r3 = (java.lang.Iterable) r3
            r5 = r8
            java.util.ArrayList r6 = new java.util.ArrayList
            r7 = 10
            int r7 = kotlin.collections.CollectionsKt.collectionSizeOrDefault(r3, r7)
            r6.<init>(r7)
            java.util.Collection r6 = (java.util.Collection) r6
            r7 = r3
            r8 = r10
            java.util.Iterator r9 = r7.iterator()
            r10 = 0
        L_0x0067:
            boolean r11 = r9.hasNext()
            if (r11 == 0) goto L_0x0080
            java.lang.Object r11 = r9.next()
            r12 = r11
            java.lang.String r12 = (java.lang.String) r12
            int r12 = indentWidth$StringsKt__IndentKt(r12)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            r6.add(r12)
            goto L_0x0067
        L_0x0080:
            r3 = r6
            java.util.List r3 = (java.util.List) r3
            java.lang.Iterable r3 = (java.lang.Iterable) r3
            java.lang.Comparable r3 = kotlin.collections.CollectionsKt.min(r3)
            java.lang.Integer r3 = (java.lang.Integer) r3
            if (r3 == 0) goto L_0x0093
            int r3 = r3.intValue()
            goto L_0x0094
        L_0x0093:
            r3 = 0
        L_0x0094:
            int r5 = r28.length()
            int r6 = r29.length()
            int r7 = r1.size()
            int r6 = r6 * r7
            int r5 = r5 + r6
            kotlin.jvm.functions.Function1 r6 = getIndentFunction$StringsKt__IndentKt(r29)
            r7 = r1
            r8 = r4
            int r9 = kotlin.collections.CollectionsKt.getLastIndex(r7)
            r10 = r7
            java.lang.Iterable r10 = (java.lang.Iterable) r10
            r11 = r4
            java.util.ArrayList r12 = new java.util.ArrayList
            r12.<init>()
            java.util.Collection r12 = (java.util.Collection) r12
            r13 = r10
            r14 = r4
            r15 = r13
            r16 = r4
            r17 = 0
            java.util.Iterator r4 = r15.iterator()
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
        L_0x00cf:
            boolean r22 = r4.hasNext()
            if (r22 == 0) goto L_0x011a
            java.lang.Object r22 = r4.next()
            int r23 = r17 + 1
            r24 = r22
            r25 = r24
            java.lang.String r25 = (java.lang.String) r25
            r26 = r17
            r0 = r26
            if (r0 == 0) goto L_0x00ed
            if (r0 != r9) goto L_0x00ea
            goto L_0x00ed
        L_0x00ea:
            r27 = r0
            goto L_0x00fb
        L_0x00ed:
            r27 = r0
            r0 = r25
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            boolean r0 = kotlin.text.StringsKt.isBlank(r0)
            if (r0 == 0) goto L_0x00fb
            r0 = 0
            goto L_0x010f
        L_0x00fb:
            r0 = r25
            java.lang.String r0 = kotlin.text.StringsKt.drop((java.lang.String) r0, (int) r3)
            if (r0 == 0) goto L_0x010c
            java.lang.Object r0 = r6.invoke(r0)
            java.lang.String r0 = (java.lang.String) r0
            if (r0 == 0) goto L_0x010c
            goto L_0x010f
        L_0x010c:
            r0 = r25
        L_0x010f:
            if (r0 == 0) goto L_0x0115
            r12.add(r0)
        L_0x0115:
            r17 = r23
            r0 = r28
            goto L_0x00cf
        L_0x011a:
            r0 = r12
            java.util.List r0 = (java.util.List) r0
            r10 = r0
            java.lang.Iterable r10 = (java.lang.Iterable) r10
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>(r5)
            r11 = r0
            java.lang.Appendable r11 = (java.lang.Appendable) r11
            java.lang.String r0 = "\n"
            r12 = r0
            java.lang.CharSequence r12 = (java.lang.CharSequence) r12
            r13 = 0
            r14 = 0
            r15 = 0
            r16 = 0
            r17 = 0
            r18 = 124(0x7c, float:1.74E-43)
            r19 = 0
            java.lang.Appendable r0 = kotlin.collections.CollectionsKt.joinTo$default(r10, r11, r12, r13, r14, r15, r16, r17, r18, r19)
            java.lang.StringBuilder r0 = (java.lang.StringBuilder) r0
            java.lang.String r0 = r0.toString()
            java.lang.String r4 = "mapIndexedNotNull { inde…\"\\n\")\n        .toString()"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r0, r4)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.text.StringsKt__IndentKt.replaceIndent(java.lang.String, java.lang.String):java.lang.String");
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String prependIndent$default(String str, String str2, int i, Object obj) {
        if ((i & 1) != 0) {
            str2 = "    ";
        }
        return StringsKt.prependIndent(str, str2);
    }

    @NotNull
    public static final String prependIndent(@NotNull String $receiver, @NotNull String indent) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(indent, "indent");
        return SequencesKt.joinToString$default(SequencesKt.map(StringsKt.lineSequence($receiver), new StringsKt__IndentKt$prependIndent$1(indent)), "\n", (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, (Function1) null, 62, (Object) null);
    }

    private static final int indentWidth$StringsKt__IndentKt(@NotNull String $receiver) {
        CharSequence $receiver$iv = $receiver;
        int length = $receiver$iv.length();
        int index$iv = 0;
        while (true) {
            if (index$iv >= length) {
                index$iv = -1;
                break;
            } else if ((CharsKt.isWhitespace($receiver$iv.charAt(index$iv)) ^ 1) != 0) {
                break;
            } else {
                index$iv++;
            }
        }
        int it = index$iv;
        int i = length;
        return it == -1 ? $receiver.length() : it;
    }

    private static final Function1<String, String> getIndentFunction$StringsKt__IndentKt(String indent) {
        if (indent.length() == 0) {
            return StringsKt__IndentKt$getIndentFunction$1.INSTANCE;
        }
        return new StringsKt__IndentKt$getIndentFunction$2(indent);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0067, code lost:
        if (r0 != null) goto L_0x006e;
     */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0070  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0075 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static final java.lang.String reindent$StringsKt__IndentKt(@org.jetbrains.annotations.NotNull java.util.List<java.lang.String> r23, int r24, kotlin.jvm.functions.Function1<? super java.lang.String, java.lang.String> r25, kotlin.jvm.functions.Function1<? super java.lang.String, java.lang.String> r26) {
        /*
            r0 = 0
            int r1 = kotlin.collections.CollectionsKt.getLastIndex(r23)
            r3 = r23
            java.lang.Iterable r3 = (java.lang.Iterable) r3
            r4 = 0
            r5 = r4
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            java.util.Collection r6 = (java.util.Collection) r6
            r7 = r3
            r8 = r4
            r9 = r7
            r10 = r4
            r11 = 0
            java.util.Iterator r12 = r9.iterator()
            r13 = 0
            r14 = 0
        L_0x0021:
            boolean r15 = r12.hasNext()
            if (r15 == 0) goto L_0x007c
            java.lang.Object r15 = r12.next()
            int r16 = r11 + 1
            r17 = r15
            r18 = r0
            r0 = r17
            java.lang.String r0 = (java.lang.String) r0
            r19 = r11
            r2 = r19
            if (r2 == 0) goto L_0x0041
            if (r2 != r1) goto L_0x003e
            goto L_0x0041
        L_0x003e:
            r20 = r1
            goto L_0x0051
        L_0x0041:
            r20 = r1
            r1 = r0
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            boolean r1 = kotlin.text.StringsKt.isBlank(r1)
            if (r1 == 0) goto L_0x0051
            r1 = 0
            r0 = r1
            r1 = r25
            goto L_0x006e
        L_0x0051:
            r1 = r26
            java.lang.Object r19 = r1.invoke(r0)
            r21 = r0
            r0 = r19
            java.lang.String r0 = (java.lang.String) r0
            if (r0 == 0) goto L_0x006a
            r1 = r25
            java.lang.Object r0 = r1.invoke(r0)
            java.lang.String r0 = (java.lang.String) r0
            if (r0 == 0) goto L_0x006c
            goto L_0x006e
        L_0x006a:
            r1 = r25
        L_0x006c:
            r0 = r21
        L_0x006e:
            if (r0 == 0) goto L_0x0075
            r2 = r14
            r6.add(r0)
        L_0x0075:
            r11 = r16
            r0 = r18
            r1 = r20
            goto L_0x0021
        L_0x007c:
            r18 = r0
            r20 = r1
            r1 = r25
            r0 = r6
            java.util.List r0 = (java.util.List) r0
            r2 = r0
            java.lang.Iterable r2 = (java.lang.Iterable) r2
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r12 = r24
            r0.<init>(r12)
            r3 = r0
            java.lang.Appendable r3 = (java.lang.Appendable) r3
            java.lang.String r0 = "\n"
            r4 = r0
            java.lang.CharSequence r4 = (java.lang.CharSequence) r4
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 124(0x7c, float:1.74E-43)
            r11 = 0
            java.lang.Appendable r0 = kotlin.collections.CollectionsKt.joinTo$default(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            java.lang.StringBuilder r0 = (java.lang.StringBuilder) r0
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "mapIndexedNotNull { inde…\"\\n\")\n        .toString()"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r0, r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.text.StringsKt__IndentKt.reindent$StringsKt__IndentKt(java.util.List, int, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1):java.lang.String");
    }
}
