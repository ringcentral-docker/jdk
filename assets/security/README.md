# CVE-2026-91403 certificate distrust

The Temurin base versions in `versions/versions.json` still require the
[official certificate-blocklist mitigation](https://ops.java/emudhra).
Updating OS CA packages alone does not update the JDK blocklist.

`cve-2026-91403.properties` contains the 48 new SHA-256 fingerprints from
[OpenJDK commit 22bc7357ce6bc5f1ed2f1e83e0544b2c25f781d4](https://github.com/openjdk/jdk/commit/22bc7357ce6bc5f1ed2f1e83e0544b2c25f781d4).
They cover all 16 PEM certificates added by that commit, including the
alternative AlgorithmIdentifier and ECDSA signature encodings emitted by its
fixed `BlockedCertsConverter`. The supplement SHA-256 is
`e16ed9c4ea8a140402ceaf15a859d66c0a66952024fc25740573acff6bf851fb`.

The source `blocked.certs.pem` SHA-256 is
`23daaddff18ed3800bf38937b51cd91045777719baa19437d812afeb9b44b64e`.
Generating the full upstream list produces 86 fingerprints (SHA-256
`2596bb6dda83c66d96c5a252d7b2ae1f24cc1ae3f1890223b4f3591a8d52bf10`).
Only the 48 newly added fingerprints are appended, preserving the base image's
existing blocklist and any custom entries.

During every image build, `UpdateBlockedCertificates` locates the security file
under the running JVM's `java.home`: `blacklisted.certs` for Java 8 and
`blocked.certs` for newer releases. It also updates the blocklist inside
`jmods/java.base.jmod` so runtimes produced by `jlink` inherit the same distrust.
Temurin 25 omits JMODs; its linkable runtime rejects a modified `blocked.certs`.
The build restores matching official Temurin JMODs with architecture-specific
SHA-256 checksums pinned in `versions/versions.json`, then updates that copy too.
This preserves ordinary `jlink` behavior and its runtime integrity checks.
It fails the build if the existing file does
not use SHA-256. Tests check preservation and idempotence, then exercise PKIX
validation against all 48 encodings in `test/fixtures/cve-2026-91403.pem` and an
unaffected ISRG Root X1 trust anchor. On Java 11+, the build repeats the PKIX test
using a freshly generated `jlink` runtime. No network revocation service is required.

The fixture contains public CA certificates, never private keys. It is used only
for build-time validation and is removed from the final image along with the
test classes.
