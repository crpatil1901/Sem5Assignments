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

        @Override
        public String toString() {
            return type + "," + code;
        }
    }

    class Operand {
        String type;
        String value;

        Operand(String type, String value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return type + "," + value;
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

    static HashMap<String, Opcode> mnemonicTable = new HashMap<>();
    static HashMap<String, Integer> registerTable = new HashMap<>();
    static HashMap<String, Integer> relationalOperatorTable = new HashMap<>();
    List<PoolTableEntry> poolTable = new ArrayList<>();
    List<LiteralTableEntry> literalTable = new ArrayList<>();
    List<SymbolTableEntry> symbolTable = new ArrayList<>();
    List<String> input;

    List<String> intermediateCode = new ArrayList<>();

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

        registerTable.put("AREG", 1);
        registerTable.put("BREG", 2);
        registerTable.put("CREG", 3);
        registerTable.put("DREG", 4);

        relationalOperatorTable.put("LT", 1);
        relationalOperatorTable.put("LE", 2);
        relationalOperatorTable.put("EQ", 3);
        relationalOperatorTable.put("GT", 4);
        relationalOperatorTable.put("GE", 5);
        relationalOperatorTable.put("ANY", 6);

        locationCounter = 0;
        poolTable.add(new PoolTableEntry(0, 0));

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
                    operand1Str = tokens[1];
                }
                if (tokens.length > 2) {
                    operand2Str = tokens[2];
                }
            } else {
                label = tokens[0];
                mnemonic = tokens[1];
                if (tokens.length > 2) {
                    operand1Str = tokens[2];
                }
                if (tokens.length > 3) {
                    operand2Str = tokens[3];
                }
            }

            Opcode opcode = mnemonicTable.get(mnemonic);
            if (opcode == null) {
                System.out.println("Error: Invalid mnemonic" + mnemonic);
                continue;
            }
            if (label != "") {
                if (opcode.type == "DL") {
                    int index = -1;
                    for (int i = 0; i < symbolTable.size(); i++) {
                        if (symbolTable.get(i).symbol.equals(label)) {
                            index = i;
                            break;
                        }
                    }
                    if (index == -1) {
                        symbolTable.add(new SymbolTableEntry(label, locationCounter+1));
                    } else {
                        symbolTable.get(index).address = locationCounter+1;
                    }
                } else {
                    symbolTable.add(new SymbolTableEntry(label, locationCounter+1));
                }
            }

            Operand operand1;
            Operand operand2;

            if (operand1Str != "") {
                if (registerTable.containsKey(operand1Str)) {
                    operand1 = new Operand("", Integer.toString(registerTable.get(operand1Str)));
                } else if (relationalOperatorTable.containsKey(operand1Str)) {
                    operand1 = new Operand("", Integer.toString(relationalOperatorTable.get(operand1Str)));
                } else if (operand1Str.charAt(0) == '=') {
                    operand1Str = operand1Str.substring(1);
                    literalTable.add(new LiteralTableEntry(operand1Str, locationCounter));
                    operand1 = new Operand("C", operand1Str);
                } else {
                    int index = -1;
                    for (int i = 0; i < symbolTable.size(); i++) {
                        if (symbolTable.get(i).symbol.equals(operand1Str)) {
                            index = i;
                            break;
                        }
                    }
                    if (index == -1) {
                        symbolTable.add(new SymbolTableEntry(operand1Str, -1));
                        index = symbolTable.size() - 1;
                    }
                    operand1 = new Operand("S", index+1 + "");
                }
            } else {
                operand1 = null;
            }
            if (operand2Str != "") {
                if (registerTable.containsKey(operand2Str)) {
                    operand2 = new Operand("R", Integer.toString(registerTable.get(operand2Str)));
                } else if (relationalOperatorTable.containsKey(operand2Str)) {
                    operand2 = new Operand("", Integer.toString(relationalOperatorTable.get(operand2Str)));
                } else if (operand2Str.charAt(0) == '=') {
                    operand2Str = operand2Str.substring(1);
                    literalTable.add(new LiteralTableEntry(operand2Str, locationCounter));
                    operand2 = new Operand("C", operand2Str);
                } else {
                    String operand2operator = null;
                    String operand2oprnd = null;
                    if (operand2Str.contains("+")) {
                        operand2operator = "+";
                        operand2oprnd = operand2Str.split("+")[1];
                        operand2Str = operand2Str.split("+")[0];
                    } else if (operand2Str.contains("-")) {
                        operand2operator = "-";
                        operand2oprnd = operand2Str.split("-")[1];
                        operand2Str = operand2Str.split("-")[0];
                    }
                    int index = -1;
                    for (int i = 0; i < symbolTable.size(); i++) {
                        if (symbolTable.get(i).symbol.equals(operand2Str)) {
                            index = i;
                            break;
                        }
                    }
                    if (index == -1) {
                        symbolTable.add(new SymbolTableEntry(operand2Str, -1));
                        index = symbolTable.size() - 1;
                    }
                    operand2 = new Operand("S", index+1 + "");
                }
            } else {
                operand2 = null;
            }
            if (opcode.type == "IS") {
                locationCounter++;
            } else if (opcode.type == "AD") {
                if (opcode.code == 1) { // START
                    if (operand1 != null) {
                        locationCounter = Integer.parseInt(operand1.value);
                    } else {
                        System.out.println("Error: START must have an operand");
                    }
                } else if (opcode.code == 2) { // END
                    int start = poolTable.get(poolTable.size() - 1).index;
                    int end = literalTable.size();
                    poolTable.get(poolTable.size() - 1).length = end - start;
                    for (int i = poolTable.get(poolTable.size() - 1).index; i < literalTable.size(); i++) {
                        literalTable.get(i).address = locationCounter;
                        locationCounter++;
                    }
                } else if (opcode.code == 3) { // ORIGIN
                    if (operand1 != null) {
                        if (operand1.type == "C") {
                            locationCounter = Integer.parseInt(operand1.value);
                        } else if (operand1.type == "S") {
                            locationCounter = symbolTable.get(Integer.parseInt(operand1.value)).address;
                        }
                    } else {
                        System.out.println("Error: ORIGIN must have an operand");
                    }
                } else if (opcode.code == 4) { // EQU
                    if (operand1 != null) {
                        if (operand1.type == "C") {
                            literalTable.add(new LiteralTableEntry(operand1.value, locationCounter));
                        } else if (operand1.type == "S") {
                            symbolTable.add(new SymbolTableEntry(label, symbolTable.get(Integer.parseInt(operand1.value)).address));
                        }
                    } else {
                        System.out.println("Error: EQU must have an operand");
                    }
                } else if (opcode.code == 5) { // LTORG
                    int start = poolTable.get(poolTable.size() - 1).index;
                    int end = literalTable.size();
                    poolTable.get(poolTable.size() - 1).length = end - start;
                    for (int i = poolTable.get(poolTable.size() - 1).index; i < literalTable.size(); i++) {
                        literalTable.get(i).address = locationCounter;
                        locationCounter++;
                    }
                    poolTable.add(new PoolTableEntry(literalTable.size(), 0));
                }
            } else if (opcode.type == "DL") {
                if (opcode.code == 1) {
                    locationCounter++;
                } else if (opcode.code == 2) {
                    locationCounter += Integer.parseInt(operand1Str);
                }
            }
            
            String IC = ( opcode.type != "AD" ? locationCounter : "___") + "\t" + opcode.toString() + "\t" + (operand1 != null ? operand1.toString() : "") + "\t" + (operand2 != null ? operand2.toString() : "");
            if (opcode.type == "AD") {
                locationCounter--;
            }
            intermediateCode.add(IC);
        }
    }

    void passTwo() {
        for (String line : intermediateCode) {
            String[] tokens = line.split("\t");
            String location = tokens[0];
            String opcode = tokens[1].split(",")[1];
            String operand1 = tokens.length > 2 ? tokens[2].split(",")[1] : "";
            String operand2 = tokens.length > 3 ? tokens[3].split(",")[1] : "";
            String machineCode = location + "\t" + opcode + "\t" + operand1 + "\t" + operand2;

            System.out.println(machineCode);

        }
    }
}

