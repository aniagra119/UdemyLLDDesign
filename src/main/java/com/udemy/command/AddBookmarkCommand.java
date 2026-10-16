package com.udemy.command;

import java.util.List;
import java.util.Objects;

public class AddBookmarkCommand implements PlaybackCommand {
    private final List<Bookmark> bookmarkList;
    private final Bookmark bookmark;

    public AddBookmarkCommand(List<Bookmark> bookmarkList, Bookmark bookmark) {
        this.bookmarkList = Objects.requireNonNull(bookmarkList, "bookmarkList must not be null");
        this.bookmark = Objects.requireNonNull(bookmark, "bookmark must not be null");
    }

    @Override
    public void execute() {
        if (!bookmarkList.contains(bookmark)) {
            bookmarkList.add(bookmark);
        }
    }

    @Override
    public void undo() {
        bookmarkList.remove(bookmark);
    }

    @Override
    public String getDescription() {
        return "Add bookmark at " + bookmark.getFormattedTimestamp() + ": " + bookmark.getNoteText();
    }

    public Bookmark getBookmark() {
        return bookmark;
    }
}
