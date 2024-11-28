FROM ubuntu:20.04

# Atualize o sistema e instale pacotes
RUN apt-get update && apt-get install -y \
    net-tools \
    iputils-ping \
    python3 \
    git \
    iproute2 \
    iperf3 \
    python3-pip

# Instale Flask via pip
RUN pip3 install flask

RUN mkdir Videos 

CMD ["/bin/bash"]