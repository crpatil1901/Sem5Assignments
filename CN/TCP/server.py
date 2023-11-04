import socket 
  
if __name__ == '__main__': 
    # Defining Socket 
    host = '127.0.0.1'
    port = 8080
    totalclient = int(input('Enter number of clients: ')) 
  
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM) 
    sock.bind((host, port)) 
    sock.listen(totalclient) 

    connections = [] 
    print('Initiating clients') 
    for i in range(totalclient): 
        conn = sock.accept() 
        connections.append(conn) 
        print('Connected with client', i+1) 
  
    for conn in connections:
        filename = ""
        char = ""
        i = 0
        while True:
            char = conn[0].recv(1).decode('utf-8')
            if char == "\0":
                break
            else:
                filename += char
                i += 1
        fo = open(filename, "wb")
        data = conn[0].recv(1024)[i-4:]
        while data:
            if not data: 
                break
            else: 
                fo.write(data)
                data = conn[0].recv(1024)
        print() 
        print('Receiving file from client') 
        print() 
        print('Received successfully! New filename is:', filename) 
        fo.close() 
    # Closing all Connections 
    for conn in connections: 
        conn[0].close() 