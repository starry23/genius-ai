FROM eclipse-temurin:17-jre
MAINTAINER apeto

ENV SPRING_HOME=/home/spring
RUN mkdir -p $SPRING_HOME/config \
	&& mkdir -p $SPRING_HOME/logs

VOLUME ["$SPRING_HOME/config", "$SPRING_HOME/logs"]

WORKDIR $SPRING_HOME
ARG JAR_FILE=./ai-server/target/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080
ENV JAVA_OPTS -Xmx1024m -Djava.awt.headless=true -XX:+HeapDumpOnOutOfMemoryError \
 -XX:MaxGCPauseMillis=20 -XX:InitiatingHeapOccupancyPercent=35 -Xloggc:/home/spring/logs/gc.log \
 -Duser.timezone=Asia/Shanghai
ENV SPRING_PROFILES_ACTIVE=dev
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar $SPRING_HOME/app.jar"]
