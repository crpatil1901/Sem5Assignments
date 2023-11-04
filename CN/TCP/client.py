import socket 
# Creating Client Socket 
if __name__ == '__main__': 
    host = '127.0.0.1'
    port = 8080
  
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM) 
    sock.connect((host, port)) 
  
    inputFilename = input('Input filename you want to send: ')
    outputFilename = input('Input filename you want to save as: ') + "\0"
    sock.send(outputFilename.encode('utf-8'))
    try: 
        # Reading file and sending data to server 
        fi = open(inputFilename, "rb") 
        data = fi.read()
        sock.send(inputFilename.encode('utf-8'))
        while data: 
            sock.send(data)
            data = fi.read() 
        # File is closed after data is sent 
        fi.close() 
    except IOError: 
        print('You entered an invalid filename! Please enter a valid name')