package com.udemy.exception;

public class InvalidPlaybackPositionException extends RuntimeException {
    public InvalidPlaybackPositionException(String message) {
        super(message);
    }
}
