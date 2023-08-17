#!/usr/bin/env python3

from random import randint

EVEN, ODD = False, True
MODE = ODD

xor = lambda a, b: a != b

stringOf = lambda x: "".join(list(map(lambda y: '1 ' if y else '0 ', x.copy())))

def encodeHamming(data, mode = MODE):
    data = data[::-1]
    m, r = len(data), 0
    while 2**r < m + r + 1 : r += 1
    print("No. of redundant bits: ", r)
    bits = [0 for _ in range(m+r)]
    i = 0
    j = 0
    rPositions = set(map(lambda x: 2**x - 1, range(r)))
    while i < m:
        if j in rPositions: j += 1; continue
        bits[j] = data[i]; i += 1; j += 1
    # print(bits[::-1])
    for i in range(r):
        j, flag, cntr, parity = 2**i - 1, True, 2**i, mode
        while j < m+r:
            if flag: parity = xor(parity, bits[j]); # print(j+1, end=" ")
            cntr -= 1
            if cntr == 0: flag = not flag; cntr = 2**i
            j += 1
        bits[2**i - 1] = (parity)
        # print("\n", bits[::-1], sep = "")
    return bits[::-1]

def decodeHamming(data, mode = MODE):
    data = data[::-1]
    r, n = 0, len(data)
    errorPosition = []
    while 2**r < n: r += 1
    for i in range(r):
        parity = mode
        j, cntr, flag = 2**i - 1, 2**i, True
        while j < n:
            if flag: parity = xor(parity, data[j])
            cntr -= 1
            if cntr == 0: flag = not flag; cntr = 2**i
            j += 1
        errorPosition.append(parity)
    errorPosition = errorPosition[::-1]
    if any(errorPosition):
        pos = 0
        for position in errorPosition:
            pos *= 2
            pos += 1 if position else 0
        if pos <= n: data[pos-1] = not data[pos-1]
        print("Corrected error at position", n-pos, "in packet", stringOf(data))
    for i in range(r-1, -1, -1):
        data.pop(2**i - 1)
    return data[::-1]

def convertToBits(char):
    ascii = ord(char)
    ans = list(map(lambda x: x == '1', bin(ascii)[2:]))
    print(char, ascii, "\t", stringOf(ans))
    return ans

def convertFromBits(data):
    num = 0
    for bit in data:
        num *= 2
        if bit: num += 1
        
    return chr(num)

while True:
    i = input("Enter 1 for EVEN parity, 0 for ODD parity: ")
    if i == '1':
        MODE = EVEN; break
    elif i == '0':
        MODE = ODD; break
    else:
        print("Invalid Input.")

if __name__ == "__main__":
    message = input("Enter Message: ")
    print("\nString: ")

    charAsBits = list(map(lambda x: convertToBits(x), message))
    print("\nEncoded Hamming: ")

    encodedString = list(map(lambda x: encodeHamming(x), charAsBits))
    print(*list(map(lambda x: stringOf(x), encodedString)), sep="\n")

    e = input("Do you want to induce error? (Y/any): ")

    if e == 'y' or e == 'Y':
        char = randint(0, len(message) - 1)
        flippedBit = randint(0, len(encodedString[char]) - 1)
        print("Flipping bit", flippedBit+1, "of character", message[char])
        encodedString[char][flippedBit] = not encodedString[char][flippedBit]
    print("\nRecieved Hamming: ")
    print(*list(map(lambda x: stringOf(x), encodedString)), sep="\n")

    recieved = list(map(lambda x: decodeHamming(x), encodedString))
    print("\nRecieved Message: ")

    print(*list(map(lambda x: stringOf(x), recieved)), sep="\n")
    decodedMessage = "".join(list(map(lambda x: convertFromBits(x), recieved)))
    
    print("\nDecoded Message:", decodedMessage)