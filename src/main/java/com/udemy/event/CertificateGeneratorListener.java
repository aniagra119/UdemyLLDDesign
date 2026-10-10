package com.udemy.event;

import com.udemy.progress.Enrollment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CertificateGeneratorListener implements CourseEventListener {
    private final List<String> generatedCertificates = new ArrayList<>();

    @Override
    public synchronized void onCourseCompleted(Enrollment enrollment) {
        String certId = "CERT-" + enrollment.getCourse().getId() + "-" + enrollment.getStudent().getId();
        generatedCertificates.add(certId);
    }

    public synchronized List<String> getGeneratedCertificates() {
        return Collections.unmodifiableList(generatedCertificates);
    }

    public synchronized boolean hasCertificate(String studentId, String courseId) {
        String expected = "CERT-" + courseId + "-" + studentId;
        return generatedCertificates.contains(expected);
    }
}
