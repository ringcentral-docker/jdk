
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

Temurin JDK 8

```bash
openjdk version "1.8.0_332"
OpenJDK Runtime Environment (Temurin)(build 1.8.0_332-b09)
OpenJDK 64-Bit Server VM (Temurin)(build 25.332-b09, mixed mode)
javac 1.8.0_332
```

Temurin JDK 11

```bash
openjdk version "11.0.18" 2023-01-17
OpenJDK Runtime Environment Temurin-11.0.18+10 (build 11.0.18+10)
OpenJDK 64-Bit Server VM Temurin-11.0.18+10 (build 11.0.18+10, mixed mode)
javac 11.0.18
```

Temurin JDK 17

```bash
openjdk version "17.0.6" 2023-01-17
OpenJDK Runtime Environment Temurin-17.0.6+10 (build 17.0.6+10)
OpenJDK 64-Bit Server VM Temurin-17.0.6+10 (build 17.0.6+10, mixed mode, sharing)
javac 17.0.6
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
