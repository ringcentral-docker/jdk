# Parameterized JDK Dockerfile with variant support
#
# Build examples:
#   Standard:    docker build --build-arg BASE_IMAGE_TAG=21.0.12.1_1-jdk-noble --build-arg JAVA_VERSION=21 --build-arg JAVA_DETAIL_VERSION=21.0.12.1 .
#   With Docker: docker build --build-arg BASE_IMAGE_TAG=21.0.12.1_1-jdk-noble --build-arg JAVA_VERSION=21 --build-arg JAVA_DETAIL_VERSION=21.0.12.1 --build-arg VARIANT=docker .
#   With Jacoco: docker build --build-arg BASE_IMAGE_TAG=21.0.12.1_1-jdk-noble --build-arg JAVA_VERSION=21 --build-arg JAVA_DETAIL_VERSION=21.0.12.1 --build-arg VARIANT=jacoco .

# Default to JDK 21 LTS
ARG BASE_IMAGE_TAG=21.0.12.1_1-jdk-noble

# =============================================================================
# Builder Stage: Compile font test
# =============================================================================
FROM docker.io/library/eclipse-temurin:${BASE_IMAGE_TAG} AS builder

WORKDIR /workspace/app
COPY test/FontTest.java .
COPY scripts/UpdateBlockedCertificates.java test/CertificateBlocklistTest.java test/UpdateBlockedCertificatesTest.java ./
RUN javac FontTest.java UpdateBlockedCertificates.java CertificateBlocklistTest.java UpdateBlockedCertificatesTest.java

# =============================================================================
# Runtime Stage: Final JDK image
# =============================================================================
FROM docker.io/library/eclipse-temurin:${BASE_IMAGE_TAG}

ARG JAVA_VERSION=21
ARG JAVA_DETAIL_VERSION=21.0.12.1
ARG VARIANT=standard
ARG JMODS_RELEASE_TAG
ARG JMODS_ARCHIVE_VERSION
ARG JMODS_SHA256_AMD64
ARG JMODS_SHA256_ARM64

ENV JAVA_VERSION=${JAVA_VERSION} \
    JAVA_DETAIL_VERSION=${JAVA_DETAIL_VERSION} \
    JAVA_TOOL_OPTIONS="-XX:+IgnoreUnrecognizedVMOptions"

# Copy installation scripts
COPY scripts/ /tmp/scripts/
RUN chmod +x /tmp/scripts/*.sh

# Install base dependencies (always)
RUN /tmp/scripts/install-base.sh
RUN /tmp/scripts/install-jmods.sh

# Apply the upstream CVE-2026-91403 distrust to the effective runtime security file.
COPY assets/security/cve-2026-91403.properties test/fixtures/cve-2026-91403.pem /tmp/java-security/
COPY --from=builder /workspace/app/UpdateBlockedCertificates.class /workspace/app/CertificateBlocklistTest.class /workspace/app/UpdateBlockedCertificatesTest.class /tmp/java-security/
RUN java -cp /tmp/java-security UpdateBlockedCertificatesTest /tmp/java-security/cve-2026-91403.properties \
    && java -cp /tmp/java-security UpdateBlockedCertificates /tmp/java-security/cve-2026-91403.properties \
    && java -cp /tmp/java-security CertificateBlocklistTest /tmp/java-security/cve-2026-91403.pem /tmp/java-security/cve-2026-91403.properties \
    && if command -v jlink >/dev/null 2>&1; then \
         jlink --add-modules java.base,jdk.crypto.ec --output /tmp/java-security/linked \
         && /tmp/java-security/linked/bin/java -cp /tmp/java-security CertificateBlocklistTest /tmp/java-security/cve-2026-91403.pem /tmp/java-security/cve-2026-91403.properties; \
       fi \
    && rm -rf /tmp/java-security

# Cleanup installation scripts
RUN rm -rf /tmp/scripts

# Verify font configuration
COPY --from=builder /workspace/app/FontTest.class .
RUN java FontTest && rm -f FontTest.class

# Display version info
RUN echo "=== Java Version ===" && java -version && javac -version
