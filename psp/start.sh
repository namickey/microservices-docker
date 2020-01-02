cd `dirname $0`
cd api
./gradlew assemble
java -Dmicronaut.server.port=8081 -Dcompany.code=P001 -jar build/libs/api-0.1-all.jar >&1 &
java -Dmicronaut.server.port=8086 -Dcompany.code=P002 -jar build/libs/api-0.1-all.jar >&1 &
