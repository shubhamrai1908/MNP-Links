package com.example.mnp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.content.res.ColorStateList;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mnplinks.R;

final class Ui {
    static final int INK = Color.rgb(19, 39, 54);
    static final int MUTED = Color.rgb(104, 120, 132);
    static final int TEAL = Color.rgb(11, 102, 114);
    static final int MINT = Color.rgb(141, 226, 208);
    static final int PALE_TEAL = Color.rgb(230, 246, 243);
    static final int BACKGROUND = Color.rgb(244, 248, 249);
    static final int BORDER = Color.rgb(228, 235, 238);
    static final int WHITE = Color.WHITE;

    private Ui() {
    }

    static int dp(Context context, int value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }

    static LinearLayout column(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }

    static TextView text(Context context, String value, int sizeSp, int color) {
        TextView view = new TextView(context);
        view.setText(value);
        view.setTextSize(sizeSp);
        view.setTextColor(color);
        view.setGravity(Gravity.CENTER_VERTICAL);
        return view;
    }

    static TextView heading(Context context, String value) {
        TextView view = text(context, value, 26, INK);
        view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return view;
    }

    static TextView eyebrow(Context context, String value) {
        TextView view = text(context, value.toUpperCase(java.util.Locale.ROOT), 11, TEAL);
        view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return view;
    }

    static Button button(Context context, String label, boolean primary) {
        Button button = new Button(context);
        button.setText(label);
        button.setAllCaps(false);
        button.setTextColor(primary ? WHITE : INK);
        button.setTextSize(14);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setBackground(background(primary ? TEAL : WHITE, 14, primary ? TEAL : BORDER));
        button.setMinHeight(dp(context, 44));
        button.setPadding(dp(context, 12), 0, dp(context, 12), 0);
        button.setStateListAnimator(null);
        return button;
    }

    static LinearLayout card(Context context) {
        LinearLayout card = column(context);
        card.setBackground(background(WHITE, 20, BORDER));
        card.setElevation(dp(context, 1));
        return card;
    }

    static ImageView logo(Context context, int sizeDp) {
        ImageView logo = new ImageView(context);
        logo.setImageResource(R.drawable.ic_mnp_logo);
        logo.setContentDescription("MNP Links logo");
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        logo.setLayoutParams(params(context, sizeDp, sizeDp));
        return logo;
    }

    static TextView badge(Context context, String label) {
        TextView badge = text(context, label, 13, TEAL);
        badge.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        badge.setGravity(Gravity.CENTER);
        badge.setBackground(background(PALE_TEAL, 14, PALE_TEAL));
        return badge;
    }

    static void addSpace(Context context, LinearLayout parent, int heightDp) {
        parent.addView(new View(context), params(context, 1, heightDp));
    }

    static GradientDrawable background(int color, int radiusDp, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radiusDp);
        drawable.setStroke(1, strokeColor);
        return drawable;
    }

    static LinearLayout.LayoutParams params(Context context, int width, int height) {
        return new LinearLayout.LayoutParams(
                width < 0 ? width : dp(context, width),
                height < 0 ? height : dp(context, height));
    }

    static LinearLayout.LayoutParams weightedButtonParams(Context context) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(context, 48), 1f);
        params.setMargins(dp(context, 4), 0, dp(context, 4), 0);
        return params;
    }

    static void setPageBackground(View view) {
        view.setBackgroundColor(BACKGROUND);
    }

    static RippleDrawable rippleBackground(Context context, int color, int radiusDp) {
        return new RippleDrawable(
                ColorStateList.valueOf(Color.rgb(235, 243, 244)),
                background(color, radiusDp, BORDER),
                null);
    }
}
