
# JDK

## Build Status

Oracle JDK 8

[![Build JDK 8](https://github.com/ringcentral-docker/jdk/actions/workflows/build-jdk8.yml/badge.svg)](https://github.com/ringcentral-docker/jdk/actions/workflows/build-jdk8.yml)

AdoptOpenJDK 11

[![Build AdoptOpenJDK 11](https://github.com/ringcentral-docker/jdk/actions/workflows/build-jdk11.yml/badge.svg)](https://github.com/ringcentral-docker/jdk/actions/workflows/build-jdk11.yml)


AdoptOpenJDK 17

[![Build AdoptOpenJDK 17](https://github.com/ringcentral-docker/jdk/actions/workflows/build-jdk17.yml/badge.svg)](https://github.com/ringcentral-docker/jdk/actions/workflows/build-jdk17.yml)
## Image description

* Base on the latest version of official alpine Docker Image : [link](https://hub.docker.com/_/alpine/)

Oracle JDK 8

```bash
java version "1.8.0_202"
Java(TM) SE Runtime Environment (build 1.8.0_202-b08)
Java HotSpot(TM) 64-Bit Server VM (build 25.202-b08, mixed mode)
javac 1.8.0_202
```

AdoptOpenJDK 11

```bash
Picked up JAVA_TOOL_OPTIONS: -XX:+IgnoreUnrecognizedVMOptions -XX:+UseContainerSupport -XX:+IdleTuningCompactOnIdle -XX:+IdleTuningGcOnIdle
openjdk version "11.0.12" 2021-07-20
OpenJDK Runtime Environment Temurin-11.0.12+7 (build 11.0.12+7)
OpenJDK 64-Bit Server VM Temurin-11.0.12+7 (build 11.0.12+7, mixed mode)
Picked up JAVA_TOOL_OPTIONS: -XX:+IgnoreUnrecognizedVMOptions -XX:+UseContainerSupport -XX:+IdleTuningCompactOnIdle -XX:+IdleTuningGcOnIdle
javac 11.0.12
```

AdoptOpenJDK 17

```bash
Picked up JAVA_TOOL_OPTIONS: -XX:+IgnoreUnrecognizedVMOptions -XX:+UseContainerSupport -XX:+IdleTuningCompactOnIdle -XX:+IdleTuningGcOnIdle
openjdk version "17" 2021-09-14
OpenJDK Runtime Environment Temurin-17+35 (build 17+35)
OpenJDK 64-Bit Server VM Temurin-17+35 (build 17+35, mixed mode, sharing)
Picked up JAVA_TOOL_OPTIONS: -XX:+IgnoreUnrecognizedVMOptions -XX:+UseContainerSupport -XX:+IdleTuningCompactOnIdle -XX:+IdleTuningGcOnIdle
javac 17
```
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
