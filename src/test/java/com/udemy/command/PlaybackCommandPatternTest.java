package com.udemy.command;

import com.udemy.enums.AuthorshipRole;
import com.udemy.media.VideoContent;
import com.udemy.model.Course;
import com.udemy.model.CourseAuthorship;
import com.udemy.model.Instructor;
import com.udemy.model.Lecture;
import com.udemy.model.Module;
import com.udemy.model.Student;
import com.udemy.progress.Enrollment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Command Pattern: Playback Actions, Bookmarking & Undo Stack Tests")
class PlaybackCommandPatternTest {

    private Enrollment enrollment;
    private Lecture lecture;
    private PlaybackHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        Student student = new Student("s@test.com", "Student");
        Instructor instructor = new Instructor("i@test.com", "Instructor", "Topic");
        Course course = new Course("Command Pattern Demo", "Desc", 1000.0);
        course.addAuthorship(new CourseAuthorship(course.getId(), instructor, AuthorshipRole.LEAD_INSTRUCTOR, 100.0));

        Module m1 = new Module("M1", 1);
        lecture = new Lecture("L1", 1, new VideoContent("http://v.mp4", 1800)); // 30 mins
        m1.addLecture(lecture);
        course.addModule(m1);
        course.publish();

        enrollment = new Enrollment(student, course);
        historyManager = new PlaybackHistoryManager();
    }

    @Test
    @DisplayName("Seek Command: Records target position and supports undo back to previous offset")
    void testSeekCommandAndUndo() {
        // Initial playback position: 0
        assertEquals(0, enrollment.getProgress().getOrCreateLectureProgress(lecture.getId()).getLastWatchedPositionSeconds());

        // Seek to 600s (10 mins mark)
        SeekPlaybackCommand seekTo10Min = new SeekPlaybackCommand(enrollment, lecture.getId(), 600, 1800);
        historyManager.executeCommand(seekTo10Min);

        assertEquals(600, enrollment.getProgress().getOrCreateLectureProgress(lecture.getId()).getLastWatchedPositionSeconds());
        assertEquals(1, historyManager.getUndoStackDepth());

        // Seek forward to 900s (15 mins mark)
        SeekPlaybackCommand seekTo15Min = new SeekPlaybackCommand(enrollment, lecture.getId(), 900, 1800);
        historyManager.executeCommand(seekTo15Min);

        assertEquals(900, enrollment.getProgress().getOrCreateLectureProgress(lecture.getId()).getLastWatchedPositionSeconds());
        assertEquals(2, historyManager.getUndoStackDepth());

        // Undo seek -> reverts back to 600s
        assertTrue(historyManager.undo());
        assertEquals(600, enrollment.getProgress().getOrCreateLectureProgress(lecture.getId()).getLastWatchedPositionSeconds());

        // Undo again -> reverts back to 0s
        assertTrue(historyManager.undo());
        assertEquals(0, enrollment.getProgress().getOrCreateLectureProgress(lecture.getId()).getLastWatchedPositionSeconds());
    }

    @Test
    @DisplayName("Bookmark Command: Adds timestamped notes and supports undo")
    void testAddBookmarkCommandAndUndo() {
        List<Bookmark> bookmarks = new ArrayList<>();
        Bookmark b1 = new Bookmark(lecture.getId(), "student_1", 600, "Key concurrency invariant explained here");

        AddBookmarkCommand addCmd = new AddBookmarkCommand(bookmarks, b1);
        historyManager.executeCommand(addCmd);

        assertEquals(1, bookmarks.size());
        assertEquals("10:00", bookmarks.get(0).getFormattedTimestamp());
        assertEquals("Key concurrency invariant explained here", bookmarks.get(0).getNoteText());

        // Undo removes bookmark
        assertTrue(historyManager.undo());
        assertTrue(bookmarks.isEmpty());
    }
}
