package edu.technopolis.advanced.boatswain.timetable.commands;

import edu.technopolis.advanced.boatswain.BoatswainBot;
import edu.technopolis.advanced.boatswain.incoming.request.Message;
import edu.technopolis.advanced.boatswain.timetable.handler.Command;
import edu.technopolis.advanced.boatswain.timetable.handler.Handler;
import edu.technopolis.advanced.boatswain.timetable.timetable.Timetable;

import java.io.*;
import java.util.Collection;
import java.util.Properties;
import java.util.StringTokenizer;

public class Change extends Command {

    public Change(Collection<String> commands) {
        super(commands);
    }

    @Override
    public Message handleCommand(Message message, String institute, String group, String userID) {
        StringTokenizer tk = new StringTokenizer(message.getText(), " ");
        if (tk.countTokens() != 3) {
            return new Message("Ошибочные данные");
        }
        tk.nextToken();
        String newInstitute = tk.nextToken();
        String newGroup = tk.nextToken();
        newInstitute = verifyData(newInstitute.toLowerCase(), newGroup);
        if (newInstitute.equals("error_inst")) {
            return new Message("Такой институт не найден");
        }
        if (newInstitute.equals("error_gr")) {
            return new Message("Такая группа не найдена");
        }
        Properties props = new Properties();
        try {
            FileReader reader = new FileReader(new File("src/main/resources/client.properties"));
            props.load(reader);
            reader.close();
            FileWriter writer = new FileWriter(new File("src/main/resources/client.properties"));
            props.setProperty(userID + ".institute", newInstitute);
            props.setProperty(userID + ".group", newGroup);
            props.store(writer, null);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(40);
        }
        return new Message("Группа и институт изменены");
    }

    private String verifyData(String institute, String group) {
        String res;
        switch (institute) {
            case "ифнит":
                res = "ifnit";
                break;
            case "всмоп":
                res = "vsmop";
                break;
            case "иеитс":
                res = "ieits";
                break;
            case "иси":
                res = "isi";
                break;
            case "всбипт":
                res = "vsbipt";
                break;
            case "встб":
                res = "vstb";
                break;
            case "ги":
                res = "gi";
                break;
            case "иппт":
                res = "ippt";
                break;
            case "икнит":
                res = "iknit";
                break;
            case "иммит":
                res = "immit";
                break;
            case "ипмим":
                res = "ipmim";
                break;
            case "идо":
                res = "ido";
                break;
            case "ипмеит":
                res = "ipmeit";
                break;
            default:
                return "error_inst";
        }
        if (Timetable.checkGroup(res, group)) {
            return res;
        } else {
            return "error_gr";
        }
    }
}
