#!/bin/bash

# git clone https://github.com/MKSBarbosa/NetworkAnalyzer.git

# Obter o IP da interface eth0 of the container
# IP=$(ip -o -4 addr list eth0 | awk '{print $4}' | cut -d/ -f1)
# echo "$IP"

# Verificar se o IP foi obtido corretamente
# if [ -z "$IP" ]; then
#   echo "Erro: Não foi possível obter o IP da interface wlp0s20f3"
#   exit 1
# fi

# Substituir o IP no arquivo server.py
# sed -i "s/host='192.168.1.140'/host='$IP'/g" NetworkAnalyzer/server/server.py

python3 NetworkAnalyzer/server/server.py &

wait