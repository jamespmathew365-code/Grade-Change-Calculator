package com.example.gradecalc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@SpringBootApplication
@RestController
@CrossOrigin
public class GradeCalcApplication {

    public static void main(String[] args) {
        SpringApplication.run(GradeCalcApplication.class, args);
    }

    // ---------- DATA MODELS ----------

    static class Assignment {
        public double earned;
        public double possible;
    }

    static class Section {
        public String name;
        public Double weight; // null if unknown
        public List<Assignment> assignments = new ArrayList<>();

        public double average() {
            double earned = 0, possible = 0;
            for (Assignment a : assignments) {
                earned += a.earned;
                possible += a.possible;
            }
            return possible == 0 ? 0 : earned / possible;
        }
    }

    static class Course {
        public List<Section> sections = new ArrayList<>();

        public double overallGrade() {
            double total = 0;
            for (Section s : sections) {
                if (s.weight != null) {
                    total += s.average() * s.weight;
                }
            }
            return total;
        }
    }

    static class SimulationRequest {
        public Course course;
        public String sectionName;
        public double newEarned;
        public double newPossible;
    }

    // ---------- API ----------

    @PostMapping("/api/simulate")
    public Map<String, Object> simulate(@RequestBody SimulationRequest req) {

        double oldGrade = req.course.overallGrade();

        for (Section s : req.course.sections) {
            if (s.name.equals(req.sectionName)) {
                Assignment a = new Assignment();
                a.earned = req.newEarned;
                a.possible = req.newPossible;
                s.assignments.add(a);
                break;
            }
        }

        double newGrade = req.course.overallGrade();

        return Map.of(
                "oldGrade", oldGrade,
                "newGrade", newGrade,
                "change", newGrade - oldGrade
        );
    }
}
