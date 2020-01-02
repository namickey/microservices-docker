cd `dirname $0`
cd api
./gradlew assemble
java -Dmicronaut.server.port=8080 -Dpsp.port=8081 -Dmerchant.code=M001 -jar build/libs/api-0.1-all.jar >&1 &
java -Dmicronaut.server.port=8084 -Dpsp.port=8081 -Dmerchant.code=M002 -jar build/libs/api-0.1-all.jar >&1 &
java -Dmicronaut.server.port=8085 -Dpsp.port=8086 -Dmerchant.code=M003 -jar build/libs/api-0.1-all.jar >&1 &
