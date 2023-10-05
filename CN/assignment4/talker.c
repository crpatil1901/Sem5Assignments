#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <time.h>

#define WINDOW_SIZE 4
#define PACKET_SIZE 1024

// Packet structure
struct Packet {
    int seq_number;
    char data[PACKET_SIZE];
};

int main() {
    int sockfd;
    struct sockaddr_in server_addr;
    socklen_t addr_len = sizeof(server_addr);
    int base = 0;
    int next_seq_num = 0;
    int window_size = WINDOW_SIZE;
    int total_packets = 10; // Total number of packets to send

    // Create a UDP socket
    sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    if (sockfd < 0) {
        perror("Socket creation failed");
        exit(1);
    }

    // Initialize server address and port
    memset(&server_addr, 0, sizeof(server_addr));
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(8080);
    server_addr.sin_addr.s_addr = INADDR_ANY;

    // Sender logic (Go-Back-N)
    while (base < total_packets) {
        while (next_seq_num < base + window_size && next_seq_num < total_packets) {
            struct Packet packet;
            packet.seq_number = next_seq_num;
            // Create and send the packet
            // Fill packet.data with the actual data to send
            sendto(sockfd, &packet, sizeof(packet), 0, (struct sockaddr*)&server_addr, addr_len);
            printf("Sent packet with sequence number: %d\n", next_seq_num);
            next_seq_num++;
        }

        // Receive acknowledgments and update the base
        struct Packet ack;
        int ack_received = recvfrom(sockfd, &ack, sizeof(ack), 0, NULL, NULL);
        if (ack_received != -1 && ack.seq_number >= base) {
            base = ack.seq_number + 1;
            printf("Received acknowledgment for sequence number: %d\n", ack.seq_number);
        } else {
            printf("Acknowledgment error. Resending packets...\n");
            next_seq_num = base; // Go back to the base
        }
    }

    close(sockfd);
    return 0;
}
