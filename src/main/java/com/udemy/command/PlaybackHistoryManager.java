package com.udemy.command;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

public class PlaybackHistoryManager {
    private final Deque<PlaybackCommand> undoStack = new ArrayDeque<>();
    private final List<String> executionAuditLog = new ArrayList<>();

    public synchronized void executeCommand(PlaybackCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        command.execute();
        undoStack.push(command);
        executionAuditLog.add(command.getDescription());
    }

    public synchronized boolean undo() {
        if (undoStack.isEmpty()) {
            return false;
        }
        PlaybackCommand command = undoStack.pop();
        command.undo();
        executionAuditLog.add("UNDO: " + command.getDescription());
        return true;
    }

    public synchronized List<String> getExecutionAuditLog() {
        return Collections.unmodifiableList(executionAuditLog);
    }

    public synchronized int getUndoStackDepth() {
        return undoStack.size();
    }
}
