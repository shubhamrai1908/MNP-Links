package com.example.mnp;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public final class LinkManagerActivity extends Activity {
    private LinkStore linkStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        linkStore = new LinkStore(this);
        showLinks();
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
        int padding = Ui.dp(this, 20);
        content.setPadding(padding, Ui.dp(this, 18), padding, Ui.dp(this, 28));
        scroll.addView(content);

        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setGravity(Gravity.CENTER_VERTICAL);
        content.addView(toolbar, Ui.params(this, -1, 50));
        Button back = Ui.button(this, "‹", false);
        back.setTextSize(25);
        back.setOnClickListener(view -> finish());
        toolbar.addView(back, Ui.params(this, 48, 46));
        TextView heading = Ui.heading(this, "Manage links");
        heading.setTextSize(21);
        LinearLayout.LayoutParams headingParams = new LinearLayout.LayoutParams(0, -2, 1f);
        headingParams.setMargins(Ui.dp(this, 10), 0, 0, 0);
        toolbar.addView(heading, headingParams);

        LinearLayout intro = new LinearLayout(this);
        intro.setOrientation(LinearLayout.HORIZONTAL);
        intro.setGravity(Gravity.CENTER_VERTICAL);
        intro.setPadding(Ui.dp(this, 14), Ui.dp(this, 12), Ui.dp(this, 14), Ui.dp(this, 12));
        intro.setBackground(Ui.background(Ui.PALE_TEAL, 16, Ui.PALE_TEAL));
        LinearLayout.LayoutParams introParams = Ui.params(this, -1, -2);
        introParams.setMargins(0, Ui.dp(this, 12), 0, Ui.dp(this, 20));
        content.addView(intro, introParams);
        intro.addView(Ui.badge(this, "HTTPS"), Ui.params(this, 64, 30));
        TextView explanation = Ui.text(this,
                "Your shortcuts are saved on this device. Add secure HTTPS links.", 13, Ui.TEAL);
        LinearLayout.LayoutParams explanationParams = new LinearLayout.LayoutParams(0, -2, 1f);
        explanationParams.setMargins(Ui.dp(this, 10), 0, 0, 0);
        intro.addView(explanation, explanationParams);

        TextView section = Ui.text(this, "SAVED PORTALS", 11, Ui.MUTED);
        section.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        LinearLayout.LayoutParams sectionParams = Ui.params(this, -1, 26);
        sectionParams.setMargins(0, 0, 0, Ui.dp(this, 8));
        content.addView(section, sectionParams);

        if (links.isEmpty()) {
            LinearLayout empty = Ui.card(this);
            empty.setPadding(Ui.dp(this, 16), Ui.dp(this, 16), Ui.dp(this, 16), Ui.dp(this, 16));
            content.addView(empty, Ui.params(this, -1, -2));
            TextView emptyTitle = Ui.text(this, "Your list is empty", 17, Ui.INK);
            emptyTitle.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            empty.addView(emptyTitle);
            empty.addView(Ui.text(this, "Add a link to create your first shortcut.", 14, Ui.MUTED));
        }

        for (int i = 0; i < links.size(); i++) {
            addLinkCard(content, links.get(i), i);
        }

        Button add = Ui.button(this, "+   Add a link", true);
        add.setOnClickListener(view -> showEditor(-1));
        LinearLayout.LayoutParams addParams = Ui.params(this, -1, 50);
        addParams.setMargins(0, Ui.dp(this, 8), 0, Ui.dp(this, 10));
        content.addView(add, addParams);

        Button reset = Ui.button(this, "Restore default links", false);
        reset.setOnClickListener(view -> confirmReset());
        content.addView(reset, Ui.params(this, -1, 48));
        setContentView(scroll, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void addLinkCard(LinearLayout content, LinkItem link, int index) {
        LinearLayout card = Ui.card(this);
        card.setPadding(Ui.dp(this, 14), Ui.dp(this, 14), Ui.dp(this, 14), Ui.dp(this, 12));
        LinearLayout.LayoutParams cardParams = Ui.params(this, -1, -2);
        cardParams.setMargins(0, 0, 0, Ui.dp(this, 11));
        content.addView(card, cardParams);

        LinearLayout detailRow = new LinearLayout(this);
        detailRow.setOrientation(LinearLayout.HORIZONTAL);
        detailRow.setGravity(Gravity.CENTER_VERTICAL);
        card.addView(detailRow, Ui.params(this, -1, -2));
        detailRow.addView(
                Ui.badge(this, String.format(java.util.Locale.ROOT, "%02d", index + 1)),
                Ui.params(this, 42, 42));

        LinearLayout detail = Ui.column(this);
        detail.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams detailParams = new LinearLayout.LayoutParams(0, -2, 1f);
        detailParams.setMargins(Ui.dp(this, 11), 0, 0, 0);
        detailRow.addView(detail, detailParams);

        TextView title = Ui.text(this, link.title, 16, Ui.INK);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        detail.addView(title);
        TextView address = Ui.text(this, link.url, 13, Ui.MUTED);
        address.setSingleLine(true);
        address.setEllipsize(android.text.TextUtils.TruncateAt.MIDDLE);
        LinearLayout.LayoutParams addressParams = Ui.params(this, -1, -2);
        addressParams.setMargins(0, Ui.dp(this, 3), 0, 0);
        detail.addView(address, addressParams);

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams actionsParams = Ui.params(this, -1, 44);
        actionsParams.setMargins(-Ui.dp(this, 4), Ui.dp(this, 10), -Ui.dp(this, 4), 0);
        card.addView(actions, actionsParams);

        Button edit = Ui.button(this, "Edit link", false);
        edit.setOnClickListener(view -> showEditor(index));
        actions.addView(edit, Ui.weightedButtonParams(this));
        Button remove = Ui.button(this, "Remove", false);
        remove.setTextColor(android.graphics.Color.rgb(178, 52, 48));
        remove.setOnClickListener(view -> confirmRemove(index, link.title));
        actions.addView(remove, Ui.weightedButtonParams(this));
    }

    private void showEditor(int index) {
        List<LinkItem> current = loadOrShowError();
        if (current == null) {
            return;
        }
        boolean editing = index >= 0 && index < current.size();
        LinearLayout fields = Ui.column(this);
        fields.setPadding(Ui.dp(this, 20), Ui.dp(this, 8), Ui.dp(this, 20), 0);

        fields.addView(Ui.eyebrow(this, "LINK NAME"));
        EditText title = new EditText(this);
        title.setSingleLine(true);
        title.setHint("For example, MNP portal");
        title.setFilters(new InputFilter[]{new InputFilter.LengthFilter(60)});
        fields.addView(title, Ui.params(this, -1, 56));

        TextView addressLabel = Ui.eyebrow(this, "WEBSITE ADDRESS");
        LinearLayout.LayoutParams addressLabelParams = Ui.params(this, -1, 28);
        addressLabelParams.setMargins(0, Ui.dp(this, 10), 0, 0);
        fields.addView(addressLabel, addressLabelParams);
        EditText url = new EditText(this);
        url.setSingleLine(true);
        url.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        url.setHint("https://example.com");
        url.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2048)});
        fields.addView(url, Ui.params(this, -1, 56));

        if (editing) {
            title.setText(current.get(index).title);
            url.setText(current.get(index).url);
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(editing ? "Edit link" : "Add link")
                .setView(fields)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null)
                .create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            String name = title.getText().toString().trim();
            if (name.isEmpty()) {
                title.setError("Enter a name.");
                return;
            }
            final String safeUrl;
            try {
                safeUrl = UrlPolicy.validate(url.getText().toString());
            } catch (IllegalArgumentException exception) {
                url.setError(exception.getMessage());
                return;
            }

            List<LinkItem> updated = new ArrayList<>(current);
            LinkItem item = new LinkItem(name, safeUrl);
            if (editing) {
                updated.set(index, item);
            } else {
                updated.add(item);
            }
            try {
                linkStore.save(updated);
                dialog.dismiss();
                showLinks();
            } catch (IllegalStateException | IllegalArgumentException exception) {
                Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        }));
        dialog.show();
    }

    private void confirmRemove(int index, String title) {
        new AlertDialog.Builder(this)
                .setTitle("Remove link?")
                .setMessage("Remove \"" + title + "\" from this device?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Remove", (dialog, which) -> {
                    List<LinkItem> current = loadOrShowError();
                    if (current == null || index >= current.size()) {
                        return;
                    }
                    current.remove(index);
                    saveAndRefresh(current);
                })
                .show();
    }

    private void confirmReset() {
        new AlertDialog.Builder(this)
                .setTitle("Restore default links?")
                .setMessage("Your custom links will be replaced with the three default MNP links.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Restore", (dialog, which) -> {
                    linkStore.reset();
                    showLinks();
                })
                .show();
    }

    private List<LinkItem> loadOrShowError() {
        try {
            return linkStore.load();
        } catch (IllegalStateException exception) {
            showStorageError(exception.getMessage());
            return null;
        }
    }

    private void saveAndRefresh(List<LinkItem> links) {
        try {
            linkStore.save(links);
            showLinks();
        } catch (IllegalStateException | IllegalArgumentException exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showStorageError(String message) {
        LinearLayout content = Ui.column(this);
        Ui.setPageBackground(content);
        content.setPadding(Ui.dp(this, 20), Ui.dp(this, 24), Ui.dp(this, 20), Ui.dp(this, 20));
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
}
