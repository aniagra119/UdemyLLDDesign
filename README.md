# Enterprise Online Learning Platform (Udemy) — Low-Level Design

[![Java 21](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![JUnit 5](https://img.shields.io/badge/JUnit-5.10.2-green.svg)](https://junit.org/junit5/)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()
[![Tests](https://img.shields.io/badge/Tests-23%20Passed-success.svg)]()
[![Architecture](https://img.shields.io/badge/Architecture-Composite%20%7C%20Observer%20%7C%20Strategy-blue.svg)]()

A comprehensive, production-grade Low-Level Design (LLD) and machine coding implementation of an enterprise online course platform (Udemy), featuring multi-instructor course publishing, composite curriculum hierarchy, polymorphic media resources, thread-safe resumable video playback, verified student ratings, and event-driven certificate issuance.

---

## 1. Requirements Matrix

| Requirement | Domain Entity / Pattern | Implementation Details | Test Coverage |
| :--- | :--- | :--- | :--- |
| **Multi-Author Course Publishing** | `CourseAuthorship`, `Instructor`, `Course` | M:N association modeling lead/co-instructors with explicit revenue share percentages. | [`CourseCompositeAndAuthorshipTest`](src/test/java/com/udemy/catalog/CourseCompositeAndAuthorshipTest.java) |
| **Curriculum Hierarchy** | Composite Pattern (`CourseComponent`) | Uniform rollup of duration in seconds and lecture counts across `Course`, `Module`, and `Lecture`. | [`CourseCompositeAndAuthorshipTest`](src/test/java/com/udemy/catalog/CourseCompositeAndAuthorshipTest.java) |
| **Video & Resource Attachments** | `VideoContent`, `LectureResource` | Mandatory primary video content plus polymorphic `FileAttachment` (PDF/ZIP) and `ReadingLink` references. | [`CourseCompositeAndAuthorshipTest`](src/test/java/com/udemy/catalog/CourseCompositeAndAuthorshipTest.java) |
| **Resumable Video Playback** | `PlaybackService`, `LectureProgress`, `PlaybackResumeSnapshot` | Tracks drop-off points down to the second (`lastWatchedPositionSeconds`). Generates resume snapshot returning 10:00 mark on a 30:00 lecture. | [`ResumablePlaybackAndProgressTest`](src/test/java/com/udemy/progress/ResumablePlaybackAndProgressTest.java) |
| **Auto-Completion Threshold** | Strategy Pattern (`PercentageThresholdCompletionStrategy`) | Automatically marks lectures completed when watched $\ge 90\%$; marks course completed when all lectures are done. | [`ResumablePlaybackAndProgressTest`](src/test/java/com/udemy/progress/ResumablePlaybackAndProgressTest.java) |
| **Verified Student Ratings** | `ReviewService`, `Review` | Enforces verified enrollment invariant; rejects unenrolled reviews; clamps ratings to 1.0–5.0; maintains real-time moving average. | [`CourseReviewAndRatingTest`](src/test/java/com/udemy/review/CourseReviewAndRatingTest.java) |
| **Asynchronous Completion Events** | Observer Pattern (`CourseEventPublisher`) | Emits `onCourseCompleted` event to trigger decoupled `CertificateGeneratorListener` upon 100% completion. | [`UdemyPlatformIntegrationTest`](src/test/java/com/udemy/UdemyPlatformIntegrationTest.java) |
| **Concurrency & Heartbeat Safety** | Synchronized locks & `ConcurrentHashMap` | Prevents race conditions during high-frequency heartbeat beacons from web and mobile clients under 20 concurrent threads. | [`ConcurrentPlaybackHeartbeatTest`](src/test/java/com/udemy/concurrency/ConcurrentPlaybackHeartbeatTest.java) |

---

## 2. Architecture & Design Patterns

```
                                  ┌───────────────────────────┐
                                  │   UdemyLearningPlatform   │ (Facade)
                                  └─────────────┬─────────────┘
                                                │
         ┌────────────────────────┬─────────────┴─────────────┬────────────────────────┐
         ▼                        ▼                           ▼                        ▼
┌─────────────────┐      ┌─────────────────┐         ┌─────────────────┐      ┌─────────────────┐
│CourseCatalogSvc │      │EnrollmentService│         │ PlaybackService │      │  ReviewService  │
│  - Course (Root)│      │  - Idempotent   │         │  - Heartbeats   │      │  - Verified Only│
│  - Module (Comp)│      │    Enrollment   │         │  - Resume Snap  │      │  - Clamped Stars│
│  - Lecture(Leaf)│      │  - Active/Done  │         │  - 90% Threshold│      │  - Moving Avg   │
└─────────────────┘      └─────────────────┘         └─────────────────┘      └─────────────────┘
```

1. **Composite Pattern (`CourseComponent`)**: Uniform interface implemented by `Course`, `Module`, and `Lecture`, allowing clients to calculate total course duration or lecture counts recursively.
2. **Strategy Pattern (`CompletionCriteriaStrategy`, `PricingStrategy`)**: Pluggable strategies for lecture auto-completion (90% threshold vs full watch) and course pricing (Standard vs Promotional Tier discounts).
3. **Observer Pattern (`CourseEventPublisher`, `CourseEventListener`)**: Decouples the playback engine from downstream post-completion side-effects (certificate generation, notifications, analytics).
4. **Facade Pattern (`UdemyLearningPlatform`)**: Provides a unified, high-level API coordinating catalog, enrollment, playback, and reviews.

---

## 3. Package Structure

```
com.udemy
├── enums
│   ├── AuthorshipRole.java
│   ├── CourseStatus.java
│   ├── EnrollmentStatus.java
│   ├── PricingType.java
│   └── ResourceType.java
├── event
│   ├── CertificateGeneratorListener.java
│   ├── CourseEventListener.java
│   └── CourseEventPublisher.java
├── exception
│   ├── CourseNotPublishedException.java
│   ├── InvalidPlaybackPositionException.java
│   ├── InvalidRatingException.java
│   ├── ResourceNotFoundException.java
│   └── UnenrolledStudentException.java
├── media
│   ├── FileAttachment.java
│   ├── LectureResource.java
│   ├── ReadingLink.java
│   ├── ResourceFactory.java
│   └── VideoContent.java
├── model
│   ├── BaseEntity.java
│   ├── Course.java
│   ├── CourseAuthorship.java
│   ├── CourseComponent.java
│   ├── Instructor.java
│   ├── Lecture.java
│   ├── Module.java
│   ├── Review.java
│   ├── Student.java
│   └── User.java
├── progress
│   ├── CourseProgress.java
│   ├── Enrollment.java
│   ├── LectureProgress.java
│   └── PlaybackResumeSnapshot.java
├── service
│   ├── CourseCatalogService.java
│   ├── EnrollmentService.java
│   ├── PlaybackService.java
│   ├── ReviewService.java
│   └── UdemyLearningPlatform.java
└── strategy
    ├── CompletionCriteriaStrategy.java
    ├── PercentageThresholdCompletionStrategy.java
    ├── PricingStrategy.java
    ├── PromotionalTierPricingStrategy.java
    └── StandardPricingStrategy.java
```

---

## 4. Test Execution & Verification

Run the exhaustive test suite:

```bash
mvn clean test
```

### Test Summary (23 Tests, 100% Pass Rate)

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.udemy.progress.PlaybackResumeSnapshotTest (1 test)
[INFO] Running com.udemy.progress.ResumablePlaybackAndProgressTest (3 tests)
[INFO] Running com.udemy.catalog.CourseCompositeAndAuthorshipTest (5 tests)
[INFO] Running com.udemy.UdemyPlatformIntegrationTest (1 test)
[INFO] Running com.udemy.concurrency.ConcurrentPlaybackHeartbeatTest (1 test)
[INFO] Running com.udemy.review.CourseReviewAndRatingTest (3 tests)
[INFO] Running com.udemy.model.DomainEntityTest (6 tests)
[INFO] Running com.udemy.service.EnrollmentServiceTest (3 tests)
[INFO] 
[INFO] Results:
[INFO] Tests run: 23, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```
