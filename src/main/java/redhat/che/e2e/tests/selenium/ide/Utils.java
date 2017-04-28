package redhat.che.e2e.tests.selenium.ide;

public class Utils {

    /**
     * Constructs a path from visible texts of items in project explorer.
     * @param path path consisting of texts of items
     * @return path usable in searching via xpath using path attribute
     */
    public static String constructPath(String... path) {
        StringBuilder sb = new StringBuilder();
        for (String text: path) {
            sb.append("/");
            sb.append(text);
        }
        return sb.toString();
    }
}
