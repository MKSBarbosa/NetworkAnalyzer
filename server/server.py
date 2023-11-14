from flask import Flask, send_from_directory

app = Flask(__name__)

@app.route('/teste')
def success():
   return 'testando'

@app.route('/video', methods=['GET'])
def send_video():
    return send_from_directory(directory="../Videos", path="teste.mp4", as_attachment=False)

if __name__ == '__main__':
    app.run(host='localhost', port=3001)