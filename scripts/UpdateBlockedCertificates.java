import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.TreeSet;

/** Apply the OpenJDK CVE-2026-91403 certificate distrust without losing local entries. */
public class UpdateBlockedCertificates {
    public static void main(String[] args) throws Exception {
        String filename = "1.8".equals(System.getProperty("java.specification.version"))
                ? "blacklisted.certs" : "blocked.certs";
        Path target = Paths.get(System.getProperty("java.home"), "lib", "security", filename);
        merge(target, Paths.get(args[0]));
        System.out.println("Updated certificate blocklist: " + target);
    }

    static void merge(Path target, Path supplement) throws Exception {
        byte[] original = Files.readAllBytes(target);
        Properties existing = load(original);
        Properties additions = load(Files.readAllBytes(supplement));
        requireSha256(existing);
        requireSha256(additions);
        if (additions.size() != 49) {
            throw new IllegalArgumentException("Expected Algorithm and 48 certificate fingerprints");
        }
        TreeSet<String> missing = new TreeSet<String>();
        for (String key : additions.stringPropertyNames()) {
            if ("Algorithm".equals(key)) {
                continue;
            }
            if (!key.matches("[0-9A-F]{64}") || !additions.getProperty(key).isEmpty()) {
                throw new IllegalArgumentException("Invalid supplemental fingerprint: " + key);
            }
            if (!existing.containsKey(key)) {
                missing.add(key);
            }
        }
        if (missing.isEmpty()) {
            return;
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(original);
        // Two newlines also terminate a possible trailing Properties continuation.
        output.write("\n\n# CVE-2026-91403: compromised eMudhra intermediates\n"
                .getBytes(StandardCharsets.US_ASCII));
        for (String fingerprint : missing) {
            output.write((fingerprint + "\n").getBytes(StandardCharsets.US_ASCII));
        }
        byte[] merged = output.toByteArray();
        Properties result = load(merged);
        for (String key : existing.stringPropertyNames()) {
            if (!existing.getProperty(key).equals(result.getProperty(key))) {
                throw new IllegalStateException("Existing blocklist entry changed: " + key);
            }
        }
        for (String key : additions.stringPropertyNames()) {
            if (!result.containsKey(key)) {
                throw new IllegalStateException("Missing blocklist entry: " + key);
            }
        }
        Files.write(target, merged);
    }

    private static Properties load(byte[] content) throws Exception {
        Properties properties = new Properties();
        try (InputStream input = new ByteArrayInputStream(content)) {
            properties.load(input);
        }
        return properties;
    }

    private static void requireSha256(Properties properties) {
        if (!"SHA-256".equalsIgnoreCase(properties.getProperty("Algorithm"))) {
            throw new IllegalArgumentException("Certificate blocklist must use SHA-256");
        }
    }
}
