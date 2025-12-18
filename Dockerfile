# Parameterized JDK Dockerfile with variant support
#
# Build examples:
#   Standard:    docker build --build-arg BASE_IMAGE_TAG=17.0.13_11-jdk-noble --build-arg JAVA_VERSION=17 --build-arg JAVA_DETAIL_VERSION=17.0.13 .
#   With Docker: docker build --build-arg BASE_IMAGE_TAG=17.0.13_11-jdk-noble --build-arg JAVA_VERSION=17 --build-arg JAVA_DETAIL_VERSION=17.0.13 --build-arg VARIANT=docker .
#   With Jacoco: docker build --build-arg BASE_IMAGE_TAG=17.0.13_11-jdk-noble --build-arg JAVA_VERSION=17 --build-arg JAVA_DETAIL_VERSION=17.0.13 --build-arg VARIANT=jacoco .

ARG BASE_IMAGE_TAG

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

ARG JAVA_VERSION
ARG JAVA_DETAIL_VERSION
ARG VARIANT=standard

ENV JAVA_VERSION=${JAVA_VERSION} \
    JAVA_DETAIL_VERSION=${JAVA_DETAIL_VERSION} \
    JAVA_TOOL_OPTIONS="-XX:+IgnoreUnrecognizedVMOptions"

# Copy installation scripts
COPY scripts/ /tmp/scripts/
RUN chmod +x /tmp/scripts/*.sh

# Install base dependencies (always)
RUN /tmp/scripts/install-base.sh

# Install variant-specific dependencies
RUN if [ "$VARIANT" = "docker" ]; then \
        /tmp/scripts/install-docker.sh; \
    elif [ "$VARIANT" = "jacoco" ]; then \
        /tmp/scripts/install-jacoco.sh; \
    fi

# Copy jacococli.jar for jacoco variant (if exists)
COPY assets/jacococli.ja[r] /opt/jacoco/

# Cleanup installation scripts
RUN rm -rf /tmp/scripts

# Verify font configuration
COPY --from=builder /workspace/app/FontTest.class .
RUN java FontTest && rm -f FontTest.class

# Display version info
RUN echo "=== Java Version ===" && java -version && javac -version
