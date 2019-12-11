import uk.ac.ed.ph.snuggletex.SnuggleEngine;
import uk.ac.ed.ph.snuggletex.SnuggleInput;
import uk.ac.ed.ph.snuggletex.SnuggleSession;

import java.io.IOException;

public class test {
    public static void main(String[] args) throws IOException {
        String s = "$$\\frac{1}{2}$$";
        SnuggleEngine engine = new SnuggleEngine();
        SnuggleSession session = engine.createSession();
        SnuggleInput input = new SnuggleInput(s);
        session.parseInput(input);
        System.out.println(session.buildXMLString());
    }
}
