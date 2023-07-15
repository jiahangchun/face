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
        openssh-server \
    && rm -rf /var/lib/apt/lists/* \
    && mkdir /var/run/sshd

RUN apt-get clean

# jar包方式安装jdk 可以指定未支持的高版本 这里设置了带graalvm的jdk20
ENV JAVA_HOME /usr/local/java
ENV PATH ${PATH}:${JAVA_HOME}/bin
RUN wget --no-check-certificate -c --header "Cookie: oraclelicense=accept-securebackup-cookie" "https://download.oracle.com/graalvm/20/latest/graalvm-jdk-20_linux-x64_bin.tar.gz" -O /tmp/jdk.tar.gz 
RUN tar xzf /tmp/jdk.tar.gz -C /usr/local/ 
RUN mv /usr/local/graalvm-jdk-* ${JAVA_HOME}
RUN rm -rf /tmp/jdk.tar.gz
RUN java -version

# 构建命令 docker build -t base/jdk20_graalvm .
CMD ["/usr/sbin/sshd", "-D"]
