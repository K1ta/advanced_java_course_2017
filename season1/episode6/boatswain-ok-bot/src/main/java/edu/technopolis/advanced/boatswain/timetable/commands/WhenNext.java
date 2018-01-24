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

public class WhenNext extends Command {

    public WhenNext(Collection<String> commands) {
        super(commands);
    }

    @Override
    public Message handleCommand(Message message, String institute, String group, String userID) {
        StringTokenizer tk = new StringTokenizer(message.getText(), " ");
        if (tk.countTokens() < 2) {
            return new Message("Ошибочный формат");
        }
        tk.nextToken();
        String subject = "";
        while (tk.hasMoreTokens()) {
            subject += tk.nextToken();
            if (tk.hasMoreTokens()) {
                subject += " ";
            }
        }
        subject = subject.toLowerCase();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
            String date = formatter.format(calendar.getTime());
            DayData data = Timetable.getDataByDate(institute, group, date);
            if (data.getLessons() != null && !data.getLessons().isEmpty()) {
                for (LessonData lesson : data.getLessons()) {
                    if (lesson.getLesson().toLowerCase().contains(subject)) {
                        res.append("Следующее занятие по ");
                        res.append(lesson.getLesson());
                        res.append(" будет ");
                        res.append(data.getDate());
                        res.append(" в ");
                        res.append(lesson.getTime());
                        return new Message(res.toString());
                    }
                }
            }
        }
        return new Message("На ближайшие 10 дней такого предмета не найдено");
    }
}
