FROM openjdk

WORKDIR usr/lib

ADD ./target/product-0.0.1-SNAPSHOT.jar /usr/lib/product-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","product-0.0.1-SNAPSHOT.jar"]