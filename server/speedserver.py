# server.py
import socket
import threading
import speedtest
import time

def handle_client(client_socket):
    st = speedtest.Speedtest()

    # Download
    download_speed = st.download() / 10**6  # Convert to Mbps

    # Upload
    upload_speed = st.upload() / 10**6  # Convert to Mbps

    # Ping (Jitter approximation)
    jitter_sum = 0
    delay_sum = 0
    num_pings = 5

    for _ in range(num_pings):
        start_time = time.time()
        st.get_best_server()
        end_time = time.time()

        delay = end_time - start_time
        delay_sum += delay
        jitter_sum += abs(delay - delay_sum / (num_pings + 1))

    average_delay = delay_sum / num_pings
    average_jitter = jitter_sum / num_pings

    result = f"Download Speed: {download_speed:.2f} Mbps\n" \
             f"Upload Speed: {upload_speed:.2f} Mbps\n" \
             f"Average Delay: {average_delay:.2f} seconds\n" \
             f"Average Jitter: {average_jitter:.2f} seconds"

    client_socket.send(result.encode())
    client_socket.close()

def start_server():
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.bind(('172.27.9.20', 8080))
    server.listen(5)
    print("[*] Listening on 172.27.9.20:8080")

    while True:
        client, addr = server.accept()
        print(f"[*] Accepted connection from: {addr[0]}:{addr[1]}")
        client_handler = threading.Thread(target=handle_client, args=(client,))
        client_handler.start()

if __name__ == "__main__":
    start_server()
