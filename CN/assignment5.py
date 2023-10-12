def calculate_subnet_details(ip_address, cidr):

    ip_address = list(map(lambda x: int(x), ip_address.split('.')))

    print(ip_address)

    completeSections = cidr // 8
    remainingBits = cidr % 8
    emptySections = 4 - completeSections - (1 if remainingBits > 0 else 0)

    subnet_mask = [255] * completeSections + [sum([2 ** (7 - i) for i in range(remainingBits)])] + [0] * emptySections

    print(subnet_mask)

    possible_connections = 2 ** (32 - cidr) - 2
    print(possible_connections)

    network_address = [ip_address[i] & subnet_mask[i] for i in range(4)]
    print(network_address)

    broadcast_address = [network_address[i] | (255 - subnet_mask[i]) for i in range(4)]
    print(broadcast_address)

    return {
        "Subnet Mask": ".".join(map(lambda x: str(x), subnet_mask)),
        "Number of Possible Connections": possible_connections,
        "First IP Address": ".".join(map(lambda x: str(x), network_address)),
        "Last IP Address": ".".join(map(lambda x: str(x), broadcast_address)),
    }

if __name__ == "__main__":
    ip_address = "192.168.1.34"
    cidr = 28
    subnet_details = calculate_subnet_details(ip_address, cidr)
    for key, value in subnet_details.items():
        print(f"{key}: {value}")