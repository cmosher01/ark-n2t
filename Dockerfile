FROM eclipse-temurin:17-jdk-jammy AS build

LABEL org.opencontainers.image.authors="Christopher Alan Mosher <cmosher01@gmail.com>"
LABEL org.opencontainers.image.title="ARK N2T resolver servlet"
LABEL org.opencontainers.image.url="https://github.com/cmosher01/ark-n2t"
LABEL org.opencontainers.image.licenses="GPL-3.0-or-later"

USER root
ENV HOME /root
WORKDIR $HOME

COPY gradle/ gradle/
COPY gradlew ./
RUN ./gradlew --version

COPY settings.gradle ./
COPY lib/ ./lib/
COPY cli/ ./cli/
COPY app/ ./app/

RUN ./gradlew -i build



FROM tomcat:10-jdk17-temurin-jammy AS run

USER root
ENV HOME /root
WORKDIR $HOME

RUN apt-get update && apt-get install -y sqlite

COPY --from=build /root/app/src/main/tomcat/conf /usr/local/tomcat/conf
COPY --from=build /root/app/build/libs/*.war /usr/local/tomcat/webapps/ROOT.war
COPY --from=build /root/cli/build/distributions/*.tar ./
RUN tar --strip-components=1 -C /usr/local -xvf *.tar
