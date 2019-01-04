
## Build Status
[![Build Status](https://travis-ci.org/ringcentral-docker/jdk.svg?branch=master)](https://travis-ci.org/ringcentral-docker/jdk)

## Image description

* Base on the latest version of official alpine Docker Image : [link](https://hub.docker.com/_/alpine/)

```bash
java version "1.8.0_191"
Java(TM) SE Runtime Environment (build 1.8.0_191-b12)
Java HotSpot(TM) 64-Bit Server VM (build 25.191-b12, mixed mode)
javac 1.8.0_191
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
