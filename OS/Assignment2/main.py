mnemonicTable = {
    'STOP': ('IS', 0),
    'ADD': ('IS', 1),
    'SUB': ('IS', 2),
    'MULT': ('IS', 3),
    'MOVER': ('IS', 4),
    'MOVEM': ('IS', 5),
    'COMP': ('IS', 6),
    'BC': ('IS', 7),
    'DIV': ('IS', 8),
    'READ': ('IS', 9),
    'PRINT': ('IS', 10),
    'START': ('AD', 1),
    'END': ('AD', 2),
    'ORIGIN': ('AD', 3),
    'EQU': ('AD', 4),
    'LTORG': ('AD', 5),
    'DC': ('DL', 1),
    'DS': ('DL', 2)
}

registers = {
    'AREG': tuple([1]),
    'BREG': tuple([2]),
    'CREG': tuple([3]),
    'DREG': tuple([4])
}

conditionTable = {
    'LT': tuple([1]),
    'LE': tuple([2]),
    'EQ': tuple([3]),
    'GT': tuple([4]),
    'GE': tuple([5]),
    'ANY': tuple([6])
}

input = """
START 101
READ N
MOVER BREG ONE
MOVEM BREG TERM
AGAIN MULT BREG TERM
MOVER CREG TERM
ADD CREG ONE
MOVEM CREG RESULT
COMP CREG N
BC LE AGAIN
DIV BREG TWO
MOVEM BREG RESULT
PRINT RESULT
STOP
N DS 1
RESULT DS 1
ONE DC '1'
TERM DS 1
TWO DC '2'
"""

symbolTable = dict()
literalTable = dict()
poolTable = dict()

def getIntermediateCodePass1(input):

    global mnemonicTable
    global registers
    global symbolTable
    global literalTable
    global poolTable
    global conditionTable

    instructions = list(map(lambda x: x.split(), input.strip().split("\n")))

    lc = 0
    intermediateCode = []

    def parseOperand(operand):
        
        if operand in registers:
            return registers[operand]
        elif operand in conditionTable:
            return conditionTable[operand]
        elif operand.isnumeric():
            return ('C', int(operand))
        elif operand[0] == "'":
            return ('C', operand)
        else:
            sIndex = 0
            for i in symbolTable.keys():
                if symbolTable[i][0] == operand:
                    sIndex = i
                    break
            else:
                sIndex = len(symbolTable) + 1
            # print(symbolTable[sIndex])
            # if not symbolTable[sIndex]:
            if not (sIndex in symbolTable and symbolTable[sIndex][1] != None):
                symbolTable[sIndex] = [operand, None]
            # print(symbolTable)
            return ('S', sIndex)

    for instruction in instructions:
        # try:
            currentLine = []

            label = None if instruction[0] in mnemonicTable else instruction[0]
            
            assemblyOperand = instruction[0] if label == None else instruction[1]
            correspondingOpcode = mnemonicTable[assemblyOperand]
            
            operand1 = None
            operand1 = None
            
            if correspondingOpcode[0] != "DL":
                operand1 = None if len(instruction) < (3 if label else 2) else parseOperand(instruction[(2 if label else 1)])
                operand2 = None if len(instruction) < (4 if label else 3) else parseOperand(instruction[(3 if label else 2)])
            else:
                operand1 = None if len(instruction) < (3 if label else 2) else (instruction[(2 if label else 1)])
                operand2 = None if len(instruction) < (4 if label else 3) else (instruction[(3 if label else 2)])
            
            if label and correspondingOpcode[0] != 'DL':
                sIndex = len(symbolTable) + 1
                symbolTable[sIndex] = [label, lc]

            currentLine.append(lc)
            if correspondingOpcode[0] == 'AD':
                if correspondingOpcode[1] == 1:
                    if operand1:
                        lc = operand1[1]
                        lc -= 1
                    else:
                        raise Exception("Invalid Operands")
                else:
                    pass
            if correspondingOpcode[0] == 'DL':
                if correspondingOpcode[1] == 1:
                    for key in symbolTable:
                        if symbolTable[key][0] == label:
                            symbolTable[key][1] = lc
                            break
                if correspondingOpcode[1] == 2:
                    for key in symbolTable:
                        if symbolTable[key][0] == label:
                            symbolTable[key][1] = lc
                            break
                    lc += int(operand1) - 1
            currentLine.append(correspondingOpcode)
            if operand1:
                currentLine.append(operand1)
            if operand2:
                currentLine.append(operand2)
            intermediateCode.append(currentLine)
            lc += 1
    print(*intermediateCode, sep="\n")
    print()
    for key in symbolTable:
        print(key, symbolTable[key])
    return intermediateCode

def getIntermediateCodePass2(pass1):
    global symbolTable
    op = []
    for instruction in pass1:
        line = []
        line.append(instruction[0])
        for i in range(1, len(instruction)):
            line.append(instruction[i][-1])
        op.append(line)
    return op

print()
print(*getIntermediateCodePass2(getIntermediateCodePass1(input)), sep="\n")
