package edu.technopolis.advanced.boatswain.timetable.timetable;

import java.util.StringTokenizer;

public class LessonData {
    private String lessonSubject;
    private String type;
    private String place;
    private String teacher;

    public void setLessonSubject(String lessonSubject) {
        if (lessonSubject.isEmpty()) {
            this.lessonSubject = "Нет данных";
        } else {
            this.lessonSubject = lessonSubject;
        }
    }

    public void setType(String type) {
        if (type.isEmpty()) {
            this.type = "Нет данных";
        } else {
            this.type = type;
        }
    }

    public void setPlace(String place) {
        if (place.isEmpty()) {
            this.place = "Нет данных";
        } else {
            this.place = place;
        }
    }

    public void setTeacher(String teacher) {
        if (teacher.isEmpty()) {
            this.teacher = "Нет данных";
        } else {
            this.teacher = teacher;
        }
    }

    public String getTime() {
        return lessonSubject.substring(0, 5);
    }

    public String getLesson() {
        String res = "";
        StringTokenizer tk = new StringTokenizer(lessonSubject, " ");
        tk.nextToken();
        while (tk.hasMoreTokens()) {
            res += tk.nextToken() + " ";
        }
        return res;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append(lessonSubject);
        res.append("\nТип занятия: ");
        res.append(type);
        res.append("\nПреподаватель: ");
        res.append(teacher);
        res.append("\nАудитория: ");
        res.append(place);
        return res.toString();
    }

}
