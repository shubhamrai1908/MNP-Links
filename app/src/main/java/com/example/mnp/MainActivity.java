package com.example.mnp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

public final class MainActivity extends Activity {
    private LinkStore linkStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        linkStore = new LinkStore(this);
        showLinks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (linkStore != null) {
            showLinks();
        }
    }

    private void showLinks() {
        List<LinkItem> links;
        try {
            links = linkStore.load();
        } catch (IllegalStateException exception) {
            showStorageError(exception.getMessage());
            return;
        }

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        Ui.setPageBackground(scroll);
        LinearLayout content = Ui.column(this);
        int sidePadding = Ui.dp(this, 22);
        content.setPadding(sidePadding, Ui.dp(this, 20), sidePadding, Ui.dp(this, 28));
        scroll.addView(content);

        addHeader(content);
        Ui.addSpace(this, content, 22);
        addWelcomeCard(content);
        Ui.addSpace(this, content, 24);
        addSectionHeading(content, links.size());

        if (links.isEmpty()) {
            addEmptyState(content);
        } else {
            for (int i = 0; i < links.size(); i++) {
                addLinkCard(content, links.get(i), i);
            }
        }

        Ui.addSpace(this, content, 10);
        addSafetyNote(content);
        setContentView(scroll, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void addHeader(LinearLayout content) {
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        content.addView(header, Ui.params(this, -1, 52));

        header.addView(Ui.logo(this, 46));

        LinearLayout brand = Ui.column(this);
        brand.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams brandParams = new LinearLayout.LayoutParams(0, -2, 1f);
        brandParams.setMargins(Ui.dp(this, 10), 0, Ui.dp(this, 6), 0);
        header.addView(brand, brandParams);
        TextView name = Ui.text(this, "MNP Links", 17, Ui.INK);
        name.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        brand.addView(name);
        brand.addView(Ui.text(this, "Your portability shortcuts", 12, Ui.MUTED));

        Button manage = Ui.button(this, "Manage", false);
        manage.setOnClickListener(view -> startActivity(new Intent(this, LinkManagerActivity.class)));
        header.addView(manage, Ui.params(this, 94, 44));
    }

    private void addWelcomeCard(LinearLayout content) {
        LinearLayout hero = Ui.column(this);
        hero.setPadding(Ui.dp(this, 20), Ui.dp(this, 19), Ui.dp(this, 20), Ui.dp(this, 20));
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{Color.rgb(11, 102, 114), Color.rgb(19, 77, 95)});
        gradient.setCornerRadius(Ui.dp(this, 24));
        hero.setBackground(gradient);
        hero.setElevation(Ui.dp(this, 2));
        content.addView(hero, Ui.params(this, -1, -2));

        TextView label = Ui.text(this, "MOBILE NUMBER PORTABILITY", 11, Ui.MINT);
        label.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        hero.addView(label);

        TextView title = Ui.text(this, "Your MNP portals,\none tap away.", 25, Ui.WHITE);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setLineSpacing(Ui.dp(this, 2), 1f);
        LinearLayout.LayoutParams titleParams = Ui.params(this, -1, -2);
        titleParams.setMargins(0, Ui.dp(this, 10), 0, Ui.dp(this, 7));
        hero.addView(title, titleParams);

        TextView subtitle = Ui.text(this, "Choose a portal below to get started.", 14, Color.rgb(222, 239, 239));
        hero.addView(subtitle);
    }

    private void addSectionHeading(LinearLayout content, int linkCount) {
        LinearLayout heading = new LinearLayout(this);
        heading.setOrientation(LinearLayout.HORIZONTAL);
        heading.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams headingParams = Ui.params(this, -1, 34);
        headingParams.setMargins(0, 0, 0, Ui.dp(this, 10));
        content.addView(heading, headingParams);

        TextView title = Ui.text(this, "Your portals", 19, Ui.INK);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        heading.addView(title, new LinearLayout.LayoutParams(0, -1, 1f));

        TextView count = Ui.badge(this, linkCount + (linkCount == 1 ? " link" : " links"));
        heading.addView(count, Ui.params(this, -2, 28));
        count.setPadding(Ui.dp(this, 10), 0, Ui.dp(this, 10), 0);
    }

    private void addLinkCard(LinearLayout content, LinkItem link, int index) {
        LinearLayout card = Ui.card(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(Ui.dp(this, 14), Ui.dp(this, 14), Ui.dp(this, 12), Ui.dp(this, 14));
        card.setBackground(Ui.rippleBackground(this, Ui.WHITE, 20));
        card.setClickable(true);
        card.setFocusable(true);
        card.setOnClickListener(view -> openLink(link));

        LinearLayout.LayoutParams cardParams = Ui.params(this, -1, -2);
        cardParams.setMargins(0, 0, 0, Ui.dp(this, 11));
        content.addView(card, cardParams);

        TextView indexBadge = Ui.badge(this, String.format(java.util.Locale.ROOT, "%02d", index + 1));
        card.addView(indexBadge, Ui.params(this, 46, 46));

        LinearLayout detail = Ui.column(this);
        detail.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams detailParams = new LinearLayout.LayoutParams(0, -2, 1f);
        detailParams.setMargins(Ui.dp(this, 12), 0, Ui.dp(this, 6), 0);
        card.addView(detail, detailParams);

        TextView title = Ui.text(this, link.title, 16, Ui.INK);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setSingleLine(true);
        title.setEllipsize(android.text.TextUtils.TruncateAt.END);
        detail.addView(title);

        TextView address = Ui.text(this, UriHelper.host(link.url), 12, Ui.MUTED);
        address.setSingleLine(true);
        address.setEllipsize(android.text.TextUtils.TruncateAt.MIDDLE);
        LinearLayout.LayoutParams addressParams = Ui.params(this, -1, -2);
        addressParams.setMargins(0, Ui.dp(this, 4), 0, 0);
        detail.addView(address, addressParams);

        TextView open = Ui.text(this, "Open  ›", 13, Ui.TEAL);
        open.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        open.setGravity(Gravity.CENTER);
        card.addView(open, Ui.params(this, 62, 42));
    }

    private void addEmptyState(LinearLayout content) {
        LinearLayout empty = Ui.card(this);
        empty.setPadding(Ui.dp(this, 18), Ui.dp(this, 18), Ui.dp(this, 18), Ui.dp(this, 18));
        content.addView(empty, Ui.params(this, -1, -2));
        TextView title = Ui.text(this, "No portals added yet", 17, Ui.INK);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        empty.addView(title);
        TextView note = Ui.text(this, "Add a website to keep your MNP shortcuts here.", 14, Ui.MUTED);
        LinearLayout.LayoutParams noteParams = Ui.params(this, -1, -2);
        noteParams.setMargins(0, Ui.dp(this, 6), 0, Ui.dp(this, 14));
        empty.addView(note, noteParams);
        Button add = Ui.button(this, "Add your first link", true);
        add.setOnClickListener(view -> startActivity(new Intent(this, LinkManagerActivity.class)));
        empty.addView(add, Ui.params(this, -1, 48));
    }

    private void addSafetyNote(LinearLayout content) {
        LinearLayout note = new LinearLayout(this);
        note.setOrientation(LinearLayout.HORIZONTAL);
        note.setGravity(Gravity.CENTER_VERTICAL);
        note.setPadding(Ui.dp(this, 14), Ui.dp(this, 12), Ui.dp(this, 14), Ui.dp(this, 12));
        note.setBackground(Ui.background(Ui.PALE_TEAL, 16, Ui.PALE_TEAL));
        content.addView(note, Ui.params(this, -1, -2));

        TextView secure = Ui.badge(this, "TLS");
        note.addView(secure, Ui.params(this, 44, 30));
        TextView message = Ui.text(this,
                "HTTPS only. Basic third-party ad filtering is enabled.", 12, Ui.TEAL);
        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(0, -2, 1f);
        messageParams.setMargins(Ui.dp(this, 10), 0, 0, 0);
        note.addView(message, messageParams);
    }

    private void openLink(LinkItem link) {
        Intent intent = new Intent(this, BrowserActivity.class);
        intent.putExtra(BrowserActivity.EXTRA_TITLE, link.title);
        intent.putExtra(BrowserActivity.EXTRA_URL, link.url);
        startActivity(intent);
    }

    private void showStorageError(String message) {
        LinearLayout content = Ui.column(this);
        Ui.setPageBackground(content);
        content.setPadding(Ui.dp(this, 22), Ui.dp(this, 24), Ui.dp(this, 22), Ui.dp(this, 20));
        content.addView(Ui.logo(this, 54));
        Ui.addSpace(this, content, 16);
        content.addView(Ui.heading(this, "Unable to load links"));
        TextView error = Ui.text(this, message, 16, Ui.MUTED);
        LinearLayout.LayoutParams errorParams = Ui.params(this, -1, -2);
        errorParams.setMargins(0, Ui.dp(this, 12), 0, Ui.dp(this, 20));
        content.addView(error, errorParams);
        Button reset = Ui.button(this, "Restore default links", true);
        reset.setOnClickListener(view -> {
            linkStore.reset();
            showLinks();
        });
        content.addView(reset);
        setContentView(content);
    }

    private static final class UriHelper {
        static String host(String url) {
            String host = android.net.Uri.parse(url).getHost();
            return host == null ? url : host;
        }
    }
}
