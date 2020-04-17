import java.io.*;

abstract class Algorithm {

    /**
     * Encryption method that reads from a file and prints to an output stream (file or System.out)
     * @param reader BufferedReader object to read from a file
     * @param writer PrintWriter to read to a file or System.out
     * @throws IOException throws IOException
     */
    private void encryptFile(BufferedReader reader, PrintWriter writer) throws IOException {
        while (reader.ready()) {
            String msg = reader.readLine();
            String encMsg = encryptMsg(msg);
            writer.println(encMsg);
        }
    }

    /**
     * Wrapper encryption method that prints to System.out
     * @param path path to the input file to read from
     * @throws IOException throws IOException
     */
    void encryptFile(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            PrintWriter writer = new PrintWriter(System.out);
            encryptFile(reader, writer);
        } catch (FileNotFoundException e) {
            System.out.println("Error: Input file not found");
        }
    }

    /**
     * Wrapper encryption method that writes to a file
     * @param inPath path to the input file to read from
     * @param outPath path to the output file to write to
     * @throws IOException throws IOException
     */
    void encryptFile(String inPath, String outPath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(inPath))) {
            try (PrintWriter writer = new PrintWriter(outPath)) {
                encryptFile(reader, writer);
            } catch (FileNotFoundException e) {
                System.out.println("Error: Output file not found");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: Input file not found");
        }
    }

    /**
     * Decryption method that reads from a file and prints to an output stream (file or System.out)
     * @param reader BufferedReader object to read from a file
     * @param writer PrintWriter to read to a file or System.out
     * @throws IOException throws IOException
     */
    private void decryptFile(BufferedReader reader, PrintWriter writer) throws IOException {
        while (reader.ready()) {
            String msg = reader.readLine();
            String decMsg = decryptMsg(msg);
            writer.println(decMsg);
        }
    }

    /**
     * Wrapper decryption method that prints to System.out
     * @param path path to the input file to read from
     * @throws IOException throws IOException
     */
    void decryptFile(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            PrintWriter writer = new PrintWriter(System.out);
            decryptFile(reader, writer);
        } catch (FileNotFoundException e) {
            System.out.println("Error: Input file not found");
        }
    }

    /**
     * Wrapper encryption method that writes to a file
     * @param inPath path to the input file to read from
     * @param outPath path to the output file to write to
     * @throws IOException throws IOException
     */
    void decryptFile(String inPath, String outPath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(inPath))) {
            try (PrintWriter writer = new PrintWriter(outPath)) {
                decryptFile(reader, writer);
            } catch (FileNotFoundException e) {
                System.out.println("Error: Output file not found");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: Input file not found");
        }
    }

    /**
     * Abstract method allowing different methods for encryption
     * @param msg String to be encrypted
     * @return Encrypted string
     */
    public abstract String encryptMsg(String msg);

    /**
     * Abstract method allowing different methods for decryption
     * @param msg String to be decrypted
     * @return Decrypted string
     */
    public abstract String decryptMsg(String msg);
}

class Config { // class to store configurations for different algorithms

    private int key;

    /**
     * Sets the key value for shift ciphers
     * @param key the shift value which must be >= 0 else IllegalArgumentException is raised
     */
    void setKey(int key) {
        if (key < 0) {
            throw new IllegalArgumentException(String.format("Error: key (%d) < 0\n", key));
        } else {
            this.key = key;
        }
    }

    /**
     * Returns the key value for shift ciphers
     * @return the key (integer)
     */
    int getKey() {
        return this.key;
    }
}

class SysFactory { // static factory class to create instances of algorithm classes

    /**
     * static method to create instances of algorithm classes
     * @param sys String indicating type of algorithm selected
     * @param cfg Config object storing parameters for algorithm
     * @return instance of class of selected algorithm
     */
    static Algorithm createSystem(String sys, Config cfg) {
        switch (sys.toLowerCase()) {
            case "shift":
                return new CaesarCipher(cfg.getKey());
            case "unicode":
                return new UnicodeCipher(cfg.getKey());
            default:
                throw new IllegalArgumentException(String.format("Invalid algorithm %s\n", sys));
        }
    }
}

class CaesarCipher extends Algorithm { // class that implements Caesar Cipher: encodes only letters of the English alphabet

    private int key;

    /**
     * Constructor for CaesarCipher class
     * @param key shift value for encryption/decryption
     */
    CaesarCipher(int key) {
        this.key = key;
    }

    /**
     * Overrides Algorithm method that shifts English letters to encrypt them
     * @param msg String to be encrypted
     * @return Encrypted string
     */
    @Override
    public String encryptMsg(String msg) {
        if (msg.length() == 0) {
            return msg;
        } else {
            char[] encMsg = new char[msg.length()];
            for (int ind = 0; ind < msg.length(); ++ind) {
                char letter = msg.charAt(ind);
                if (letter >= 'a' && letter <= 'z') {
                    encMsg[ind] = (char) ('a' + ((letter - 'a' + key) % 26));
                } else if (letter >= 'A' && letter <= 'Z') {
                    encMsg[ind] = (char) ('A' + ((letter - 'A' + key) % 26));
                } else {
                    encMsg[ind] = letter;
                }
            }
            return new String(encMsg);
        }
    }

    /**
     * Overrides Algorithm method that performs inverse of encryptMsg() method
     * @param msg String to be decrypted
     * @return Decrypted string
     */
    @Override
    public String decryptMsg(String msg) {
        if (msg.length() == 0) {
            return msg;
        } else {
            char[] decMsg = new char[msg.length()];
            for (int ind = 0; ind < msg.length(); ++ind) {
                char letter = msg.charAt(ind);
                if (letter >= 'a' && letter <= 'z') {
                    int temp = letter - 'a' - key;
                    decMsg[ind] = (char) ('a' + (char) ((temp < 0) ? temp + 26 : temp));
                } else if (letter >= 'A' && letter <= 'Z') {
                    int temp = letter - 'A' - key;
                    decMsg[ind] = (char) ('A' + (char) ((temp < 0) ? temp + 26 : temp));
                } else {
                    decMsg[ind] = letter;
                }
            }
            return new String(decMsg);
        }
    }
}

class UnicodeCipher extends Algorithm { // class that implements Caesar Cipher but shifts any Unicode character

    private int key;

    /**
     * Constructor for UnicodeCipher
     * @param key shift value for encryption/decryption
     */
    UnicodeCipher(int key) {
        this.key = key;
    }

    /**
     * Overrides Algorithm method to shift Unicode characters to encrypt them
     * @param msg String to be encrypted
     * @return Encrypted string
     */
    @Override
    public String encryptMsg(String msg) {
        if (msg.length() == 0) {
            return msg;
        } else {
            char[] encMsg = new char[msg.length()];
            for (int ind = 0; ind < msg.length(); ++ind) {
                char letter = msg.charAt(ind);
                encMsg[ind] = (char) ((letter + key) % 65536);
            }
            return new String(encMsg);
        }
    }

    /**
     * Overrides Algorithm method to perform inverse of encryptMsg() method
     * @param msg String to be decrypted
     * @return Decrypted string
     */
    @Override
    public String decryptMsg(String msg) {
        if (msg.length() == 0) {
            return msg;
        } else {
            char[] decMsg = new char[msg.length()];
            int actKey = key % 65536;
            for (int ind = 0; ind < msg.length(); ++ind) {
                char letter = msg.charAt(ind);
                decMsg[ind] = (char) ((letter - actKey < 0) ? letter - actKey + 65536 : letter - actKey);
            }
            return new String(decMsg);
        }
    }
}
