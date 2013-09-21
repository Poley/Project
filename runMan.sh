export CLASSPATH=/home/jamie/raspberryPiCloud/resources/java_websocket.jar:$CLASSPATH
export CLASSPATH=/home/jamie/raspberryPiCloud/resources/mysql-connector-java-5.1.26-bin.jar:$CLASSPATH
export CLASSPATH=/home/jamie/raspberryPiCloud/resources/gson-2.2.4.jar:$CLASSPATH
java -Djava.security.policy==tempPolicy -Djava.rmi.server.hostname=192.168.100.100 pi_cloud.piManager.Controller 

