FROM ubuntu:20.04

RUN set -eux \
  && apt-get update \
  && DEBIAN_FRONTEND="noninteractive" apt-get -y install --no-install-recommends \
    ca-certificates \
    locales \
    tzdata \
    wget \
    maven \
    vim \
    curl \
    git \
    openjdk-17-jdk \
    openjdk-17-source \
    openssh-server \
  && rm -rf /var/lib/apt/lists/* \
  && mkdir /var/run/sshd

EXPOSE 22

CMD ["/usr/sbin/sshd", "-D"]