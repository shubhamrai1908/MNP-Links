package com.example.mnp;

import android.net.Uri;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

final class AdBlocker {
    private static final Set<String> AD_DOMAINS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "doubleclick.net",
            "googlesyndication.com",
            "googleadservices.com",
            "googletagservices.com",
            "adnxs.com",
            "adsrvr.org",
            "adform.net",
            "adroll.com",
            "criteo.com",
            "taboola.com",
            "outbrain.com",
            "advertising.com"
    )));

    private AdBlocker() {
    }

    static boolean shouldBlock(String url) {
        String host = Uri.parse(url).getHost();
        if (host == null) {
            return false;
        }
        String normalizedHost = host.toLowerCase(Locale.ROOT);
        for (String domain : AD_DOMAINS) {
            if (normalizedHost.equals(domain) || normalizedHost.endsWith("." + domain)) {
                return true;
            }
        }
        return false;
    }
}
