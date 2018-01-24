package edu.technopolis.advanced.boatswain.timetable.timetable;

import edu.technopolis.advanced.boatswain.BoatswainBot;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

public class Timetable {

    private static Properties props = new Properties();

    static {
        try {
            props.load(BoatswainBot.class.getResourceAsStream("/timetable.properties"));
        } catch (IOException e) {
            System.exit(10);
        }
    }


    public static DayData getDataByDate(String institute, String group, String date) {
        String lessonsDate = "";
        ArrayList<LessonData> lessonsInf = new ArrayList<>();
        String schema = props.getProperty("schema");
        String host = props.getProperty("host");
        String instituteUrl = props.getProperty(institute + "Groups");
        if (instituteUrl == null) {
            return new DayData("Нет данных", null);
        }
        try {
            //connect to page with list of groups
            Document groupList = Jsoup.connect(schema + "://" + host + instituteUrl).get();
            //find requested group
            Element groupItem = groupList.selectFirst(".groups-list__item:contains(" + group + ")");
            if (groupItem == null) {
                return new DayData("Такой группы не существует", null);
            }
            String groupLink = groupItem.select("a[href]").attr("href");
            //connect to page with timetable for a week
            Document doc = Jsoup.connect(schema + "://" + host + groupLink + "?date=" + date).get();
            //select struct with requested date
            StringTokenizer tk = new StringTokenizer(date, "-");
            if(tk.countTokens() != 3) {
                return new DayData("Ошибка в дате", null);
            }
            tk.nextToken();
            tk.nextToken();
            String temp = tk.nextToken();
            Element lessonDay = doc.selectFirst(".schedule__day:matches(" + temp + "\\s.+\\.\\,)");
            if (lessonDay == null) {
                return new DayData("Занятий нет", null);
            }
            //date of lesson
            lessonsDate = lessonDay.select(".schedule__date").text();
            Elements lessons = lessonDay.select(".lesson");
            for (Element lesson : lessons) {
                LessonData curLesson = new LessonData();
                curLesson.setLessonSubject(lesson.select(".lesson__subject").text());
                curLesson.setType(lesson.select(".lesson__type").text());
                curLesson.setTeacher(lesson.select(".lesson__teachers").text());
                curLesson.setPlace(lesson.select(".lesson__places").text());
                lessonsInf.add(curLesson);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new DayData("Нет данных", null);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return new DayData("Нет данных", null);
        }
        return new DayData(lessonsDate, lessonsInf);
    }

    public static boolean checkGroup(String institute, String group) {
        String schema = props.getProperty("schema");
        String host = props.getProperty("host");
        String instituteUrl = props.getProperty(institute + "Groups");
        if (instituteUrl == null) {
            return false;
        }
        Document groupList = null;
        try {
            groupList = Jsoup.connect(schema + "://" + host + instituteUrl).get();
        } catch (IOException e) {
            return false;
        }
        Element groupItem = groupList.selectFirst(".groups-list__item:matches(^" + group + "$)");
        if (groupItem == null) {
            return false;
        }
        return true;
    }

}
