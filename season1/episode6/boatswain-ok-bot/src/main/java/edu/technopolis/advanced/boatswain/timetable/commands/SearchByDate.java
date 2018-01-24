package edu.technopolis.advanced.boatswain.timetable.commands;

import edu.technopolis.advanced.boatswain.incoming.request.Message;
import edu.technopolis.advanced.boatswain.timetable.handler.Command;
import edu.technopolis.advanced.boatswain.timetable.timetable.DayData;
import edu.technopolis.advanced.boatswain.timetable.timetable.Timetable;

import java.util.Collection;
import java.util.StringTokenizer;

public class SearchByDate extends Command {

    public SearchByDate(Collection<String> commands) {
        super(commands);
    }

    @Override
    public Message handleCommand(Message message, String institute, String group, String userID) {
        StringTokenizer tk = new StringTokenizer(message.getText(), ". ");
        if (tk.countTokens() != 4) {
            return new Message("Ошибочные данные");
        }
        tk.nextToken();
        String day = tk.nextToken();
        String month = tk.nextToken();
        String year = tk.nextToken();
        try {
            int dayInt = Integer.parseInt(day);
            if (dayInt > 31 || dayInt < 1) {
                return new Message("Ошибочные данные");
            }
            int monthInt = Integer.parseInt(month);
            if (monthInt < 1 || monthInt > 12) {
                return new Message("Ошибочные данные");
            }
            int yearInt = Integer.parseInt(year);
            if (yearInt < 1000 || yearInt > 9999) {
                return new Message("Ошибочные данные");
            }
        } catch (NumberFormatException e) {
            return new Message("Ошибочные данные");
        }
        DayData data = Timetable.getDataByDate(institute, group, year + "-" + month + "-" + day);
        return new Message(data.toString());
    }
}
