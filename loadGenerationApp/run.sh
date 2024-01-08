
CLASSPATH=./target/kafka-connect-acme-source-0.0.1-jar-with-dependencies.jar \
    ~/Applications/kafka/kafka_2.13-2.8.2/bin/connect-standalone.sh \
    ~/Applications/kafka/kafka_2.13-2.8.2/config/connect-standalone.properties \
    connector.properties