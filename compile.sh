
#!/bin/bash
export CLASSPATH=/home/pole/raspberry-pi-cloud-educational-tool-internship/resources/java_websocket.jar:$CLASSPATH
export CLASSPATH=/home/pole/raspberry-pi-cloud-educational-tool-internship/resources/mysql-connector-java-5.1.26-bin.jar:$CLASSPATH
export CLASSPATH=/home/pole/raspberry-pi-cloud-educational-tool-internship/resources/gson-2.2.4.jar:$CLASSPATH

javac  /home/pole/raspberry-pi-cloud-educational-tool-internship/pi_cloud/piManager/*.java 
javac  /home/pole/raspberry-pi-cloud-educational-tool-internship/pi_cloud/piClient/*.java

