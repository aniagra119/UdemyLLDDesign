package com.udemy.event;

import com.udemy.progress.Enrollment;

import java.util.ArrayList;
import java.util.List;

public class CourseEventPublisher {
    private final List<CourseEventListener> listeners = new ArrayList<>();

    public synchronized void subscribe(CourseEventListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public synchronized void unsubscribe(CourseEventListener listener) {
        listeners.remove(listener);
    }

    public synchronized void publishCourseCompleted(Enrollment enrollment) {
        for (CourseEventListener listener : listeners) {
            listener.onCourseCompleted(enrollment);
        }
    }
}
