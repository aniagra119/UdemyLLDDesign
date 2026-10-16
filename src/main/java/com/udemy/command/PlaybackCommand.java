package com.udemy.command;

public interface PlaybackCommand {
    void execute();
    void undo();
    String getDescription();
}
