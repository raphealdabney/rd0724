#!/bin/bash

docker compose down --volumes && docker rm -f $(docker ps -a -q) 
docker volume rm $(docker volume ls -q) && echo "TRUE"
docker compose up --build
#docker system prune -a --volumes
