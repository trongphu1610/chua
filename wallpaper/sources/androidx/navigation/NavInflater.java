package androidx.navigation;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import androidx.navigation.NavOptions;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

public class NavInflater {
    private static final String APPLICATION_ID_PLACEHOLDER = "${applicationId}";
    @NonNull
    public static final String METADATA_KEY_GRAPH = "android.nav.graph";
    private static final String TAG_ACTION = "action";
    private static final String TAG_ARGUMENT = "argument";
    private static final String TAG_DEEP_LINK = "deepLink";
    private static final String TAG_INCLUDE = "include";
    private static final ThreadLocal<TypedValue> sTmpValue = new ThreadLocal<>();
    private Context mContext;
    private NavigatorProvider mNavigatorProvider;

    public NavInflater(@NonNull Context context, @NonNull NavigatorProvider navigatorProvider) {
        this.mContext = context;
        this.mNavigatorProvider = navigatorProvider;
    }

    @Nullable
    public NavGraph inflateMetadataGraph() {
        int resid;
        Bundle metaData = this.mContext.getApplicationInfo().metaData;
        if (metaData == null || (resid = metaData.getInt(METADATA_KEY_GRAPH)) == 0) {
            return null;
        }
        return inflate(resid);
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0024 A[Catch:{ Exception -> 0x005a, all -> 0x0058 }] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x001c A[Catch:{ Exception -> 0x005a, all -> 0x0058 }] */
    @android.annotation.SuppressLint({"ResourceType"})
    @android.support.annotation.NonNull
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public androidx.navigation.NavGraph inflate(@android.support.annotation.NavigationRes int r10) {
        /*
            r9 = this;
            android.content.Context r0 = r9.mContext
            android.content.res.Resources r0 = r0.getResources()
            android.content.res.XmlResourceParser r1 = r0.getXml(r10)
            android.util.AttributeSet r2 = android.util.Xml.asAttributeSet(r1)
        L_0x000e:
            int r3 = r1.next()     // Catch:{ Exception -> 0x005a }
            r4 = r3
            r5 = 2
            if (r3 == r5) goto L_0x001a
            r3 = 1
            if (r4 == r3) goto L_0x001a
            goto L_0x000e
        L_0x001a:
            if (r4 == r5) goto L_0x0024
            org.xmlpull.v1.XmlPullParserException r3 = new org.xmlpull.v1.XmlPullParserException     // Catch:{ Exception -> 0x005a }
            java.lang.String r5 = "No start tag found"
            r3.<init>(r5)     // Catch:{ Exception -> 0x005a }
            throw r3     // Catch:{ Exception -> 0x005a }
        L_0x0024:
            java.lang.String r3 = r1.getName()     // Catch:{ Exception -> 0x005a }
            androidx.navigation.NavDestination r5 = r9.inflate(r0, r1, r2)     // Catch:{ Exception -> 0x005a }
            boolean r6 = r5 instanceof androidx.navigation.NavGraph     // Catch:{ Exception -> 0x005a }
            if (r6 != 0) goto L_0x0051
            java.lang.IllegalArgumentException r6 = new java.lang.IllegalArgumentException     // Catch:{ Exception -> 0x005a }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x005a }
            r7.<init>()     // Catch:{ Exception -> 0x005a }
            java.lang.String r8 = "Root element <"
            r7.append(r8)     // Catch:{ Exception -> 0x005a }
            r7.append(r3)     // Catch:{ Exception -> 0x005a }
            java.lang.String r8 = ">"
            r7.append(r8)     // Catch:{ Exception -> 0x005a }
            java.lang.String r8 = " did not inflate into a NavGraph"
            r7.append(r8)     // Catch:{ Exception -> 0x005a }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x005a }
            r6.<init>(r7)     // Catch:{ Exception -> 0x005a }
            throw r6     // Catch:{ Exception -> 0x005a }
        L_0x0051:
            r6 = r5
            androidx.navigation.NavGraph r6 = (androidx.navigation.NavGraph) r6     // Catch:{ Exception -> 0x005a }
            r1.close()
            return r6
        L_0x0058:
            r3 = move-exception
            goto L_0x0082
        L_0x005a:
            r3 = move-exception
            java.lang.RuntimeException r4 = new java.lang.RuntimeException     // Catch:{ all -> 0x0058 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0058 }
            r5.<init>()     // Catch:{ all -> 0x0058 }
            java.lang.String r6 = "Exception inflating "
            r5.append(r6)     // Catch:{ all -> 0x0058 }
            java.lang.String r6 = r0.getResourceName(r10)     // Catch:{ all -> 0x0058 }
            r5.append(r6)     // Catch:{ all -> 0x0058 }
            java.lang.String r6 = " line "
            r5.append(r6)     // Catch:{ all -> 0x0058 }
            int r6 = r1.getLineNumber()     // Catch:{ all -> 0x0058 }
            r5.append(r6)     // Catch:{ all -> 0x0058 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0058 }
            r4.<init>(r5, r3)     // Catch:{ all -> 0x0058 }
            throw r4     // Catch:{ all -> 0x0058 }
        L_0x0082:
            r1.close()
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.navigation.NavInflater.inflate(int):androidx.navigation.NavGraph");
    }

    @NonNull
    private NavDestination inflate(@NonNull Resources res, @NonNull XmlResourceParser parser, @NonNull AttributeSet attrs) throws XmlPullParserException, IOException {
        NavDestination dest = this.mNavigatorProvider.getNavigator(parser.getName()).createDestination();
        dest.onInflate(this.mContext, attrs);
        int innerDepth = parser.getDepth() + 1;
        while (true) {
            int next = parser.next();
            int type = next;
            if (next == 1) {
                break;
            }
            int depth = parser.getDepth();
            int depth2 = depth;
            if (depth < innerDepth && type == 3) {
                break;
            } else if (type == 2 && depth2 <= innerDepth) {
                String name = parser.getName();
                if (TAG_ARGUMENT.equals(name)) {
                    inflateArgument(res, dest, attrs);
                } else if (TAG_DEEP_LINK.equals(name)) {
                    inflateDeepLink(res, dest, attrs);
                } else if (TAG_ACTION.equals(name)) {
                    inflateAction(res, dest, attrs);
                } else if (TAG_INCLUDE.equals(name) && (dest instanceof NavGraph)) {
                    TypedArray a = res.obtainAttributes(attrs, R.styleable.NavInclude);
                    ((NavGraph) dest).addDestination(inflate(a.getResourceId(R.styleable.NavInclude_graph, 0)));
                    a.recycle();
                } else if (dest instanceof NavGraph) {
                    ((NavGraph) dest).addDestination(inflate(res, parser, attrs));
                }
            }
        }
        return dest;
    }

    private void inflateArgument(@NonNull Resources res, @NonNull NavDestination dest, @NonNull AttributeSet attrs) throws XmlPullParserException {
        TypedArray a = res.obtainAttributes(attrs, R.styleable.NavArgument);
        String name = a.getString(R.styleable.NavArgument_android_name);
        TypedValue value = sTmpValue.get();
        if (value == null) {
            value = new TypedValue();
            sTmpValue.set(value);
        }
        String argType = a.getString(R.styleable.NavArgument_argType);
        if (a.getValue(R.styleable.NavArgument_android_defaultValue, value)) {
            if ("string".equals(argType)) {
                dest.getDefaultArguments().putString(name, a.getString(R.styleable.NavArgument_android_defaultValue));
            } else {
                int i = value.type;
                boolean z = true;
                if (i != 1) {
                    switch (i) {
                        case 3:
                            String stringValue = value.string.toString();
                            if (argType == null) {
                                Long longValue = parseLongValue(stringValue);
                                if (longValue != null) {
                                    dest.getDefaultArguments().putLong(name, longValue.longValue());
                                    break;
                                }
                            } else if ("long".equals(argType)) {
                                Long longValue2 = parseLongValue(stringValue);
                                if (longValue2 != null) {
                                    dest.getDefaultArguments().putLong(name, longValue2.longValue());
                                    break;
                                } else {
                                    throw new XmlPullParserException("unsupported long value " + value.string);
                                }
                            }
                            dest.getDefaultArguments().putString(name, stringValue);
                            break;
                        case 4:
                            dest.getDefaultArguments().putFloat(name, value.getFloat());
                            break;
                        case 5:
                            dest.getDefaultArguments().putInt(name, (int) value.getDimension(res.getDisplayMetrics()));
                            break;
                        default:
                            if (value.type >= 16 && value.type <= 31) {
                                if (value.type != 18) {
                                    dest.getDefaultArguments().putInt(name, value.data);
                                    break;
                                } else {
                                    Bundle defaultArguments = dest.getDefaultArguments();
                                    if (value.data == 0) {
                                        z = false;
                                    }
                                    defaultArguments.putBoolean(name, z);
                                    break;
                                }
                            } else {
                                throw new XmlPullParserException("unsupported argument type " + value.type);
                            }
                            break;
                    }
                } else {
                    dest.getDefaultArguments().putInt(name, value.data);
                }
            }
        }
        a.recycle();
    }

    @Nullable
    private Long parseLongValue(String value) {
        if (!value.endsWith("L")) {
            return null;
        }
        try {
            String value2 = value.substring(0, value.length() - 1);
            if (value2.startsWith("0x")) {
                return Long.valueOf(Long.parseLong(value2.substring(2), 16));
            }
            return Long.valueOf(Long.parseLong(value2));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void inflateDeepLink(@NonNull Resources res, @NonNull NavDestination dest, @NonNull AttributeSet attrs) {
        TypedArray a = res.obtainAttributes(attrs, R.styleable.NavDeepLink);
        String uri = a.getString(R.styleable.NavDeepLink_uri);
        if (TextUtils.isEmpty(uri)) {
            throw new IllegalArgumentException("Every <deepLink> must include an app:uri");
        }
        dest.addDeepLink(uri.replace(APPLICATION_ID_PLACEHOLDER, this.mContext.getPackageName()));
        a.recycle();
    }

    private void inflateAction(@NonNull Resources res, @NonNull NavDestination dest, @NonNull AttributeSet attrs) {
        TypedArray a = res.obtainAttributes(attrs, R.styleable.NavAction);
        int id = a.getResourceId(R.styleable.NavAction_android_id, 0);
        NavAction action = new NavAction(a.getResourceId(R.styleable.NavAction_destination, 0));
        NavOptions.Builder builder = new NavOptions.Builder();
        builder.setLaunchSingleTop(a.getBoolean(R.styleable.NavAction_launchSingleTop, false));
        builder.setLaunchDocument(a.getBoolean(R.styleable.NavAction_launchDocument, false));
        builder.setClearTask(a.getBoolean(R.styleable.NavAction_clearTask, false));
        builder.setPopUpTo(a.getResourceId(R.styleable.NavAction_popUpTo, 0), a.getBoolean(R.styleable.NavAction_popUpToInclusive, false));
        builder.setEnterAnim(a.getResourceId(R.styleable.NavAction_enterAnim, -1));
        builder.setExitAnim(a.getResourceId(R.styleable.NavAction_exitAnim, -1));
        builder.setPopEnterAnim(a.getResourceId(R.styleable.NavAction_popEnterAnim, -1));
        builder.setPopExitAnim(a.getResourceId(R.styleable.NavAction_popExitAnim, -1));
        action.setNavOptions(builder.build());
        dest.putAction(id, action);
        a.recycle();
    }
}
