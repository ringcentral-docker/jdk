FROM public.ecr.aws/docker/library/amazoncorretto:17.0.12-alpine3.20 as builder
WORKDIR /workspace/app

COPY test/FontTest.java .

RUN javac FontTest.java

FROM public.ecr.aws/docker/library/amazoncorretto:17.0.12-alpine3.20
LABEL maintainer="john.lin@ringcentral.com"

ENV JAVA_VERSION=17 \
    JAVA_DETAIL_VERSION=17.0.12 \
    JAVA_TOOL_OPTIONS="-XX:+IgnoreUnrecognizedVMOptions"

RUN apk add --no-cache libgcc fontconfig ttf-dejavu ca-certificates bash git openssh openssl zip subversion sshpass curl

COPY --from=builder /workspace/app/FontTest.class .

RUN java FontTest && rm -f FontTest.class

#==============
# Show version
#==============
RUN java -version \
    && javac -version