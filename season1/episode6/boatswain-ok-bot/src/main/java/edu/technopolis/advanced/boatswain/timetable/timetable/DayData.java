package edu.technopolis.advanced.boatswain.timetable.timetable;

import edu.technopolis.advanced.boatswain.timetable.timetable.LessonData;

import java.util.ArrayList;
import java.util.Collection;

public class DayData {

    private String date;
    private ArrayList<LessonData> lessons;

    public DayData(String date, Collection<LessonData> lessons) {
        this.date = date;
        if (lessons != null) {
            this.lessons = new ArrayList<>();
            this.lessons.addAll(lessons);
        }
    }

    public String getDate() {
        return date;
    }

    public Collection<LessonData> getLessons() {
        return lessons;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        if (lessons == null) {
            return date;
        }
        res.append(date);
        for (LessonData lessonData : lessons) {
            res.append("\n");
            res.append(lessonData.toString());
        }
        return res.toString();
    }
}
