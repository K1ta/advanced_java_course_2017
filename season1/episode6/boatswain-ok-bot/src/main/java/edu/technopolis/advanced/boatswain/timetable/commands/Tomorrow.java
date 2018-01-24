package edu.technopolis.advanced.boatswain.timetable.commands;

import edu.technopolis.advanced.boatswain.incoming.request.Message;
import edu.technopolis.advanced.boatswain.timetable.handler.Command;
import edu.technopolis.advanced.boatswain.timetable.timetable.DayData;
import edu.technopolis.advanced.boatswain.timetable.timetable.Timetable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;

public class Tomorrow extends Command {

    public Tomorrow(Collection<String> commands) {
        super(commands);
    }

    @Override
    public Message handleCommand(Message message, String institute, String group, String userID) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        String date = formatter.format(calendar.getTime());
        DayData data = Timetable.getDataByDate(institute, group, date);
        StringBuilder res = new StringBuilder();
        if (data.getLessons() == null || data.getLessons().isEmpty()) {
            res.append("На завтра ничего нет");
        } else {
            res.append(data.toString());
        }
        return new Message(res.toString());
    }
}
