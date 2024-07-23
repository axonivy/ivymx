FROM maven:3.8.6-jdk-11

RUN apt-get -y update && apt-get -y install gnupg2 && apt-get install git -y
