FROM sonatype-nexus-repository-infra.ru-central1.internal:9082/base-images/smartax-service:latest

RUN adduser --disabled-password --gecos '' duiservice


WORKDIR /app

COPY service/build/libs/*.jar /tmp/jars/

RUN cp $(find /tmp/jars/ -type f -name "*.jar" ! -name "*javadoc.jar" ! -name "*sources.jar") ./app.jar

RUN find / -perm +6000 -type f -exec chmod a-s {} \; || true

EXPOSE 8080
EXPOSE 9090

USER duiservice

HEALTHCHECK --interval=5m --timeout=3s CMD wget http://localhost:8080/ || exit 1

ENTRYPOINT export JAVA_OPTS=$(echo $JAVA_OPTS | sed "s/\$START_TIME/$(date +%Y-%m-%d_%H-%M)/"); \
           exec java $JAVA_OPTS -jar ./app.jar
