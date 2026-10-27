package com.example.mnp;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

final class LinkStore {
    private static final String PREFERENCES = "mnp_links";
    private static final String LINKS_KEY = "links";
    private static final String OLD_ZONE_TWO_URL = "https://121.241.253.44/NPCWebGUI/login.do";
    private static final String ZONE_TWO_URL = "https://49.249.27.250/NPCWebGUI/login.do";

    private final SharedPreferences preferences;

    LinkStore(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    List<LinkItem> load() {
        String saved = preferences.getString(LINKS_KEY, null);
        if (saved == null) {
            return defaultLinks();
        }

        try {
            JSONArray json = new JSONArray(saved);
            List<LinkItem> links = new ArrayList<>();
            boolean zoneTwoUpdated = false;
            for (int i = 0; i < json.length(); i++) {
                JSONObject entry = json.getJSONObject(i);
                String title = entry.getString("title");
                String url = UrlPolicy.validate(entry.getString("url"));
                if (title.trim().isEmpty()) {
                    throw new JSONException("A saved link has an empty title");
                }
                if ("MNP Zone 2".equals(title) && OLD_ZONE_TWO_URL.equals(url)) {
                    url = ZONE_TWO_URL;
                    zoneTwoUpdated = true;
                }
                links.add(new LinkItem(title, url));
            }
            if (zoneTwoUpdated) {
                save(links);
            }
            return links;
        } catch (JSONException | IllegalArgumentException exception) {
            throw new IllegalStateException("Saved links could not be read. Reset the links to continue.", exception);
        }
    }

    void save(List<LinkItem> links) {
        JSONArray json = new JSONArray();
        for (LinkItem link : links) {
            JSONObject entry = new JSONObject();
            try {
                entry.put("title", link.title);
                entry.put("url", UrlPolicy.validate(link.url));
            } catch (JSONException | IllegalArgumentException exception) {
                throw new IllegalArgumentException("A link could not be saved.", exception);
            }
            json.put(entry);
        }

        if (!preferences.edit().putString(LINKS_KEY, json.toString()).commit()) {
            throw new IllegalStateException("Links could not be saved on this device.");
        }
    }

    void reset() {
        save(defaultLinks());
    }

    static List<LinkItem> defaultLinks() {
        List<LinkItem> links = new ArrayList<>();
        links.add(new LinkItem("MNP Zone 1", "https://sa.npindia.in/home.do"));
        links.add(new LinkItem("MNP Zone 2", ZONE_TWO_URL));
        links.add(new LinkItem("FREE MNP", "https://freecarrierlookup.com/"));
        return links;
    }

}
