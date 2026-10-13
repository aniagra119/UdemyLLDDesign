package com.udemy.builder;

import com.udemy.adapter.cdn.CdnStreamAdapter;
import com.udemy.enums.AuthorshipRole;
import com.udemy.model.Course;
import com.udemy.model.CourseAuthorship;
import com.udemy.model.Instructor;
import com.udemy.model.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class CourseBuilder {
    private final String title;
    private final String description;
    private final double basePrice;
    private final List<CourseAuthorship> authorships = new ArrayList<>();
    private final List<Module> modules = new ArrayList<>();
    private CdnStreamAdapter cdnAdapter;
    private int currentModuleIndex = 1;

    private CourseBuilder(String title, String description, double basePrice) {
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.description = Objects.requireNonNull(description, "description must not be null");
        this.basePrice = basePrice;
    }

    public static CourseBuilder create(String title, String description, double basePrice) {
        return new CourseBuilder(title, description, basePrice);
    }

    public CourseBuilder withLeadInstructor(Instructor instructor, double revenueSharePercentage) {
        authorships.add(new CourseAuthorship("temp", instructor, AuthorshipRole.LEAD_INSTRUCTOR, revenueSharePercentage));
        return this;
    }

    public CourseBuilder withCoInstructor(Instructor instructor, double revenueSharePercentage) {
        authorships.add(new CourseAuthorship("temp", instructor, AuthorshipRole.CO_INSTRUCTOR, revenueSharePercentage));
        return this;
    }

    public CourseBuilder withCdnAdapter(CdnStreamAdapter cdnAdapter) {
        this.cdnAdapter = cdnAdapter;
        return this;
    }

    public CourseBuilder addModule(String moduleTitle, Consumer<ModuleBuilder> moduleConfigurator) {
        ModuleBuilder mb = new ModuleBuilder(moduleTitle, currentModuleIndex++);
        if (moduleConfigurator != null) {
            moduleConfigurator.accept(mb);
        }
        modules.add(mb.build());
        return this;
    }

    public Course build() {
        Course course = new Course(title, description, basePrice);
        if (cdnAdapter != null) {
            course.setCdnAdapter(cdnAdapter);
        }
        for (CourseAuthorship a : authorships) {
            course.addAuthorship(new CourseAuthorship(course.getId(), a.getInstructor(), a.getRole(), a.getRevenueSharePercentage()));
        }
        for (Module m : modules) {
            course.addModule(m);
        }
        return course;
    }
}
