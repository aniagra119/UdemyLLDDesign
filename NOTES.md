# Enterprise Online Learning Platform (Udemy) — Master Architecture & Engineering Guide

A comprehensive, production-grade reference for designing, implementing, and scaling online learning platforms, video playback resume engines, multi-course checkout, and course rating systems.

---

## 1. Complete Design Patterns & Domain Responsibility Matrix

| Pattern | Component | Core Responsibility | Why It Genuinely Makes Sense |
| :--- | :--- | :--- | :--- |
| **Composite** | `CourseComponent` $\to$ `Course`, `Module`, `Lecture` | Uniform duration rollup and lecture counts across the curriculum tree. | Eliminates manual loops when aggregating duration across nested modules. |
| **State** | `CourseState` (`Draft`, `Published`, `Archived`) | Encapsulates behavior and access control per lifecycle stage. | Prevents enrolling in draft/archived courses; locks syllabus changes once published to protect active learner progress. |
| **Chain of Responsibility** | `PublishingValidationChain` (`InstructorCheck` $\to$ `CurriculumCheck` $\to$ `VideoQualityCheck`) | Automated quality and completeness gate before a course transitions to `PUBLISHED`. | Decouples compliance rules from the Course aggregate; allows adding copyright/audio checks without modifying entities. |
| **Adapter** | `CdnStreamAdapter` (`AwsCloudFrontAdapter`, `CloudflareStreamAdapter`) | Bridges vendor streaming token APIs to a uniform internal interface. | Decouples playback logic from AWS CloudFront key-pairs or Cloudflare HMAC tokens; enables multi-CDN switching. |
| **Decorator** | `CourseProduct` $\to$ `VerifiedCertificateAddon`, `MentorshipDoubtSupportAddon`, `OfflineDownloadAddon` | Dynamically wraps courses with value-added features and fee structures. | Handles combinatorial entitlement bundles ($2^N$) without class hierarchy explosion. |
| **Command** | `PlaybackCommand` (`SeekPlaybackCommand`, `AddBookmarkCommand`, `ToggleLectureCompleteCommand`) | Encapsulates user playback interactions with undo stack and audit logs. | Enables scrubbing rollback, note bookmark undo, and offline command synchronization queues. |
| **Factory / Registry** | `PaymentProcessorFactory` | Decoupled factory registry mapping `PaymentMethod` to processors. | Open-Closed Principle (OCP): new payment methods can be registered without editing checkout code. |
| **Strategy** | `PaymentProcessor` SPI, `CompletionCriteriaStrategy` (90% threshold), `PricingStrategy` | Pluggable algorithms for financial settlement, video auto-completion, and discounting. | Supports custom payment vendors and custom completion rules without altering core entities. |
| **Builder** | `CourseBuilder`, `ModuleBuilder`, `LectureBuilder` | Fluent construction of complex 3-tier course hierarchies. | Eliminates telescoping constructors and nested collections boilerplate. |
| **Observer** | `CourseEventPublisher` $\to$ `CertificateGeneratorListener` | Asynchronously generates certificates upon 100% course completion. | Decouples playback tracking from downstream notification and certification side-effects. |
| **Facade** | `UdemyLearningPlatform` | High-level unified entry point for clients. | Coordinates catalog, playback, multi-course checkout, and review services. |

---

## 2. Multi-Course Checkout & Financial Settlement Architecture

In Udemy and Coursera, learners frequently add multiple courses to a cart and checkout in one transaction.

```
[ Shopping Cart (2 Courses) ] ──► OrderService.checkout(student, items, method, details)
                                           │
                                ┌──────────┴──────────┐
                                ▼                     ▼
                    PaymentProcessorFactory       Order Aggregate
                    • Resolves PaymentProcessor   • State: CREATED ──► PAID / FAILED
                    • CreditCard / UPI / PayPal   • Calculates total payable amount
                                │
                                ▼ (On Payment Success)
                    EnrollmentService.enroll(student, course)
                    • Creates atomic enrollments for all courses in order
```

---

## 3. Resumable Video Playback Mechanics

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

## 4. Concurrency & High-Frequency Heartbeats

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

## 5. Verified Review & Moving Average Rating Math

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
