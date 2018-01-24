package edu.technopolis.advanced.boatswain.timetable.commands;

import edu.technopolis.advanced.boatswain.incoming.request.Message;
import edu.technopolis.advanced.boatswain.timetable.handler.Command;

import java.util.Collection;

public class PrintInstitutes extends Command {

    public PrintInstitutes(Collection<String> commands) {
        super(commands);
    }

    @Override
    public Message handleCommand(Message message, String institute, String group, String userID) {
        StringBuilder res = new StringBuilder();
        res.append("Список доступных институтов:\n");
        res.append("ИФНиТ\n");
        res.append("ВШМОП\n");
        res.append("ИЕиТС\n");
        res.append("ИСИ\n");
        res.append("ВСБиПТ\n");
        res.append("ВШТБ\n");
        res.append("ГИ\n");
        res.append("ИППТ\n");
        res.append("ИКНиТ\n");
        res.append("ИММиТ\n");
        res.append("ИПМиМ\n");
        res.append("ИДО\n");
        res.append("ИПМЭиТ");
        return new Message(res.toString());
    }
}
