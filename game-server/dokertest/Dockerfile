FROM alpine:3.19.1

WORKDIR /server/

RUN apk add openjdk18-jre

COPY craftbukkit-1.20.4.jar /server/

COPY inputAndOutput.java /control/
COPY input.txt /control/
COPY output.txt /control/
COPY meomory.txt /control/
COPY stop.sh /control/
COPY start.sh /control/

RUN chmod +x /control/start.sh
RUN chmod +x /control/stop.sh

RUN java -jar /server/craftbukkit-1.20.4.jar 

RUN sed -i 's/eula=false/eula=true/g' /server/eula.txt

WORKDIR /

CMD ["java", "/control/inputAndOutput.java"]


# CMD ["java", "-Xmx2G", "-jar", "/server/craftbukkit-1.20.4.jar"]