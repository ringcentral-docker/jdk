import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

/** Exercise the runtime PKIX distrust check, including alternative certificate encodings. */
public class CertificateBlocklistTest {
    public static void main(String[] args) throws Exception {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        Collection<? extends Certificate> certificates;
        try (InputStream input = Files.newInputStream(Paths.get(args[0]))) {
            certificates = factory.generateCertificates(input);
        }
        Properties expected = new Properties();
        try (InputStream input = Files.newInputStream(Paths.get(args[1]))) {
            expected.load(input);
        }
        expected.remove("Algorithm");
        Set<String> seen = new HashSet<String>();
        int rejected = 0;
        for (Certificate certificate : certificates) {
            X509Certificate cert = (X509Certificate) certificate;
            String fingerprint = fingerprint(cert);
            if (!expected.containsKey(fingerprint) || !seen.add(fingerprint)) {
                throw new AssertionError("Unexpected or duplicate fixture: " + fingerprint);
            }
            try {
                validateAnchor(factory, cert);
            } catch (CertPathValidatorException failure) {
                if (!failure.getMessage().toLowerCase(Locale.ROOT).contains("untrusted")) {
                    throw failure;
                }
                rejected++;
            }
        }
        if (seen.size() != 48 || !seen.equals(expected.stringPropertyNames())) {
            throw new AssertionError("Must exercise all 48 compromised certificate encodings");
        }

        // The unaffected ISRG trust anchor must still validate through the same PKIX path.
        KeyStore cacerts = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream input = Files.newInputStream(Paths.get(System.getProperty("java.home"),
                "lib", "security", "cacerts"))) {
            cacerts.load(input, "changeit".toCharArray());
        }
        boolean controlFound = false;
        for (Enumeration<String> aliases = cacerts.aliases(); aliases.hasMoreElements();) {
            X509Certificate cert = (X509Certificate) cacerts.getCertificate(aliases.nextElement());
            if (cert.getSubjectX500Principal().getName().contains("CN=ISRG Root X1")) {
                validateAnchor(factory, cert);
                controlFound = true;
            }
        }
        if (!controlFound) {
            throw new AssertionError("ISRG Root X1 legitimate control is missing");
        }
        System.out.println("PKIX: " + rejected + "/48 compromised encodings rejected; ISRG Root X1 accepted");
        if (rejected != 48) {
            throw new AssertionError("Compromised certificates are still trusted");
        }
    }

    private static void validateAnchor(CertificateFactory factory, X509Certificate cert) throws Exception {
        CertPath path = factory.generateCertPath(Collections.<Certificate>emptyList());
        PKIXParameters parameters = new PKIXParameters(Collections.singleton(new TrustAnchor(cert, null)));
        parameters.setRevocationEnabled(false);
        CertPathValidator.getInstance("PKIX").validate(path, parameters);
    }

    private static String fingerprint(X509Certificate cert) throws Exception {
        StringBuilder result = new StringBuilder();
        for (byte value : MessageDigest.getInstance("SHA-256").digest(cert.getEncoded())) {
            result.append(String.format("%02X", value & 0xff));
        }
        return result.toString();
    }
}
