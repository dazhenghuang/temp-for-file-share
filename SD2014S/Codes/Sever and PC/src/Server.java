import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
    // Example User
    private String user = "michael";
    /*
     * private ArrayList<Integer> charOrder = new ArrayList<Integer>(
     * Arrays.asList(4, 5, 6, 7));
     */

    // Constants
    private static final int PORT = 1040;

    // Connection variables
    private int port;
    private ServerSocket listenSocket;
    private BufferedReader pcReader;
    private BufferedReader androidReader;
    private PrintWriter pcWriter;
    private PrintWriter androidWriter;
    private boolean pcDone = false;
    private boolean androidDone = false;

    // Authentication Variables
    private String username;
    private ArrayList<Integer> inputOrdering;
    private ArrayList<String> pwCharacters;
    private ArrayList<String> authPasswordBufferPC;
    private ArrayList<String> authPasswordBufferAndroid;
    private ArrayList<String> nextPasswordBufferPC;
    private ArrayList<String> nextPasswordBufferAndroid;
    private static ArrayList<String> pwPermutationsPC = new ArrayList<String>();
    private static ArrayList<String> pwPermutationsAndroid = new ArrayList<String>();

    public static void main(String[] args) {
        int connections = 0;
        Server server = new Server(PORT);

        try {
            // Change this to two later
            while(connections < 2) {
                server.establishConnections(connections);
                connections++;
            }
            server.listen();
            // server.shutDown();
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Server(int port) {
        this.port = port;
        try {
            this.listenSocket = new ServerSocket(PORT);
        }
        catch(Exception e) {
            System.out.println(e.toString());
        }

        inputOrdering = new ArrayList<Integer>();
        pwCharacters = new ArrayList<String>();
        authPasswordBufferPC = new ArrayList<String>();
        authPasswordBufferAndroid = new ArrayList<String>();
        nextPasswordBufferPC = new ArrayList<String>();
        nextPasswordBufferAndroid = new ArrayList<String>();

        try {
            getChallengePassword();
            buildPasswordPermutations(pwCharacters, pwPermutationsPC);
        }
        catch(Exception e) {
            System.out.println(e.toString());
        }
    }

    public void establishConnections(int connectionNumber) throws IOException {
        Socket client = listenSocket.accept();
        if(connectionNumber == 0) {
            pcReader = new BufferedReader(new InputStreamReader(
                    client.getInputStream()), 1000);
            pcWriter = new PrintWriter(client.getOutputStream(), true);
        }
        else {
            androidReader = new BufferedReader(new InputStreamReader(
                    client.getInputStream()), 1000);
            androidWriter = new PrintWriter(client.getOutputStream(), true);
        }
    }

    private void processInput(BufferedReader reader, PrintWriter writer,
            ArrayList<String> authPasswordBuffer,
            ArrayList<String> nextPasswordBuffer, boolean isAndroid)
            throws IOException {
        String input = reader.readLine();
        if(input == null) {
            return;
        }
        String[] params = input.split(" ");
        System.out.println(input);
        if(params[0].equals("PWCHAR")) {
            if(params[1].equals("AUTH")) {
                authPasswordBuffer.add(params[2] + " " + params[3] + " "
                        + params[4]);
            }
            else if(params[1].equals("NEXT")) {
                nextPasswordBuffer.add(params[2] + " " + params[3] + " "
                        + params[4]);
            }
        }
        else if(params[0].equals("CLEAR")) {
            authPasswordBuffer.clear();
            nextPasswordBuffer.clear();
        }
        else if(params[0].equals("USERNAME")) {
            username = params[1];
            pcDone = true;
        }
        else if(params[0].equals("SUBMIT")) {
            androidDone = true;
/*            boolean valid = authenticate();
            if(valid) {
                pcWriter.println("SUCCESS");
                androidWriter.println("SUCCESS");
                System.out.println("SUCCESS");
            }
            else {
                writer.println("FAILURE");
                authPasswordBufferPC.clear();
                authPasswordBufferAndroid.clear();
                nextPasswordBufferPC.clear();
                nextPasswordBufferAndroid.clear();
                username = "";
                System.out.println("FAILURE");
            }*/
        }
    }

    private void listen() {
        boolean working = true;
        boolean finished = false;
        while(working) {
            if(pcDone && androidDone && !finished) {
                boolean valid = authenticate();
                if(valid) {
                    pcWriter.println("SUCCESS");
                    androidWriter.println("SUCCESS");
                    System.out.println("SUCCESS");
                    finished = true;
                }
                else {
                    pcWriter.println("FAILURE");
                    androidWriter.println("FAILURE");
                    authPasswordBufferPC.clear();
                    authPasswordBufferAndroid.clear();
                    nextPasswordBufferPC.clear();
                    nextPasswordBufferAndroid.clear();
                    username = "";
                    System.out.println("FAILURE");
                }
            }
            try {
                if(pcReader.ready()) {
                    processInput(pcReader, pcWriter, authPasswordBufferPC,
                            nextPasswordBufferPC, false);
                }
            }
            catch(Exception e) {
                System.out.println("Something went wrong with the PC client.");
                System.out.println(e.toString());
                working = false;
            }
            try {
                if(androidReader.ready()) {
                    processInput(androidReader, androidWriter,
                            authPasswordBufferAndroid,
                            nextPasswordBufferAndroid, true);
                }
            }
            catch(Exception e) {
                System.out
                        .println("Something went wrong with the Android client.");
                System.out.println(e.toString());
                working = false;
            }
        }

    }

    private boolean authenticate() {
        boolean validPC = false;
        boolean validAndroid = true;
        String password = buildPassword();
        // String passwordAndroid = buildPassword(authPasswordBufferAndroid);
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        // System.out.println("Android Password: " + passwordAndroid);

        // Check PC password against generated permutations
        for(int i = 0; i < pwPermutationsPC.size(); i++) {
            System.out.println("Permutation: " + pwPermutationsPC.get(i));
            if(password.equals(pwPermutationsPC.get(i))) {
                validPC = true;
            }
        }

        /*
         * //Check Android password against generated permutations for(int i =
         * 0; i < pwPermutationsAndroid.size(); i++) {
         * System.out.println("Permutation: " + pwPermutationsAndroid.get(i));
         * if(passwordAndroid.equals(pwPermutationsAndroid.get(i))) {
         * validAndroid = true; } }
         * 
         * //Check that the correct number of keys were pressed by the Android
         * client. if(charOrder.length != inputOrdering.size()) { return false;
         * }
         * 
         * //Check that the input ordering is correct for(int i = 0; i <
         * inputOrdering.size(); i++) { if(charOrder[i] != inputOrdering.get(i))
         * { return false; } }
         */

        return username.equals(user) && validPC && validAndroid;
    }

    private String buildPassword() {
        int pcCount = 0;
        int androidCount = 0;
        String password = "";
        while(androidCount < authPasswordBufferAndroid.size()
                || pcCount < authPasswordBufferPC.size()) {
            if(pcCount == authPasswordBufferPC.size()) {
                String[] androidPassword = authPasswordBufferAndroid.get(
                        androidCount).split(" ");
                password += androidPassword[1];
                androidCount++;
            }
            else if(androidCount == authPasswordBufferAndroid.size()) {
                String[] pcPassword = authPasswordBufferPC.get(pcCount).split(
                        " ");
                password += pcPassword[1];
                pcCount++;
            }
            else {
                String[] pcPassword = authPasswordBufferPC.get(pcCount).split(
                        " ");
                String[] androidPassword = authPasswordBufferAndroid.get(
                        androidCount).split(" ");
                if(Long.parseLong(pcPassword[2]) < Long
                        .parseLong(androidPassword[2])) {
                    password += pcPassword[1];
                    pcCount++;
                }
                else {
                    password += androidPassword[1];
                    androidCount++;
                }
            }
        }

        return password;
    }

    private void getChallengePassword() throws FileNotFoundException {
        Scanner scanner = new Scanner(
                new FileReader(
                        "D:/iDataFiles/iFiles/Shared-Files/StudentReports/SeniorDesign/S_14/Final delivery/SeniorDesign_S14/Sever and PC/data/challengePassword.txt"));

        while(scanner.hasNext()) {
            pwCharacters.add(scanner.nextLine());
        }

        scanner.close();
    }

    private void buildPasswordPermutations(ArrayList<String> entries,
            ArrayList<String> pwPermutations) {
        String[] current;
        ArrayList<String> newEntries;
        String permutation = "";
        int permuteCount = 0;

        for(int i = 0; i < entries.size(); i++) {
            current = entries.get(i).split(" ");
            if(Integer.parseInt(current[0]) < permuteCount) {
                newEntries = new ArrayList<String>(entries);
                newEntries.remove(i - 1);
                buildPasswordPermutations(newEntries, pwPermutations);
            }
            else {
                permutation += current[1];
                permuteCount++;
            }
        }

        pwPermutations.add(permutation);

    }
}
