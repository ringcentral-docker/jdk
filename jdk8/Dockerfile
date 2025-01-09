FROM public.ecr.aws/docker/library/eclipse-temurin:8u432-b06-jdk-noble as builder
WORKDIR /workspace/app

COPY test/FontTest.java .

RUN javac FontTest.java

FROM public.ecr.aws/docker/library/eclipse-temurin:8u432-b06-jdk-noble

ENV JAVA_VERSION=8 \
    JAVA_DETAIL_VERSION=8u432 \
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
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /workspace/app/FontTest.class .

RUN java FontTest && rm -f FontTest.class

#==============
# Show version
#==============
RUN java -version \
    && javac -version
