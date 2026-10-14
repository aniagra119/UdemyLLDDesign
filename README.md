# Enterprise Online Learning Platform (Udemy) — Low-Level Design

[![Java 21](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![JUnit 5](https://img.shields.io/badge/JUnit-5.10.2-green.svg)](https://junit.org/junit5/)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()
[![Tests](https://img.shields.io/badge/Tests-35%20Passed-success.svg)]()
[![Architecture](https://img.shields.io/badge/Architecture-Composite%20%7C%20State%20%7C%20Chain%20%7C%20Adapter%20%7C%20Builder-blue.svg)]()

A comprehensive, production-grade Low-Level Design (LLD) and machine coding implementation of an enterprise online course platform (Udemy), featuring multi-instructor course publishing, composite curriculum hierarchy, lifecycle state machines, pre-publishing audit chains, multi-cloud CDN streaming adapters, fluent builders, thread-safe resumable video playback, verified student ratings, and event-driven certificate issuance.

---

## 1. Requirements Matrix

| Requirement | Domain Entity / Pattern | Implementation Details | Test Coverage |
| :--- | :--- | :--- | :--- |
| **Multi-Author Course Publishing** | `CourseAuthorship`, `Instructor`, `Course` | M:N association modeling lead/co-instructors with explicit revenue share percentages. | [`CourseCompositeAndAuthorshipTest`](src/test/java/com/udemy/catalog/CourseCompositeAndAuthorshipTest.java) |
| **Curriculum Hierarchy** | **Composite Pattern** (`CourseComponent`) | Uniform rollup of duration in seconds and lecture counts across `Course`, `Module`, and `Lecture`. | [`CourseCompositeAndAuthorshipTest`](src/test/java/com/udemy/catalog/CourseCompositeAndAuthorshipTest.java) |
| **Lifecycle State Machine** | **State Pattern** (`CourseState`) | `Draft`, `Published`, `Archived`. Rejects enrollments in draft; locks curriculum modification once live to protect learner progress. | [`CourseStatePatternTest`](src/test/java/com/udemy/state/CourseStatePatternTest.java) |
| **Pre-Publishing Audit Pipeline** | **Chain of Responsibility** (`CoursePublishingValidator`) | Automated quality checks: `InstructorValidator` (100% split) $\to$ `CurriculumCompletenessValidator` $\to$ `VideoQualityValidator`. | [`CoursePublishingValidationChainTest`](src/test/java/com/udemy/validation/chain/CoursePublishingValidationChainTest.java) |
| **Multi-Cloud CDN Streaming** | **Adapter Pattern** (`CdnStreamAdapter`) | Bridges AWS CloudFront Key-Pair signed URLs and Cloudflare Stream HMAC HLS manifests to uniform internal streaming interface. | [`CdnStreamAdapterTest`](src/test/java/com/udemy/adapter/cdn/CdnStreamAdapterTest.java) |
| **Fluent Curriculum Assembly** | **Builder Pattern** (`CourseBuilder`, `ModuleBuilder`) | Declarative, fluent DSL constructing 3-tier curriculum trees without telescoping constructors. | [`CourseBuilderTest`](src/test/java/com/udemy/builder/CourseBuilderTest.java) |
| **Resumable Video Playback** | `PlaybackService`, `LectureProgress`, `PlaybackResumeSnapshot` | Tracks drop-off points down to the second (`lastWatchedPositionSeconds`). Returns 10:00 mark on a 30:00 lecture. | [`ResumablePlaybackAndProgressTest`](src/test/java/com/udemy/progress/ResumablePlaybackAndProgressTest.java) |
| **Auto-Completion Threshold** | **Strategy Pattern** (`PercentageThresholdCompletionStrategy`) | Automatically marks lectures completed when watched $\ge 90\%$; marks course completed when all lectures are done. | [`ResumablePlaybackAndProgressTest`](src/test/java/com/udemy/progress/ResumablePlaybackAndProgressTest.java) |
| **Verified Student Ratings** | `ReviewService`, `Review` | Enforces verified enrollment invariant; rejects unenrolled reviews; clamps ratings to 1.0–5.0; maintains real-time moving average. | [`CourseReviewAndRatingTest`](src/test/java/com/udemy/review/CourseReviewAndRatingTest.java) |
| **Asynchronous Completion Events** | **Observer Pattern** (`CourseEventPublisher`) | Emits `onCourseCompleted` event to trigger decoupled `CertificateGeneratorListener` upon 100% completion. | [`UdemyPlatformIntegrationTest`](src/test/java/com/udemy/UdemyPlatformIntegrationTest.java) |
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
│  - State Pattern│      │  - State Guard  │         │  - Heartbeats   │      │  - Verified Only│
│  - Chain Audit  │      │  - Idempotent   │         │  - Resume Snap  │      │  - Clamped Stars│
│  - CDN Adapter  │      │    Enrollment   │         │  - 90% Threshold│      │  - Moving Avg   │
└─────────────────┘      └─────────────────┘         └─────────────────┘      └─────────────────┘
```

---

## 3. Package Structure

```
com.udemy
├── adapter
│   └── cdn
│       ├── AwsCloudFrontAdapter.java
│       ├── CdnStreamAdapter.java
│       ├── CloudflareStreamAdapter.java
│       └── vendor
│           ├── aws/AwsCloudFrontClient.java
│           └── cloudflare/CloudflareStreamClient.java
├── builder
│   ├── CourseBuilder.java
│   ├── LectureBuilder.java
│   └── ModuleBuilder.java
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
├── state
│   ├── ArchivedCourseState.java
│   ├── CourseState.java
│   ├── DraftCourseState.java
│   └── PublishedCourseState.java
├── strategy
│   ├── CompletionCriteriaStrategy.java
│   ├── PercentageThresholdCompletionStrategy.java
│   ├── PricingStrategy.java
│   ├── PromotionalTierPricingStrategy.java
│   └── StandardPricingStrategy.java
└── validation
    └── chain
        ├── AbstractCoursePublishingValidator.java
        ├── CoursePublishingValidator.java
        ├── CourseValidationException.java
        ├── CourseValidationPipeline.java
        ├── CurriculumCompletenessValidator.java
        ├── InstructorValidator.java
        └── VideoQualityValidator.java
```

---

## 4. Test Execution & Verification

Run the comprehensive test suite:

```bash
mvn clean test
```

### Test Summary (35 Tests, 100% Pass Rate)

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.udemy.progress.PlaybackResumeSnapshotTest (1 test)
[INFO] Running com.udemy.progress.ResumablePlaybackAndProgressTest (3 tests)
[INFO] Running com.udemy.catalog.CourseCompositeAndAuthorshipTest (5 tests)
[INFO] Running com.udemy.UdemyPlatformIntegrationTest (1 test)
[INFO] Running com.udemy.adapter.cdn.CdnStreamAdapterTest (3 tests)
[INFO] Running com.udemy.state.CourseStatePatternTest (3 tests)
[INFO] Running com.udemy.concurrency.ConcurrentPlaybackHeartbeatTest (1 test)
[INFO] Running com.udemy.review.CourseReviewAndRatingTest (3 tests)
[INFO] Running com.udemy.model.DomainEntityTest (6 tests)
[INFO] Running com.udemy.service.EnrollmentServiceTest (3 tests)
[INFO] Running com.udemy.builder.CourseBuilderTest (1 test)
[INFO] Running com.udemy.validation.chain.CoursePublishingValidationChainTest (5 tests)
[INFO] 
[INFO] Results:
[INFO] Tests run: 35, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS (1.122 s)
```
