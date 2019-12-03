package client.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexFunctions {
    public static String selectBy(String message, String by) {
        Pattern p = Pattern.compile(by);
        Matcher m = p.matcher(message);
        if (m.find()) return m.group(1);
        return null;
    }
}
