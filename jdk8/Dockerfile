FROM public.ecr.aws/docker/library/eclipse-temurin:8u432-b06-jdk-alpine as builder
WORKDIR /workspace/app

COPY test/FontTest.java .

RUN javac FontTest.java

FROM public.ecr.aws/docker/library/eclipse-temurin:8u432-b06-jdk-alpine
LABEL maintainer="john.lin@ringcentral.com"

ENV JAVA_VERSION=8 \
    JAVA_DETAIL_VERSION=8u432 \
    JAVA_TOOL_OPTIONS="-XX:+IgnoreUnrecognizedVMOptions"

RUN apk add --no-cache libgcc fontconfig ttf-dejavu ca-certificates bash git openssh openssl zip subversion sshpass curl

COPY --from=builder /workspace/app/FontTest.class .

RUN java FontTest && rm -f FontTest.class

# Install vanilla GLibC: https://github.com/sgerrand/alpine-pkg-glibc
RUN curl -o /etc/apk/keys/sgerrand.rsa.pub https://alpine-pkgs.sgerrand.com/sgerrand.rsa.pub \
&& curl -LO https://github.com/sgerrand/alpine-pkg-glibc/releases/download/2.35-r1/glibc-2.35-r1.apk \
&& apk add glibc-2.35-r1.apk

#==============
# Show version
#==============
RUN java -version \
    && javac -version
