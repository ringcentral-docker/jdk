import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class UpdateBlockedCertificatesTest {
    public static void main(String[] args) throws Exception {
        Path target = Files.createTempFile("blocked-certs-test", ".properties");
        Path supplement = Paths.get(args[0]);
        try {
            // Keep custom entries, comments and a trailing Properties continuation intact.
            byte[] original = ("# local policy\nAlgorithm=SHA-256\n"
                    + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n"
                    + "custom=value\\").getBytes(StandardCharsets.US_ASCII);
            Files.write(target, original);
            UpdateBlockedCertificates.merge(target, supplement);
            byte[] merged = Files.readAllBytes(target);
            if (!Arrays.equals(original, Arrays.copyOf(merged, original.length))) {
                throw new AssertionError("Existing blocklist bytes changed");
            }
            UpdateBlockedCertificates.merge(target, supplement);
            if (!Arrays.equals(merged, Files.readAllBytes(target))) {
                throw new AssertionError("Blocklist update is not idempotent");
            }
            for (String invalid : new String[] {"Algorithm=SHA-1\n", "# missing algorithm\n",
                    "Algorithm=SHA-256\nAlgorithm=SHA-1\n"}) {
                byte[] content = invalid.getBytes(StandardCharsets.US_ASCII);
                Files.write(target, content);
                try {
                    UpdateBlockedCertificates.merge(target, supplement);
                    throw new AssertionError("Unsafe hash algorithm accepted");
                } catch (IllegalArgumentException expected) {
                    if (!Arrays.equals(content, Files.readAllBytes(target))) {
                        throw new AssertionError("Invalid blocklist was modified");
                    }
                }
            }
            System.out.println("Blocklist merge: preservation, idempotence and invalid algorithms passed");
        } finally {
            Files.deleteIfExists(target);
        }
    }
}
