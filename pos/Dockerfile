FROM nginx:alpine
COPY build/. /usr/share/nginx/html
COPY ./default.conf /etc/nginx/conf.d/
EXPOSE 8080
CMD ["nginx", "-g", "daemon off;"]
