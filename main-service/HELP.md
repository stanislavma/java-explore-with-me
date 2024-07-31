## Stats service

### run without docker - add to environment variables this params

```
STATS_SERVICE_URL=http://localhost:9090;POSTGRES_DB=ewm;POSTGRES_HOST=localhost;POSTGRES_PASSWORD=postgres;POSTGRES_PORT=5432;POSTGRES_USER=postgres;SERVER_PORT=8080;
```

---

### docker commands
 
```bash
docker build -t stasma/stats_service_image:latest .
```

```bash
 docker push stasma/stats_service_image:latest
```

```bash
docker run --name stats_service_container -p 9091:9091 -p 9090:9090 stats_service_image
```

```bash
docker start stats_service_container 
```

```bash
docker exec -it stats_service_container /bin/sh
```