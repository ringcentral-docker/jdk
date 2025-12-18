# Parameterized JDK Dockerfile with variant support
#
# Build examples:
#   Standard:    docker build --build-arg BASE_IMAGE_TAG=21.0.9_10-jdk-noble --build-arg JAVA_VERSION=21 --build-arg JAVA_DETAIL_VERSION=21.0.9 .
#   With Docker: docker build --build-arg BASE_IMAGE_TAG=21.0.9_10-jdk-noble --build-arg JAVA_VERSION=21 --build-arg JAVA_DETAIL_VERSION=21.0.9 --build-arg VARIANT=docker .
#   With Jacoco: docker build --build-arg BASE_IMAGE_TAG=21.0.9_10-jdk-noble --build-arg JAVA_VERSION=21 --build-arg JAVA_DETAIL_VERSION=21.0.9 --build-arg VARIANT=jacoco .

# Default to JDK 21 LTS
ARG BASE_IMAGE_TAG=21.0.9_10-jdk-noble

# =============================================================================
# Builder Stage: Compile font test
# =============================================================================
FROM public.ecr.aws/docker/library/eclipse-temurin:${BASE_IMAGE_TAG} AS builder

WORKDIR /workspace/app
COPY test/FontTest.java .
RUN javac FontTest.java

# =============================================================================
# Runtime Stage: Final JDK image
# =============================================================================
FROM public.ecr.aws/docker/library/eclipse-temurin:${BASE_IMAGE_TAG}

ARG JAVA_VERSION=21
ARG JAVA_DETAIL_VERSION=21.0.9
ARG VARIANT=standard

ENV JAVA_VERSION=${JAVA_VERSION} \
    JAVA_DETAIL_VERSION=${JAVA_DETAIL_VERSION} \
    JAVA_TOOL_OPTIONS="-XX:+IgnoreUnrecognizedVMOptions"

# Copy installation scripts
COPY scripts/ /tmp/scripts/
RUN chmod +x /tmp/scripts/*.sh

# Install base dependencies (always)
RUN /tmp/scripts/install-base.sh

# Cleanup installation scripts
RUN rm -rf /tmp/scripts

# Verify font configuration
COPY --from=builder /workspace/app/FontTest.class .
RUN java FontTest && rm -f FontTest.class

# Display version info
RUN echo "=== Java Version ===" && java -version && javac -version
