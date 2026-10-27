package com.example.mnp;

import android.net.Uri;

final class UrlPolicy {
    private UrlPolicy() {
    }

    static String validate(String input) {
        String value = input == null ? "" : input.trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Enter a website address.");
        }
        for (int i = 0; i < value.length(); i++) {
            if (Character.isWhitespace(value.charAt(i)) || Character.isISOControl(value.charAt(i))) {
                throw new IllegalArgumentException("The address must not contain spaces.");
            }
        }

        Uri uri = Uri.parse(value);
        if (!"https".equalsIgnoreCase(uri.getScheme())) {
            throw new IllegalArgumentException("Only HTTPS addresses are allowed.");
        }
        if (uri.getHost() == null || uri.getHost().isEmpty() || uri.getUserInfo() != null) {
            throw new IllegalArgumentException("Enter a valid HTTPS address without embedded credentials.");
        }
        return value;
    }
}
