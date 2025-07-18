FROM public.ecr.aws/docker/library/eclipse-temurin:17.0.13_11-jdk-noble as builder
WORKDIR /workspace/app

COPY test/FontTest.java .

RUN javac FontTest.java

FROM public.ecr.aws/docker/library/eclipse-temurin:17.0.13_11-jdk-noble

ENV JAVA_VERSION=17 \
    JAVA_DETAIL_VERSION=17.0.13 \
    JAVA_TOOL_OPTIONS="-XX:+IgnoreUnrecognizedVMOptions"

RUN apt-get update && apt-get install -y \
    libgcc1 \
    fontconfig \
    fonts-dejavu \
    ca-certificates \
    bash \
    git \
    openssh-client \
    openssl \
    zip \
    subversion \
    sshpass \
    curl \
    wget \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# Install swagger-coverage-commandline
RUN wget https://github.com/viclovsky/swagger-coverage/releases/download/1.5.0/swagger-coverage-1.5.0.zip \
    && unzip swagger-coverage-1.5.0.zip \
    && mv swagger-coverage-commandline-1.5.0 /opt/swagger-coverage \
    && rm swagger-coverage-1.5.0.zip \
    && ln -s /opt/swagger-coverage/bin/swagger-coverage-commandline /usr/local/bin/swagger-coverage

# Copy jacococli.jar to the image
COPY jacococli.jar /opt/jacoco/jacococli.jar

COPY --from=builder /workspace/app/FontTest.class .

RUN java FontTest && rm -f FontTest.class

#==============
# Show version
#==============
RUN java -version \
    && javac -version
