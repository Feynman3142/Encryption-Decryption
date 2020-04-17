import java.io.*;

public class Main { // class to handle user input from command line
    /**
     * Method to handle user input from the command line
     * @param args -mode to select en(de)cryption, -key to specify shift value, -data to specify input to
     *             be en(de)crypted, -in to specify path to input file, -out to specify path to output file
     *             -algo to specify algorithm to use for en(de)cryption
     * @throws IOException throws IOException
     */
    public static void main(String[] args) throws IOException {
        // string to check if message should be en(de)crypted
        String op = "enc";
        // 'input' either holds path to input file or the actual data for en(de)cryption
        String input = "";
        // holds the shift value
        int shift = 0;
        // check if path to input file is specified
        boolean isFile = false;
        // check if we got actual data or path to input file (it is possible we may receive neither!)
        boolean gotData = false;
        // holds path to output file
        String outPath = null;
        // holds algorithm selected
        String algo = "shift";
        for (int ind = 0; ind < args.length; ++ind) {
            if ("-mode".equals(args[ind])) {
                op = args[ind + 1].toLowerCase();
                if (!"enc".equals(op) && !"dec".equals(op)) {
                    throw new IllegalArgumentException(String.format("Invalid operation: %s", args[ind + 1]));
                }
            } else if ("-key".equals(args[ind])) {
                try {
                    shift = Integer.parseInt(args[ind + 1]);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException(String.format("Invalid key value: %s", args[ind + 1]));
                }
            } else if ("-data".equals(args[ind])) {
                input = args[ind + 1];
                gotData = true;
            } else if (("-in".equals(args[ind])) && !gotData) {
                input = args[ind + 1];
                isFile = true;
            } else if ("-out".equals(args[ind])) {
                outPath = args[ind + 1];
            } else if ("-algo".equals(args[ind])) {
                algo = args[ind + 1].toLowerCase();
                if (!"shift".equals(algo) && !"unicode".equals(algo)) { // only supports caesar cipher and extended unicode version
                    throw new IllegalArgumentException(String.format("Invalid operation: %s", args[ind + 1]));
                }
            }
        }

        Config cfg = new Config();
        cfg.setKey(shift);
        Algorithm cryptoSys = SysFactory.createSystem(algo, cfg);

        boolean opIsEnc = "enc".equals(op);
        boolean outPathExists = outPath != null;

        // decrypt data given on command line and write to System.out
        if (!opIsEnc && !isFile && !outPathExists) {
            String decMsg = cryptoSys.decryptMsg(input);
            System.out.println(decMsg);
        // decrypt data given on command line and write to output file
        } else if (!opIsEnc && !isFile && outPathExists) {
            String decMsg = cryptoSys.decryptMsg(input);
            try (PrintWriter writer = new PrintWriter(outPath)) {
                writer.print(decMsg);
            } catch (FileNotFoundException e) {
                System.out.println("Error: Output file not found");
            }
        // decrypt data from input file and write to System.out
        } else if (!opIsEnc && isFile && !outPathExists) {
            cryptoSys.decryptFile(input);
        // decrypt data from input file and write to output file
        } else if (!opIsEnc && isFile && outPathExists) {
            cryptoSys.decryptFile(input, outPath);
        // encrypt data given on command line and write to System.out
        } else if (opIsEnc && !isFile && !outPathExists) {
            String encMsg = cryptoSys.encryptMsg(input);
            System.out.println(encMsg);
        // encrypt data given on command line and write to output file
        } else if (opIsEnc && !isFile && outPathExists) {
            String encMsg = cryptoSys.encryptMsg(input);
            try (PrintWriter writer = new PrintWriter(outPath)) {
                writer.print(encMsg);
            } catch (FileNotFoundException e) {
                System.out.println("Error: Output file not found");
            }
        // encrypt data from input file and write to System.out
        } else if (opIsEnc && isFile && !outPathExists) {
            cryptoSys.encryptFile(input);
        // encrypt data from input file and write to output file
        } else if (opIsEnc && isFile && outPathExists) {
            cryptoSys.encryptFile(input, outPath);
        }
    }
}