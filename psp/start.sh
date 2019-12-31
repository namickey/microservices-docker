cd `dirname $0`
cd api
./gradlew assemble
java -jar build/libs/api-0.1-all.jar >&1 &
