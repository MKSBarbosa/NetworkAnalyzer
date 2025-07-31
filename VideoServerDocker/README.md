# Inserting the Video Server Docker in Your Network

In you yaml file add this

In the UPF add a new network:

```
traffic_net:
ipv4_address: 192.168.70.134
```

After all the services and before the network, add the video server container:

```
  VideoServer:
    privileged: true
    init: true
    container_name: video_server
    build: ./VideoServerDocker
    image: docker_ubuntu_focal
    volumes:
        - ./VideoServer/entrypoint.sh:/tmp/entrypoint.sh
    entrypoint: ./tmp/entrypoint.sh
    depends_on:
        - upf
    networks:
        traffic_net:
            ipv4_address: 192.168.70.135
    healthcheck:
        test: /bin/bash -c "ping -c 2 192.168.70.134"
        interval: 10s
        timeout: 5s
        retries: 5

```

And, finally create the network, add the new network below the existing one:

```
  traffic_net:
    ipam:
        config:
            - subnet: 192.168.70.128/26
```

After configure the yaml, copy the VideoServerDocker directory on your network directory, to alow the image to be build.