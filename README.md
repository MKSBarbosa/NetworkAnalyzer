# NetworkAnalyzer

## Estrutura do Projeto

```plaintext
NetworkAnalyzer/
├── Application/
│   ├── iperfApplication.java
│   ├── PingApplication.java
│   ├── RadioApplication.java
│   ├── VideoApplication.java
│   ├── MainActivity.java
│   └── TesteActivity.java
├── apps/
│   ├── Networkanalizer_v1_0.apk
└── server/
    ├── server.py
    └── speedserver.py
```

## Descrição das aplicações

### Pasta `apps`
Contém apks disponiveis para uso.

### Pasta `Application`
Contém o aplicativo Android em Java para uso em dispositivos móveis. Dentro dela, existem as seguintes classes:

1. **Iperf Application**: Realiza a medição de download e upload utilizando o iperf3 (não utilizado, pois os telefones não disponibilizam o binário do iperf3).
2. **Ping Application**: Calcula a latência em relação ao servidor.
3. **Radio Application**: Aplicação de rádio que captura as métricas de rádio, como SNR, RSRP, e RSRQ.
4. **Video Application**: Realiza a solicitação de vídeo, podendo solicitar em 1080p, 2K, ou 4K.
5. **Main Activity**: Instruções da tela inicial.
6. **Teste Activity**: Tela que realiza a chamada das aplicações a partir das configurações de teste definidas na tela inicial, chamando todas as classes de aplicações.

### Pasta `server`
1. **server.py**: Servidor de streaming de vídeo e salvamento dos dados em CSV. As opções de vídeo correspondem a 1080p, 2K, e 4K. Deve-se indicar a pasta onde os vídeos estão armazenados; a atual é conforme o desktop.
2. **speedserver.py**: Em desenvolvimento. Criará uma solução alternativa para o iperf3, a fim de medir a vazão de upload e download.

## Instruções de Uso

### Clonando o Repositório
```bash
git clone <URL_DO_REPOSITORIO>
```

### Aplicativo Mobile

Abra a pasta Application dentro do Android Studio ou baixe o APK.

#### Uso do apk

Dentro da pasta `aplicativos`, há alguns APKs disponíveis. Opte pela versão mais atual ou a que melhor lhe atende.


### Servidor
Abra o terminal.
Acesse a pasta do repositório:
```bash
cd NetworkAnalyzer
```
Navegue até a pasta server:

```bash
cd server
```
Execute o servidor:

```bash
python3 server.py
```

### Observações

1. Certifique-se de que a pasta onde os vídeos estão armazenados está corretamente configurada no server.py.
2. speedserver.py está em desenvolvimento e ainda não está disponível para uso.
3. O app apresenta um bug, as requisições do video sempre ocorrem sample+1, ou seja, se for testado 1, vai 2, se for 3, vai 4, se for 5, vai 6.
4. Entre as solicitações do video o app pode apresentar instabilidade, aguardar, pois irá funcionar normalmente. 
