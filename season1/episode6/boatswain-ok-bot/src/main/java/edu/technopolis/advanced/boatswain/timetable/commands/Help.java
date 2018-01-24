package edu.technopolis.advanced.boatswain.timetable.commands;

import edu.technopolis.advanced.boatswain.incoming.request.Message;
import edu.technopolis.advanced.boatswain.timetable.handler.Command;

import java.util.Collection;

public class Help extends Command {

    public Help(Collection<String> commands) {
        super(commands);
    }

    @Override
    public Message handleCommand(Message message, String institute, String group, String userID) {
        StringBuilder res = new StringBuilder();
        res.append("Бот для вывода расписания занятий политеха\n");
        res.append("Команды:\n");
        res.append("расписание/р дд.мм.гггг - вывод расписания на определенную дату\n");
        res.append("завтра/з - вывод расписания на завтрашний день\n");
        res.append("сегодня/с - вывод оставшихся пар на сегодняшний день с учетом времени\n");
        res.append("когда/к [название предмета] - выводит ближайшее занятие по предмету в следующие 10 дней\n");
        res.append("выбрать/в [аббревиатура института] [номер группы] - меняет текующую группу и институт\n");
        res.append("группа/г - выводит текущую институт и группу\n");
        res.append("инстиуты/и - выводит список доступных институтов\n");
        res.append("Все команды и слова нечувствительны к регистру\n");
        res.append("Название предмета можно вводить не полностью, однако грамматику надо сохранять.\n");
        res.append("По умолчанию расписание выводится для группы ИКНиТ 33501/1");
        return new Message(res.toString());
    }
}
