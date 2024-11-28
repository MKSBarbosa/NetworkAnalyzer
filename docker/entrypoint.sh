#!/bin/bash

# Adicione a rota IP
# ip route add 12.1.1.0/24 via 192.168.70.134 dev eth0

# # Execute o servidor iperf em segundo plano
# iperf3 -s -i 1 &

# apt-get update && \
#     apt-get install -y python3 git && \
#     apt-get install -y python3-pip && \
#         pip3 install flask

git clone https://github.com/MKSBarbosa/NetworkAnalyzer.git


# Obter o IP da interface wlp0s20f3
IP=$(ip -o -4 addr list wlp0s20f3 | awk '{print $4}' | cut -d/ -f1)
echo "$IP"

# Verificar se o IP foi obtido corretamente
if [ -z "$IP" ]; then
  echo "Erro: Não foi possível obter o IP da interface wlp0s20f3"
  exit 1
fi

# Substituir o IP no arquivo server.py
sed -i "s/host='192.168.1.140'/host='$IP'/g" NetworkAnalyzer/server/server.py

python3 NetworkAnalyzer/server/server.py &

# Aguarde indefinidamente para manter o contêiner em execução
wait