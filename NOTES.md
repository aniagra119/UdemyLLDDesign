# Enterprise Online Learning Platform (Udemy) — Master Architecture & Engineering Guide

A comprehensive, production-grade reference for designing, implementing, and scaling online learning platforms, video playback resume engines, and course rating systems.

---

## 1. Domain Responsibility Boundaries

| Layer / Component | Type | Core Responsibility | Anti-Patterns to Avoid |
| :--- | :--- | :--- | :--- |
| **`Course`** | Aggregate Root / Composite | Course curriculum root, modules container, and moving average rating calculation. | **Never** embed direct HTTP streaming sockets inside Course entities. |
| **`Module`** | Composite | Ordered collection of lectures within a course. Rolls up duration and lecture counts. | **Never** hardcode lecture indices as fixed array positions. |
| **`Lecture`** | Composite Leaf | Holds mandatory primary `VideoContent` and polymorphic `LectureResource` attachments. | **Never** create subclass variants (`PdfLecture`, `VideoLecture`) when attachments are heterogeneous. |
| **`CourseAuthorship`** | Association Entity | Models M:N multi-instructor relationships with revenue share percentages and explicit roles. | **Never** model instructors as a simple flat string or single foreign key. |
| **`CourseProgress`** | Aggregate | Manages per-lecture playback positions and calculates real-time overall course completion percentage. | **Never** mutate progress asynchronously without synchronization. |
| **`PlaybackService`** | Domain Service | Thread-safe intake for high-frequency client playback heartbeats (every 5–10s) and resume snapshot generation. | **Never** query disk or run heavy DB joins during active heartbeat beacons. |
| **`ReviewService`** | Domain Service | Enforces verified purchase invariant, 1.0–5.0 star bounds, and triggers moving average recalculations. | **Never** allow unverified or duplicate reviews from the same learner. |

---

## 2. Resumable Video Playback Mechanics

### The 10-Minute Drop-off Invariant
When a learner watches 10 minutes (600s) of a 30-minute (1800s) lecture and drops off:
1. Client emits periodic heartbeat beacons:
   ```java
   playbackService.recordHeartbeat(enrollment, lectureId, currentSeconds);
   ```
2. Server validates $0 \le \text{currentSeconds} \le \text{totalDurationSeconds}$.
3. `LectureProgress` atomically records:
   - `lastWatchedPositionSeconds = 600`
   - `maxWatchedPositionSeconds = \max(previousMax, 600)`
4. Upon next login, calling `resumePlayback()` generates an immutable `PlaybackResumeSnapshot`:
   ```java
   PlaybackResumeSnapshot snapshot = enrollment.resumePlayback().orElseThrow();
   // Returns: lectureId, offset: 600s, total: 1800s, formatted: "10:00 / 30:00"
   ```
5. Player frontend seeks directly to `t = 600s` with zero manual scrubbing needed.

---

## 3. Concurrency & High-Frequency Heartbeats

In a large-scale streaming platform, learners on web and mobile devices emit heartbeats every 5 seconds. Scrubbing forward/backward causes out-of-order beacon delivery:

```java
public synchronized void updatePosition(int currentSeconds, int totalDurationSeconds, CompletionCriteriaStrategy strategy) {
    int boundedSeconds = Math.min(Math.max(0, currentSeconds), totalDurationSeconds);
    this.lastWatchedPositionSeconds = boundedSeconds;
    this.maxWatchedPositionSeconds = Math.max(this.maxWatchedPositionSeconds, boundedSeconds);
    this.lastAccessedAt = LocalDateTime.now();

    if (!this.completed && strategy != null && strategy.isLectureCompleted(lastWatchedPositionSeconds, maxWatchedPositionSeconds, totalDurationSeconds)) {
        this.completed = true;
    }
}
```

- **Thread-Safety Guarantee**: `ConcurrentHashMap` holds per-lecture progress; `synchronized` blocks inside `LectureProgress` ensure concurrent heartbeats update timestamps monotonically without race conditions. Tested under 20 concurrent threads using `CountDownLatch`.

---

## 4. Verified Review & Moving Average Rating Math

To guarantee review authenticity:
1. **Verified Purchase Invariant**:
   $$\text{Student } S \text{ can rate Course } C \iff S.\text{isEnrolled}(C.\text{getId}()) == \text{true}$$
2. **Clamping Invariant**:
   $$1.0 \le \text{Rating} \le 5.0$$
3. **Idempotent Single Review**:
   $$\text{ReviewCount}(S, C) \le 1$$
   Subsequent submissions by the same student update the existing review rather than skewing the rating count.
4. **Moving Average Formula**:
   $$\bar{R} = \frac{\sum_{i=1}^{N} R_i}{N}$$
   Rounded to 2 decimal places.

---

## 5. Socratic Interview Trade-offs & Rubric

| Dimension | Junior Approach | Senior / Staff Architect Resolution |
| :--- | :--- | :--- |
| **Curriculum Structure** | Flat list of lectures with arbitrary string module names. | **Composite Pattern** (`CourseComponent` $\to$ `Course`, `Module`, `Lecture`) aggregating duration and lecture counts recursively. |
| **Multi-Instructors** | Single `instructorId` foreign key on Course. | **Association Entity** (`CourseAuthorship`) with explicit `AuthorshipRole` and revenue split percentage. |
| **Media Attachments** | Subclassing lectures (`VideoLecture`, `PdfLecture`). | **Polymorphic Resources** (`LectureResource` $\to$ `FileAttachment`, `ReadingLink`) attached to a single Lecture holding a primary `VideoContent`. |
| **Progress Resume** | Storing only a single global integer percentage on enrollment. | Granular **`LectureProgress`** with `lastWatchedPositionSeconds`, `maxWatchedPositionSeconds`, and `PlaybackResumeSnapshot`. |
| **Course Completion** | Polling database tables on client refresh. | **Observer Pattern** (`CourseEventPublisher`) asynchronously triggering `CertificateGeneratorListener`. |
