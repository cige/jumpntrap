package com.jumpntrap.adapter;

/**
 * PlayerItem defines an item for the PlayerAdapter.
 */
public final class PlayerItem {
    /**
     * The related adapter.
     */
    private final PlayerAdapter adapter;

    /**
     * The text.
     */
    private String text;

    /**
     * Constructor.
     * @param adapter the related adapter.
     * @param text the text.
     */
    public PlayerItem(final PlayerAdapter adapter, final String text) {
        this.adapter = adapter;
        this.text = text;
    }

    /**
     * Get the text.
     * @return the text.
     */
    public final String getText() {
        return text;
    }

    /**
     * Set the text.
     * @param text the text to set.
     */
    public final void setText(final String text) {
        this.text = text;
        adapter.notifyDataSetChanged();
    }
}
