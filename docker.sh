docker stop 5d16

docker cp ./ 5d16:/root/microservices-docker

docker start 5d16

docker exec -d 5d16 sh /root/microservices-docker/start.sh
