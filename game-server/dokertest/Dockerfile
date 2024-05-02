FROM alpine:3.19.1

WORKDIR /server/

RUN apk add openjdk18-jre

COPY . .

RUN sed -i 's/eula=false/eula=true/g' eula.txt

CMD ["java", "inputAndOutput.java"]


# CMD ["java", "-Xmx2G", "-jar", "/server/craftbukkit-1.20.4.jar"]