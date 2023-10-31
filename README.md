
# JDK

## Build Status

OpenJDK 8

[![Build JDK 8](https://github.com/ringcentral-docker/jdk/actions/workflows/build-jdk8.yml/badge.svg)](https://github.com/ringcentral-docker/jdk/actions/workflows/build-jdk8.yml)

OpenJDK 11

[![Build OpenJDK 11](https://github.com/ringcentral-docker/jdk/actions/workflows/build-jdk11.yml/badge.svg)](https://github.com/ringcentral-docker/jdk/actions/workflows/build-jdk11.yml)


OpenJDK 17

[![Build OpenJDK 17](https://github.com/ringcentral-docker/jdk/actions/workflows/build-jdk17.yml/badge.svg)](https://github.com/ringcentral-docker/jdk/actions/workflows/build-jdk17.yml)

OpenJDK 21

[![Build OpenJDK 21](https://github.com/ringcentral-docker/jdk/actions/workflows/build-jdk21.yml/badge.svg)](https://github.com/ringcentral-docker/jdk/actions/workflows/build-jdk21.yml)

## Image description

* Base on the latest version of official alpine Docker Image : [link](https://hub.docker.com/_/alpine/)
## How to use this image

### Start a Java instance in your app

The most straightforward way to use this image is to use a Java container as both the build and runtime environment. In your Dockerfile, writing something along the lines of the following will compile and run your project:

```Docker
FROM ringcentral/jdk:latest
COPY . /usr/src/myapp
WORKDIR /usr/src/myapp
RUN javac Main.java
CMD ["java", "Main"]
```

You can then run and build the Docker image:

$ docker build -t my-java-app .
$ docker run -it --rm --name my-running-app my-java-app

## Get the Image

```bash
docker pull ringcentral/jdk:latest
```

for more detail information please refer this url:

<https://github.com/ringcentral-docker/jdk>

<https://hub.docker.com/r/ringcentral/jdk>
