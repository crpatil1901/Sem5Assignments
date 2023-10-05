#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>

#define WINDOW_SIZE 4
#define PACKET_SIZE 1024

struct Packet {
    int seq_number;
    char data[PACKET_SIZE];
};

int main() {
    int sockfd;
    struct sockaddr_in server_addr, client_addr;
    socklen_t addr_len = sizeof(client_addr);
    int expected_seq_num = 0;

    sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    if (sockfd < 0) {
        perror("Socket creation failed");
        exit(1);
    }

    memset(&server_addr, 0, sizeof(server_addr));
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(8080);
    server_addr.sin_addr.s_addr = INADDR_ANY;

    if (bind(sockfd, (struct sockaddr*)&server_addr, sizeof(server_addr)) < 0) {
        perror("Binding failed");
        exit(1);
    }

    while (1) {
        struct Packet packet;
        int packet_received = recvfrom(sockfd, &packet, sizeof(packet), 0, (struct sockaddr*)&client_addr, &addr_len);

        if (packet_received != -1 && packet.seq_number == expected_seq_num) {
            printf("Received packet with sequence number: %d\n", packet.seq_number);
            
            sendto(sockfd, &packet, sizeof(packet), 0, (struct sockaddr*)&client_addr, addr_len);

            expected_seq_num++;
        } else {
            printf("Packet out of order or error. Expected: %d, Received: %d\n", expected_seq_num, packet.seq_number);
        }
    }

    close(sockfd);
    return 0;
}
