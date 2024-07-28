## Stats service

### run without docker - add to environment variables this params

```
POSTGRES_DB=endpointHit;POSTGRES_HOST=localhost;POSTGRES_PASSWORD=postgres;POSTGRES_PORT=5432;POSTGRES_USER=postgres;SERVER_PORT=9090;
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