import java.awt.*;

public class FontTest {

    public static void main(String[] args) {
        String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        System.out.println("Found " + names.length + " fonts:");

        for (String name : names) {
            System.out.println(name);
        }
    }
}