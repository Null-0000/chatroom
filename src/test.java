import javafx.beans.property.BooleanProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import uk.ac.ed.ph.snuggletex.SnuggleEngine;
import uk.ac.ed.ph.snuggletex.SnuggleInput;
import uk.ac.ed.ph.snuggletex.SnuggleSession;

import java.io.IOException;

public class test {
    public static void main(String[] args){
        SimpleMapProperty<String, String> map = new SimpleMapProperty<>(FXCollections.observableHashMap());
        map.putIfAbsent("A", "a");
        System.out.println(map.get("A"));



    }
}
