#!/bin/bash
# Jacoco and swagger-coverage installation script for jdk17-jacoco variant
set -e

# Install additional dependencies
apt-get update && apt-get install -y --no-install-recommends \
    wget \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# Install swagger-coverage-commandline
wget -q https://github.com/viclovsky/swagger-coverage/releases/download/1.5.0/swagger-coverage-1.5.0.zip
unzip -q swagger-coverage-1.5.0.zip
mv swagger-coverage-commandline-1.5.0 /opt/swagger-coverage
rm swagger-coverage-1.5.0.zip
ln -s /opt/swagger-coverage/bin/swagger-coverage-commandline /usr/local/bin/swagger-coverage

# Create jacoco directory (jacococli.jar will be copied separately)
mkdir -p /opt/jacoco
