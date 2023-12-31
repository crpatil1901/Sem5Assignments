import socket
import time
import sys

UDP_IP = "127.0.0.1"
UDP_PORT = 5005
buf = 1024
file_name = input("Enter file name: ").strip()


sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.sendto(file_name.encode(), (UDP_IP, UDP_PORT))
print ("Sending %s ..." % file_name)
f = open(file_name, "rb")

data = f.read(buf)
while(data):
    if(sock.sendto(data, (UDP_IP, UDP_PORT))):
        data = f.read(buf)
        time.sleep(0.001)

sock.close()
f.close()