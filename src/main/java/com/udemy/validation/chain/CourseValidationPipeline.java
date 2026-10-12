package com.udemy.validation.chain;

import com.udemy.model.Course;

public class CourseValidationPipeline {
    private final CoursePublishingValidator headValidator;

    public CourseValidationPipeline() {
        // Construct default Chain of Responsibility: Instructor -> Curriculum -> Video
        InstructorValidator instructorValidator = new InstructorValidator();
        CurriculumCompletenessValidator curriculumValidator = new CurriculumCompletenessValidator();
        VideoQualityValidator videoValidator = new VideoQualityValidator();

        instructorValidator.setNext(curriculumValidator);
        curriculumValidator.setNext(videoValidator);

        this.headValidator = instructorValidator;
    }

    public CourseValidationPipeline(CoursePublishingValidator customHead) {
        this.headValidator = customHead;
    }

    public void validate(Course course) throws CourseValidationException {
        if (headValidator != null) {
            headValidator.validate(course);
        }
    }
}
