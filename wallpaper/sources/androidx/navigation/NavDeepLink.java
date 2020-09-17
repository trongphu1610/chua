package androidx.navigation;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class NavDeepLink {
    private static final Pattern SCHEME_PATTERN = Pattern.compile("^(\\w+-)*\\w+:");
    private final ArrayList<String> mArguments = new ArrayList<>();
    private final Pattern mPattern;

    NavDeepLink(@NonNull String uri) {
        StringBuffer uriRegex = new StringBuffer("^");
        if (!SCHEME_PATTERN.matcher(uri).find()) {
            uriRegex.append("http[s]?://");
        }
        Matcher matcher = Pattern.compile("\\{(.+?)\\}").matcher(uri);
        while (matcher.find()) {
            this.mArguments.add(matcher.group(1));
            matcher.appendReplacement(uriRegex, "");
            uriRegex.append("(.+?)");
        }
        matcher.appendTail(uriRegex);
        this.mPattern = Pattern.compile(uriRegex.toString());
    }

    /* access modifiers changed from: package-private */
    public boolean matches(@NonNull Uri deepLink) {
        return this.mPattern.matcher(deepLink.toString()).matches();
    }

    /* access modifiers changed from: package-private */
    @Nullable
    public Bundle getMatchingArguments(@NonNull Uri deepLink) {
        Matcher matcher = this.mPattern.matcher(deepLink.toString());
        if (!matcher.matches()) {
            return null;
        }
        Bundle bundle = new Bundle();
        int size = this.mArguments.size();
        for (int index = 0; index < size; index++) {
            bundle.putString(this.mArguments.get(index), Uri.decode(matcher.group(index + 1)));
        }
        return bundle;
    }
}
