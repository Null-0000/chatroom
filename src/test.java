import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import uk.ac.ed.ph.snuggletex.SnuggleEngine;
import uk.ac.ed.ph.snuggletex.SnuggleInput;
import uk.ac.ed.ph.snuggletex.SnuggleSession;

import java.io.IOException;

public class test {
    public static void main(String[] args){
        Boolean b = false;
        BooleanProperty bp = new SimpleBooleanProperty(b);
        bp.addListener((e)->{
            System.out.println("changed");
        });
        b = true;
        bp.set(true);

    }
}
