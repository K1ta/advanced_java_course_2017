package edu.technopolis.advanced.boatswain.timetable.handler;

import edu.technopolis.advanced.boatswain.incoming.request.Message;
import edu.technopolis.advanced.boatswain.timetable.commands.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class Handler {

    public static ArrayList<Command> commands = new ArrayList<>();

    public Handler() {
        commands.add(new SearchByDate(Arrays.asList("расписание", "р")));
        commands.add(new Today(Arrays.asList("сегодня", "с")));
        commands.add(new Tomorrow(Arrays.asList("завтра", "з")));
        commands.add(new WhenNext(Arrays.asList("когда", "к")));
        commands.add(new Help(Arrays.asList("help", "h")));
        commands.add(new Change(Arrays.asList("выбрать", "в")));
        commands.add(new PrintGroup(Arrays.asList("группа", "г")));
        commands.add(new PrintInstitutes(Arrays.asList("институты", "и")));
    }

    public Message handle(Message message, String userID) {
        for (Command command : commands) {
            if (command.equals(message)) {
                Properties props = new Properties();
                FileReader reader = null;
                FileWriter writer = null;
                try {
                    reader = new FileReader(new File("src/main/resources/client.properties"));
                    props.load(reader);
                    reader.close();
                    writer = new FileWriter(new File("src/main/resources/client.properties"));
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(30);
                }
                String institute = props.getProperty(userID + ".institute");
                String group = props.getProperty(userID + ".group");
                if (institute == null || group == null) {
                    props.setProperty(userID + ".institute", "iknit");
                    props.setProperty(userID + ".group", "33501/1");
                    institute = "iknit";
                    group = "33501/1";
                }
                try {
                    props.store(writer, null);
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(60);
                }
                return command.handleCommand(message, institute, group, userID);
            }
        }
        return new Message("Такой команды не существует\nВведите help или h для помощи");
    }
}
