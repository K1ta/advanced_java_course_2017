package edu.technopolis.advanced.boatswain.timetable.commands;

import edu.technopolis.advanced.boatswain.incoming.request.Message;
import edu.technopolis.advanced.boatswain.timetable.handler.Command;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

public class PrintGroup extends Command {

    public PrintGroup(Collection<String> commands) {
        super(commands);
    }

    @Override
    public Message handleCommand(Message message, String institute, String group, String userID) {
        StringBuilder res = new StringBuilder();
        Properties props = new Properties();
        FileReader reader = null;
        try {
            reader = new FileReader(new File("src/main/resources/client.properties"));
            props.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(100);
        }
        res.append("Текущие институт и группа: ");
        res.append(translateName(props.getProperty(userID + ".institute")));
        res.append(" ");
        res.append(props.getProperty(userID + ".group"));
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(100);
        }
        return new Message(res.toString());
    }

    private String translateName(String s) {
        switch (s) {
            case "ifnit":
                return "ИФНиТ";
            case "vsmop":
                return "ВСМОП";
            case "ieits":
                return "ИЕиТС";
            case "isi":
                return "ИСИ";
            case "vsbipt":
                return "ВСБиПТ";
            case "vstb":
                return "ВШТБ";
            case "gi":
                return "ГИ";
            case "ippt":
                return "ИППТ";
            case "iknit":
                return "ИКНиТ";
            case "immit":
                return "ИММиТ";
            case "ipmim":
                return "ИПМиМ";
            case "ido":
                return "ИДО";
            case "ipmeit":
                return "ИПМЭиТ";
        }
        return "error";
    }
}
