# Start with Java 8 runtime
FROM eclipse-temurin:8-jdk

# Maintainer Info
LABEL maintainer="iiitb"

# Temporary directory
VOLUME /tmp

# Make port 8082 available
EXPOSE 8082

# Add the application's WAR file
COPY target/cms.war cms.war

# Create directory for Tomcat access logs
RUN mkdir -p -m 777 /var/log/tomcat

# Create directory for application logs
RUN mkdir -p -m 777 /var/log/cms

# Run the WAR file
ENTRYPOINT ["java", "-jar", "cms.war"]