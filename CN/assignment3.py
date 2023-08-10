EVEN, ODD = True, False
MODE = EVEN

def encodeHamming(data, mode = MODE):
    data = data[::-1]
    m, r = len(data), 0
    while 2**r < m + r + 1 : r += 1
    bits = [0 for _ in range(m+r)]
    i = 0
    j = 0
    rPositions = set(map(lambda x: 2**x - 1, range(r)))
    while i < m:
        if j in rPositions: j += 1; continue
        bits[j] = data[i]; i += 1; j += 1
    # print(bits[::-1])
    for i in range(r):
        j, flag, cntr, parity = 2**i - 1, mode, 2**i, mode
        while j < m+r:
            if flag: parity = parity != bits[j]; # print(j+1, end=" ")
            cntr -= 1
            if cntr == 0: flag = not flag; cntr = 2**i
            j += 1
        bits[2**i - 1] = (parity == 1)
        # print("\n", bits[::-1], sep = "")
    return bits[::-1]

def decodeHamming(data, mode = MODE):
    data = data[::-1]
    r, n = 0, len(data)
    errorPosition = []
    while 2**r < n: r += 1
    for i in range(r):
        parity = mode
        j, cntr, flag = 2**i - 1, 2**i, mode
        while j < n:
            if flag: parity = parity != data[j]; # print(j+1, end=" ")
            cntr -= 1
            if cntr == 0: flag = not flag; cntr = 2**i
            j += 1
        # print()
        errorPosition.append(parity)
    if any(errorPosition):
        pos = 0
        for position in errorPosition:
            pos *= 2
            pos += 1 if position else 0
        pos -= 1
        if pos < n: data[pos] = data[pos] != data[pos]
    for i in range(r-1, -1, -1):
        data.pop(2**i - 1)
    return data[::-1]


def convertToBits(char):
    ascii = ord(char)
    ans = list(map(lambda x: x == '1', bin(ascii)[2:]))
    print(char, ascii, ans)
    return ans

def convertFromBits(data):
    num = 0
    for bit in data:
        num *= 2
        if bit: num += 1
        
    return chr(num)


# dat = [False, False, True, True, True, False, False]
# encoded = encodeHamming(dat)
# decoded = decodeHamming(encoded)
# print(decoded)

message = input("Enter Message: ")
print("\nString: ")
charAsBits = list(map(lambda x: convertToBits(x), message))
print("\nEncoded Hamming: ")
encodedString = list(map(lambda x: encodeHamming(x), charAsBits))
print(*encodedString, sep="\n")
recieved = list(map(lambda x: decodeHamming(x), encodedString))
print("\nRecieved Hamming: ")
print(*recieved, sep="\n")
decodedMessage = "".join(list(map(lambda x: convertFromBits(x), recieved)))
print("\nDecoded Message:", decodedMessage)