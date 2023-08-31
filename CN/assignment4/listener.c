
#include <arpa/inet.h>
#include <stdio.h>
#include <string.h>
#include <sys/socket.h>
#include <unistd.h>
#include <signal.h>
#include <stdlib.h>
#define PORT 8080

int client_fd;

void sigint_handler(int signum) {
    close(client_fd);
    printf("Shutting down gracefully...\n");
    exit(1);
}

int main(int argc, char const* argv[]) {
    signal(SIGINT, sigint_handler);
	int status, valread;
	struct sockaddr_in serv_addr;
	char* msg = "Hello from listener";
	char buffer[1024] = { 0 };
    size_t bufferSize = 1024;
	if ((client_fd = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
		printf("\n Socket creation error \n");
		return -1;
	}

	serv_addr.sin_family = AF_INET;
	serv_addr.sin_port = htons(PORT);

	if (inet_pton(AF_INET, "127.0.0.1", &serv_addr.sin_addr)
		<= 0) {
		printf(
			"\nInvalid address/ Address not supported \n");
		return -1;
	}

	if ((status
		= connect(client_fd, (struct sockaddr*)&serv_addr,
				sizeof(serv_addr)))
		< 0) {
		printf("\nConnection Failed \n");
		return -1;
	}

    while (1) {
        send(client_fd, msg, strlen(msg), 0);
        getline(&msg, &bufferSize, stdin);
    }

	close(client_fd);
	return 0;
}
