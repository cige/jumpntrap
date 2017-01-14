package com.jumpntrap.adapter;

public final class PlayerItem {

    private final PlayerAdapter adapter;
    private String text;

    public PlayerItem(final PlayerAdapter adapter, final String text) {
        this.adapter = adapter;
        this.text = text;
    }

    public final String getText() {
        return text;
    }

    public final void setText(final String text) {
        this.text = text;
        adapter.notifyDataSetChanged();
    }

}
