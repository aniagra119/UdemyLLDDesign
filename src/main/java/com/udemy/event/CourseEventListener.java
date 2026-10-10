package com.udemy.event;

import com.udemy.progress.Enrollment;

public interface CourseEventListener {
    void onCourseCompleted(Enrollment enrollment);
}
