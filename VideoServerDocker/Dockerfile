FROM ubuntu:20.04

# Atualize o sistema e instale pacotes
RUN apt-get update && apt-get install -y \
    net-tools \
    iputils-ping \
    iproute2 \
    iperf

RUN apt-get install -y wget

RUN apt-get install -y unzip

# Baixe vídeos de teste
RUN apt-get update && apt-get install -y ffmpeg wget unzip && \
    mkdir /Videos && \
    cd /Videos && \
    wget https://download.blender.org/demo/movies/BBB/bbb_sunflower_1080p_60fps_normal.mp4.zip && \
    unzip bbb_sunflower_1080p_60fps_normal.mp4.zip && \
    mv bbb_sunflower_1080p_60fps_normal.mp4 teste_1080p.mp4 && \
    rm bbb_sunflower_1080p_60fps_normal.mp4.zip && \
    mkdir /Videos/chunks_1080p && \
    ffmpeg -i teste_1080p.mp4 -c copy -map 0 -segment_time 10 -f segment chunks_1080p/chunk_%03d.mp4 && \
    wget https://download.blender.org/demo/movies/BBB/bbb_sunflower_2160p_60fps_normal.mp4.zip && \
    unzip bbb_sunflower_2160p_60fps_normal.mp4.zip && \
    mv bbb_sunflower_2160p_60fps_normal.mp4 teste_2k.mp4 && \
    rm bbb_sunflower_2160p_60fps_normal.mp4.zip && \
    mkdir /Videos/chunks_2k && \
    ffmpeg -i teste_2k.mp4 -c copy -map 0 -segment_time 10 -f segment chunks_2k/chunk_%03d.mp4


RUN apt-get update && \
    apt-get install -y python3 git && \
    apt-get install -y python3-pip && \
        pip3 install flask

CMD ["/bin/bash"]
