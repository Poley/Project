sudo ifconfig eth0 192.168.100.100 netmask 255.255.255.0 up
export CLASSPATH=.:$CLASSPATH:/home/pole/raspberry-pi-cloud-educational-tool-internship/resources/java_websocket.jar 
export CLASSPATH=.:$CLASSPATH:/home/pole/raspberry-pi-cloud-educational-tool-internship/resources/mysql-connector-java-5.1.26-bin.jar
export CLASSPATH=.:$CLASSPATH:/home/pole/raspberry-pi-cloud-educational-tool-internship/resources/gson-2.2.4.jar
java -Djava.security.policy==tempPolicy -Djava.rmi.server.hostname=192.168.100.100 pi_cloud.piManager.Controller >> log.txt &
java pi_cloud.piClient.initClient >> log.txt &
java pi_cloud.piClient.initClient >> log.txt &
