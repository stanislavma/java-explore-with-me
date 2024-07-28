```bash
docker build -t stasma/main_service_image:latest .
```
 
```bash
 docker push stasma/main_service_image:latest
```
 
```bash
docker run --name main_service_container -p 8081:8081 -p 8080:8080 main_service_image
```

```bash
docker start main_service_container 
```

```bash
docker exec -it main_service_container /bin/sh
```