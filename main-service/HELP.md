## Main service

### run without docker - add to environment variables this params

```
STATS_SERVICE_URL=http://localhost:9090;
POSTGRES_DB=ewm;
POSTGRES_HOST=localhost;
POSTGRES_USER=postgres;
POSTGRES_PASSWORD=postgres;
POSTGRES_PORT=5432;
SERVER_PORT=8080;
```

---

### docker commands

```bash
docker build -t stasma/main_service_image:latest .
```

```bash
 docker push stasma/main_service_image:latest
```

```bash
docker run --name main_service_container -p 8081:8081 -p 8080:8080 -e SERVER_PORT=8080 -e STATS_SERVICE_URL=http://localhost:9090 -e POSTGRES_DB=ewm -e POSTGRES_HOST=localhost -e POSTGRES_PASSWORD=postgres -e POSTGRES_PORT=5432 -e POSTGRES_USER=postgres  stasma/main_service_image
```

```bash
docker start main_service_container 
```

```bash
docker exec -it main_service_container /bin/sh
```