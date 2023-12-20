from flask import Flask, send_from_directory, request, jsonify
import csv
app = Flask(__name__)

nome_arquivo_csv = 'dados.csv'

def salvar_dados_csv(dados):
    # Verificar se o arquivo CSV já existe
    arquivo_existe = False
    try:
        with open(nome_arquivo_csv, 'r') as csvfile:
            reader = csv.reader(csvfile)
            arquivo_existe = any(row for row in reader)
    except FileNotFoundError:
        pass

    # Abrir o arquivo CSV em modo de anexação
    with open(nome_arquivo_csv, 'a', newline='') as csvfile:
        fieldnames = ['id','rsrp', 'rsrq', 'snr', 'download', 'upload', 'jitterD',"jitterU", 'ping', 'vazao', 'tempoDeCarregamento']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)

        # Se o arquivo não existir, escrever o cabeçalho
        if not arquivo_existe:
            writer.writeheader()

        # Escrever os dados no arquivo CSV
        writer.writerow(dados)

@app.route('/teste')
def success():
   return 'testando'

@app.route('/video', methods=['GET'])
def send_video():
    return send_from_directory(directory="../../Videos", path="teste.mp4", as_attachment=False)

@app.route('/registrar_dados', methods=['POST'])
def registrar_dados():
    try:
        # Obter dados do corpo da solicitação POST
        dados = request.json

        # Salvar dados no arquivo CSV
        salvar_dados_csv(dados)

        # Retornar uma resposta JSON indicando sucesso
        return jsonify({'status': 'sucesso', 'mensagem': 'Dados registrados com sucesso'})

    except Exception as e:
        # Em caso de erro, retornar uma resposta JSON indicando o erro
        return jsonify({'status': 'erro', 'mensagem': str(e)})
    
if __name__ == '__main__':
    app.run(host='192.168.70.135', port=3001)
