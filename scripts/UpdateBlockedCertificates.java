import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/** Apply the OpenJDK CVE-2026-91403 certificate distrust without losing local entries. */
public class UpdateBlockedCertificates {
    public static void main(String[] args) throws Exception {
        String filename = "1.8".equals(System.getProperty("java.specification.version"))
                ? "blacklisted.certs" : "blocked.certs";
        Path target = Paths.get(System.getProperty("java.home"), "lib", "security", filename);
        merge(target, Paths.get(args[0]));
        System.out.println("Updated certificate blocklist: " + target);
        if (!"1.8".equals(System.getProperty("java.specification.version"))) {
            updateJmod(Paths.get(System.getProperty("java.home"), "jmods", "java.base.jmod"),
                    Paths.get(args[0]));
        }
    }

    static void merge(Path target, Path supplement) throws Exception {
        byte[] original = Files.readAllBytes(target);
        byte[] merged = mergedContent(original, supplement);
        if (!Arrays.equals(original, merged)) {
            Files.write(target, merged);
        }
    }

    private static byte[] mergedContent(byte[] original, Path supplement) throws Exception {
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
            return original;
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
        return merged;
    }

    // jlink uses this second copy rather than the running JVM's security file.
    private static void updateJmod(Path jmod, Path supplement) throws Exception {
        byte[] magic = new byte[4];
        try (InputStream input = Files.newInputStream(jmod)) {
            for (int i = 0; i < magic.length; i++) {
                int value = input.read();
                if (value < 0) {
                    throw new IllegalStateException("Truncated JMOD header");
                }
                magic[i] = (byte) value;
            }
        }
        if (!Arrays.equals(magic, new byte[] {'J', 'M', 1, 0})) {
            throw new IllegalStateException("Unsupported JMOD format");
        }
        Path temporary = Files.createTempFile(jmod.getParent(), "java.base-", ".jmod.tmp");
        try {
            try (ZipFile source = new ZipFile(jmod.toFile());
                    OutputStream raw = Files.newOutputStream(temporary)) {
                ZipEntry security = source.getEntry("lib/security/blocked.certs");
                if (security == null) {
                    throw new IllegalStateException("JMOD certificate blocklist is missing");
                }
                ByteArrayOutputStream content = new ByteArrayOutputStream();
                try (InputStream input = source.getInputStream(security)) {
                    copy(input, content);
                }
                byte[] merged = mergedContent(content.toByteArray(), supplement);
                raw.write(magic);
                try (ZipOutputStream output = new ZipOutputStream(raw)) {
                    for (Enumeration<? extends ZipEntry> entries = source.entries(); entries.hasMoreElements();) {
                        ZipEntry entry = entries.nextElement();
                        ZipEntry replacement = new ZipEntry(entry.getName());
                        replacement.setTime(entry.getTime());
                        output.putNextEntry(replacement);
                        if (entry.getName().equals(security.getName())) {
                            output.write(merged);
                        } else {
                            try (InputStream input = source.getInputStream(entry)) {
                                copy(input, output);
                            }
                        }
                        output.closeEntry();
                    }
                }
            }
            Files.setPosixFilePermissions(temporary, Files.getPosixFilePermissions(jmod));
            Files.move(temporary, jmod, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Updated jlink certificate blocklist: " + jmod);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private static void copy(InputStream input, OutputStream output) throws Exception {
        byte[] buffer = new byte[8192];
        int count;
        while ((count = input.read(buffer)) != -1) {
            output.write(buffer, 0, count);
        }
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
