docker stop cb8c

docker cp ./ cb8c:/root/microservices-docker

docker start cb8c

docker exec -d cb8c sh /root/microservices-docker/start.sh
