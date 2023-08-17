package Assignment2;

import java.util.*;

class Assembler {

    class Opcode {
        String type;
        int code;

        Opcode(String type, int code) {
            this.type = type;
            this.code = code;
        }
    }

    class Operand {
        String type;
        String value;

        Operand(String type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    class PoolTableEntry {
        int index;
        int length;

        PoolTableEntry(int index, int length) {
            this.index = index;
            this.length = length;
        }
    }

    class LiteralTableEntry {
        String value;
        int address;

        LiteralTableEntry(String value, int address) {
            this.value = value;
            this.address = address;
        }
    }

    class SymbolTableEntry {
        String symbol;
        int address;


        SymbolTableEntry(String symbol, int address) {
            this.symbol = symbol;
            this.address = address;

        }
    }

    int locationCounter;

    static HashMap<String, Opcode> mnemonicTable;
    List<PoolTableEntry> poolTable;
    List<LiteralTableEntry> literalTable;
    List<SymbolTableEntry> symbolTable;
    List<String> input;

    List<String> intermediateCode;

    private void initialize() {
        mnemonicTable.put("STOP", new Opcode("IS", 0));
        mnemonicTable.put("ADD", new Opcode("IS", 1));
        mnemonicTable.put("SUB", new Opcode("IS", 2));
        mnemonicTable.put("MULT", new Opcode("IS", 3));
        mnemonicTable.put("MOVER", new Opcode("IS", 4));
        mnemonicTable.put("MOVEM", new Opcode("IS", 5));
        mnemonicTable.put("COMP", new Opcode("IS", 6));
        mnemonicTable.put("BC", new Opcode("IS", 7));
        mnemonicTable.put("DIV", new Opcode("IS", 8));
        mnemonicTable.put("READ", new Opcode("IS", 9));
        mnemonicTable.put("PRINT", new Opcode("IS", 10));
        mnemonicTable.put("START", new Opcode("AD", 1));
        mnemonicTable.put("END", new Opcode("AD", 2));
        mnemonicTable.put("ORIGIN", new Opcode("AD", 3));
        mnemonicTable.put("EQU", new Opcode("AD", 4));
        mnemonicTable.put("LTORG", new Opcode("AD", 5));
        mnemonicTable.put("DC", new Opcode("DL", 1));
        mnemonicTable.put("DS", new Opcode("DL", 2));

        locationCounter = 0;
        poolTable.add(new PoolTableEntry(1, 0));

    }

    Assembler(List<String> input) {
        initialize();
        this.input = input;
    }

    void passOne() {
        for (String line : input) {
            String[] tokens = line.split(" ");
            String label, mnemonic, operand1Str, operand2Str;
            label = mnemonic = operand1Str = operand2Str = "";
            if (mnemonicTable.containsKey(tokens[0])) {
                mnemonic = tokens[0];
                if (tokens.length > 1) {
                    operand1 = tokens[1];
                }
                if (tokens.length > 2) {
                    operand2 = tokens[2];
                }
            } else {
                label = tokens[0];
                mnemonic = tokens[1];
                if (tokens.length > 2) {
                    operand1 = tokens[2];
                }
                if (tokens.length > 3) {
                    operand2 = tokens[3];
                }
            }
            if (label != "") {
                symbolTable.add(new SymbolTableEntry(label, locationCounter));
            }
            Opcode opcode = mnemonicTable.get(mnemonic);
            Operand operand1 = new Operand("", "");
            Operand operand2 = new Operand("", "");
            if (operand1Str != "") {
                if (operand1Str.charAt(0) == '=') {
                    literalTable.add(new LiteralTableEntry(operand1Str, -1));
                } else {
                    operand1 = new Operand("S", operand1Str);
                }
            }
            if (operand2Str != "") {
                if (operand2Str.charAt(0) == '=') {
                    literalTable.add(new LiteralTableEntry(operand2Str, -1));
                } else {
                    operand2 = new Operand("S", operand2Str);
                }
            }
            if (opcode.type == "IS") {
                locationCounter++;
            } else if (opcode.type == "AD") {
                if (opcode.code == 1) {
                    locationCounter = Integer.parseInt(operand1Str);
                } else if (opcode.code == 2) {
                    // poolTable.add(new PoolTableEntry(poolTable.get(poolTable.size() - 1).index + 1, 0));
                } else if (opcode.code == 3) {
                    // locationCounter = symbolTable.get(symbolTable.indexOf(new SymbolTableEntry(operand1, 0))).address;
                } else if (opcode.code == 4) {
                    symbolTable.add(new SymbolTableEntry(operand1Str, locationCounter));
                } else if (opcode.code == 5) {
                    for (int i = poolTable.get(poolTable.size() - 1).index; i < literalTable.size(); i++) {
                        literalTable.get(i).address = locationCounter;
                        locationCounter++;
                    }
                }
            } else if (opcode.type == "DL") {
                if (opcode.code == 1) {
                    locationCounter++;
                } else if (opcode.code == 2) {
                    locationCounter += Integer.parseInt(operand1Str);
                }
            }
            

        }
    }
}

public class Main {
    public static void main(String[] args) {
        List<String> input = Arrays.asList(
            "START 101",
            "READ N",
            "MOVER BREG ONE",
            "MOVEM BREG TERM",
            "AGAIN MULT BREG TERM",
            "MOVER CREG TERM",
            "ADD CREG ONE",
            "MOVEM CREG RESULT",
            "COMP CREG N",
            "BC LE AGAIN",
            "DIV BREG TWO",
            "MOVEM BREG RESULT",
            "PRINT RESULT",
            "STOP",
            "N DS 1",
            "RESULT DS 1",
            "ONE DC '1'",
            "TERM DS 1",
            "TWO DC '2'"
        );
    }
}