#!/usr/bin/env bash

cd pos
npm run build
docker cp build/. tool-rental-pos-system-frontend-1:/usr/share/nginx/html
cd ..
