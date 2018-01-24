package edu.technopolis.advanced.boatswain.timetable.commands;

import edu.technopolis.advanced.boatswain.incoming.request.Message;
import edu.technopolis.advanced.boatswain.timetable.handler.Command;
import edu.technopolis.advanced.boatswain.timetable.timetable.DayData;
import edu.technopolis.advanced.boatswain.timetable.timetable.LessonData;
import edu.technopolis.advanced.boatswain.timetable.timetable.Timetable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.StringTokenizer;

public class Today extends Command {

    public Today(Collection<String> commands) {
        super(commands);
    }

    @Override
    public Message handleCommand(Message message, String institute, String group, String userID) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm");
        Calendar calendar = Calendar.getInstance();
        String dateString = formatter.format(calendar.getTime());
        StringTokenizer tk = new StringTokenizer(dateString, " ");
        String date = tk.nextToken();
        String time = tk.nextToken();
        DayData data = Timetable.getDataByDate(institute, group, date);
        StringBuilder res = new StringBuilder();
        if (data.getLessons() == null || data.getLessons().isEmpty()) {
            res.append("На сегодня ничего нет");
        } else {
            res.append(data.getDate());
            res.append("\nОсталось:\n");
            boolean added = false;
            for (LessonData lesson : data.getLessons()) {
                if (after(lesson.getTime(), time)) {
                    res.append(lesson.toString());
                    res.append("\n");
                    added = true;
                }
            }
            if (!added) {
                res.append("Все пары закончились");
            }
        }
        return new Message(res.toString());
    }

    private boolean after(String lessonTime, String curTime) {
        int curHours = Integer.parseInt(curTime.substring(0, 2));
        int lessonHours = Integer.parseInt(lessonTime.substring(0, 2));
        int curMinutes = Integer.parseInt(curTime.substring(3, 5));
        int lessonMinutes = Integer.parseInt(lessonTime.substring(3, 5));
        if (lessonHours <= curHours) {
            if (lessonMinutes < curMinutes) {
                return false;
            }
        }
        return true;
    }
}
