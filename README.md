# JDK Docker Images

Multi-platform JDK Docker images based on Eclipse Temurin.

## Supported Platforms

- linux/amd64
- linux/arm64

## Available Images

| JDK Version | Version | Docker Hub Tags | GitHub Package Tags |
|-------------|---------|-----------------|---------------------|
| jdk8 | 8 | `ringcentral/jdk:8` `ringcentral/jdk:8u504` | `ghcr.io/ringcentral-docker/jdk:8` `ghcr.io/ringcentral-docker/jdk:8u504` |
| jdk11 | 11 | `ringcentral/jdk:11` `ringcentral/jdk:11.0.32.1` | `ghcr.io/ringcentral-docker/jdk:11` `ghcr.io/ringcentral-docker/jdk:11.0.32.1` |
| jdk17 | 17 | `ringcentral/jdk:17` `ringcentral/jdk:17.0.20.1` | `ghcr.io/ringcentral-docker/jdk:17` `ghcr.io/ringcentral-docker/jdk:17.0.20.1` |
| jdk21 | 21 | `ringcentral/jdk:21` `ringcentral/jdk:21.0.12.1` | `ghcr.io/ringcentral-docker/jdk:21` `ghcr.io/ringcentral-docker/jdk:21.0.12.1` |
| jdk25 | 25 | `ringcentral/jdk:25` `ringcentral/jdk:25.0.4.1` | `ghcr.io/ringcentral-docker/jdk:25` `ghcr.io/ringcentral-docker/jdk:25.0.4.1` |

## Usage

```bash
# Pull from Docker Hub
docker pull ringcentral/jdk:21

# Pull from GitHub Container Registry
docker pull ghcr.io/ringcentral-docker/jdk/jdk:21

# Run with specific version
docker run -it ringcentral/jdk:21 java -version
```

## Build Locally

```bash
# Build specific version (see versions/versions.json for available configurations)
docker build \
  --build-arg BASE_IMAGE_TAG=21.0.12.1_1-jdk-noble \
  --build-arg JAVA_VERSION=21 \
  --build-arg JAVA_DETAIL_VERSION=21.0.12.1 \
  -t my-jdk:21 .
```

## License

MIT License