public class Main {
    public static void main(String[] args) {
        List<String> input = Arrays.asList(
            "START =101",
            "READ N",
            "MOVER BREG ONE",
            "MOVEM BREG TERM",
            "AGAIN MULT BREG TERM",
            "MOVER CREG TERM",
            "ADD CREG ONE",
            "MOVEM CREG TERM",
            "COMP CREG N",
            "BC LE AGAIN",
            "DIV BREG TWO",
            "MOVEM BREG RESULT",
            "PRINT RESULT",
            "STOP",
            "N DS =1",
            "RESULT DS =1",
            "ONE DC =1",
            "TERM DS =1",
            "TWO DC =2",
            "END"
        );
        Assembler assembler = new Assembler(input);
        assembler.passOne();
        assembler.passTwo();
        System.out.println("\nPool Table");
        for (Assembler.PoolTableEntry entry : assembler.poolTable) {
            System.out.println(entry.index + " " + entry.length);
        }
        System.out.println("\nLiteral Table");
        for (Assembler.LiteralTableEntry entry : assembler.literalTable) {
            System.out.println(entry.value + " " + entry.address);
        }
        System.out.println("\nSymbol Table");
        for (Assembler.SymbolTableEntry entry : assembler.symbolTable) {
            System.out.println(entry.symbol + " " + entry.address);
        }
        System.out.println("\nIntermediate Code");
        for (String line : assembler.intermediateCode) {
            System.out.println(line);
        }
    }
}