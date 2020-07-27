FROM openjdk:11
COPY target/demo-0.0.1-SNAPSHOT.jar /usr/app/
WORKDIR /usr/app
RUN sh -c 'touch demo-0.0.1-SNAPSHOT.jar'
COPY docker/init.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/init.sh
ENV SSH_PASSWD "root:Docker!"
RUN apt-get update \
        && apt-get install -y --no-install-recommends dialog \
        && apt-get update \
  && apt-get install -y --no-install-recommends openssh-server \
  && echo "$SSH_PASSWD" | chpasswd
COPY docker/sshd_config /etc/ssh/
ENTRYPOINT ["init.sh"]