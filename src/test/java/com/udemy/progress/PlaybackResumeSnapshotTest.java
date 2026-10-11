package com.udemy.progress;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Playback Resume Snapshot Value Object Tests")
class PlaybackResumeSnapshotTest {

    @Test
    @DisplayName("Snapshot Formatter: Correctly formats mm:ss representations for resume offsets")
    void testResumeTimeFormatting() {
        // 10 mins (600s) of 30 mins (1800s)
        PlaybackResumeSnapshot s1 = new PlaybackResumeSnapshot("c1", "m1", "l1", "Lec 1", 600, 1800);
        assertEquals("10:00 / 30:00", s1.getFormattedResumeTime());

        // 5 mins 5 secs (305s) of 15 mins (900s)
        PlaybackResumeSnapshot s2 = new PlaybackResumeSnapshot("c1", "m1", "l2", "Lec 2", 305, 900);
        assertEquals("05:05 / 15:00", s2.getFormattedResumeTime());

        // 0s of 45 mins (2700s)
        PlaybackResumeSnapshot s3 = new PlaybackResumeSnapshot("c1", "m1", "l3", "Lec 3", 0, 2700);
        assertEquals("00:00 / 45:00", s3.getFormattedResumeTime());

        assertTrue(s1.toString().contains("10:00 / 30:00"));
    }
}
