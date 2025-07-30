
import os
import re
import csv
from flask import Flask, send_from_directory, request, jsonify, Response, abort

app = Flask(__name__)

nome_arquivo_csv = 'dados.csv'
VIDEO_DIR = "../../../Videos"

def salvar_dados_csv(dados):
    arquivo_existe = False
    try:
        with open(nome_arquivo_csv, 'r') as csvfile:
            reader = csv.reader(csvfile)
            arquivo_existe = any(row for row in reader)
    except FileNotFoundError:
        pass

    with open(nome_arquivo_csv, 'a', newline='') as csvfile:
        fieldnames = ['id','rsrp', 'rsrq', 'snr', 'download', 'upload', 'jitterD',"jitterU", 'ping', 'vazao', 'tempoDeCarregamento']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)

        if not arquivo_existe:
            writer.writeheader()
        writer.writerow(dados)

@app.route('/teste')
def success():
   return 'testando'

@app.route('/vod/<quality>')
def stream_video(quality):
    video_files = {
        '1080p': 'teste_1080p.mp4',
        '2K': 'teste_2k.mp4',
        '4K': 'teste_4k.mp4'
    }

    video_filename = video_files.get(quality)
    if not video_filename:
        return "Quality not supported", 404

    video_path = os.path.join(VIDEO_DIR, video_filename)
    try:
        file_size = os.path.getsize(video_path)
        range_header = request.headers.get('Range', None)

        if range_header:
            # Ex: 'bytes=1000-'
            byte1, byte2 = 0, None
            match = re.search(r'bytes=(\d+)-(\d*)', range_header)
            if match:
                byte1 = int(match.group(1))
                if match.group(2):
                    byte2 = int(match.group(2))

            byte2 = byte2 or file_size - 1
            length = byte2 - byte1 + 1

            with open(video_path, 'rb') as f:
                f.seek(byte1)
                data = f.read(length)

            response = Response(data, status=206, mimetype='video/mp4')
            response.headers.add('Content-Range', f'bytes {byte1}-{byte2}/{file_size}')
            response.headers.add('Accept-Ranges', 'bytes')
            response.headers.add('Content-Length', str(length))
        else:
            # fallback: send full video
            with open(video_path, 'rb') as f:
                data = f.read()
            response = Response(data, status=200, mimetype='video/mp4')
            response.headers.add('Content-Length', str(file_size))

        return response

    except FileNotFoundError:
        abort(404)


@app.route('/video/<quality>', methods=['GET'])
def send_video(quality):
    video_files = {
        '1080p': 'teste_1080p.mp4',
        '2K': 'teste_2k.mp4',
        '4K': 'teste_4k.mp4'
    }
    
    video_path = video_files.get(quality)
    
    if video_path:
        return send_from_directory(directory=VIDEO_DIR, path=video_path, as_attachment=False)
    else:
        return "Quality not supported", 404

@app.route('/registrar_dados', methods=['POST'])
def registrar_dados():
    try:
        dados = request.json
        salvar_dados_csv(dados)
        return jsonify({'status': 'sucesso', 'mensagem': 'Dados registrados com sucesso'})

    except Exception as e:
        return jsonify({'status': 'erro', 'mensagem': str(e)})
    
if __name__ == '__main__':
    app.run(host='192.168.70.135', port=3001)
