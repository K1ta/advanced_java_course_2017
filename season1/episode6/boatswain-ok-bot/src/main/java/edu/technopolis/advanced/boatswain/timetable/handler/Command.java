package edu.technopolis.advanced.boatswain.timetable.handler;

import edu.technopolis.advanced.boatswain.incoming.request.Message;

import java.util.Collection;
import java.util.StringTokenizer;

public abstract class Command {
    private Collection<String> commands;

    public Command(Collection<String> commands) {
        this.commands = commands;
    }

    public boolean equals(Message message) {
        StringTokenizer tk = new StringTokenizer(message.getText(), " ");
        String phrase = tk.nextToken();
        phrase = phrase.toLowerCase();
        for (String command : commands) {
            if (phrase.equals(command)) {
                return true;
            }
        }
        return false;
    }

    public abstract Message handleCommand(Message message, String institute, String group, String userID);
}
