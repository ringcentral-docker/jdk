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
    jq \
    && rm -rf /var/lib/apt/lists/*

RUN apt-get update
RUN apt-get install -y ca-certificates curl gnupg lsb-release
RUN mkdir -p /etc/apt/keyrings
RUN curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
RUN echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null
RUN apt-get update
RUN apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin 

COPY --from=builder /workspace/app/FontTest.class .

RUN java FontTest && rm -f FontTest.class

#==============
# Show version
#==============
RUN java -version \
    && javac -version
