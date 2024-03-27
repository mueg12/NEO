FROM openjdk:18

WORKDIR /

COPY craftbukkit-1.20.4.jar ./

RUN java -jar craftbukkit-1.20.4.jar

RUN sed -i 's/eula=false/eula=true/g' eula.txt

CMD ["java", "-Xmx2G", "-jar", "/craftbukkit-1.20.4.jar"]