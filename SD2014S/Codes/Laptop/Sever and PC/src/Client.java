import java.net.*;
import java.security.*;
import java.util.*;
import java.io.*;

import security.*;

public class Client {
    // Constants
    private static final int SERVER_PORT = 1040;
    private static final int CLIENT_SERVER_PORT = 1050;
    private static final int NUM_BUTTONS = 16;
    private static final int GROUP_SIZE = 4;
    private static ArrayList<String> characterTable = new ArrayList<String>(
            Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
                    "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                    "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7",
                    "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
                    "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
                    "w", "x", "y", "z", "!", "_"));
    private static ArrayList<KeyGrouping> groupingTable = new ArrayList<KeyGrouping>();

    // Network objects and variables
    private InetAddress serverAddress;
    private InetAddress clientAddress;
    private BasicConnection serverConnection;
    private BasicConnection clientConnection;
    private ServerSocket clientServer;

    // Security objects and variables
    private SecureRandom generator;
    private Obfuscator obfuscator;

    // Counters
    private int charactersSent;

    public static void main(String[] args) {
        Client client = new Client("127.0.0.1", "127.0.0.1", true);
    }

    public Client(String server_ip, String client_ip, boolean isPcClient) {
        try {
            this.serverAddress = InetAddress.getByName(server_ip);
            this.clientAddress = InetAddress.getByName(client_ip);
        }
        catch(UnknownHostException e) {
            System.out.println(e.getMessage());
        }

        this.serverConnection = new BasicConnection(this.serverAddress,
                SERVER_PORT);

        if(isPcClient) {
            try {
                initializeKeyGroupings();
            }
            catch(Exception e) {
                System.out.println(e.getMessage());
            }
            createNextGroupingValues();

        }
        else {
        }

        // this.generator = getNewGenerator();
        // this.obfuscator = new Obfuscator(getGenerator());
        serverConnection.openConnection();
    }

    /*
     * private void createKeyGroupings() { SecureRandom random = getGenerator();
     * KeyGrouping grouping; ArrayList<String> tempTable = new
     * ArrayList<String>(characterTable); int index; String keys;
     * 
     * // Create the grouping objects for(int i = 0; i < NUM_BUTTONS; i++) { //
     * Create the key string for the grouping keys = ""; for(int j = 0; j <
     * GROUP_SIZE; j++) { index = random.nextInt(tempTable.size()); keys +=
     * tempTable.get(index); tempTable.remove(index); } grouping = new
     * KeyGrouping(i % 4, keys); groupingTable.add(grouping); } }
     */

    private void initializeKeyGroupings() throws FileNotFoundException {
        String keys;
        KeyGrouping grouping;
        Scanner scanner = new Scanner(
                new FileReader(
                        "C:/Users/wu/Desktop/Sever and PC/data/keyGroupings.txt"));
        // Apparently we need the absolute path here.
        // new FileReader("data/keyGroupings.txt"));

        // Create the grouping objects
        for(int i = 0; i < NUM_BUTTONS; i++) {
            // Get the key string for the grouping
            keys = scanner.nextLine();
            grouping = new KeyGrouping(i % 4, keys);
            groupingTable.add(grouping);
        }

        scanner.close();
    }

    private void createNextGroupingValues() {
        KeyGrouping grouping;
        KeyGrouping otherGrouping;
        String next_value;
        SecureRandom random = getGenerator();
        int first_index = random.nextInt(16);
        int second_index = random.nextInt(16);

        // Make sure the two indexes aren't the same
        while(second_index == first_index) {
            second_index = random.nextInt(16);
        }

        for(int i = 0; i < 16; i++) {
            grouping = groupingTable.get(i);
            if(i != first_index && i != second_index) {
                grouping.setNextKeys(grouping.getKeys());
            }
        }

        grouping = groupingTable.get(first_index);
        otherGrouping = groupingTable.get(second_index);

        String first_values = grouping.getKeys();
        String second_values = otherGrouping.getKeys();

        // Swap the first set
        next_value = first_values.replace(first_values.substring(2),
                second_values.substring(2));
        grouping.setNextKeys(next_value);
        grouping.setSwapGrouping(otherGrouping);

        // Swap the second set
        next_value = second_values.replace(second_values.substring(2),
                first_values.substring(2));
        otherGrouping.setNextKeys(next_value);
        otherGrouping.setSwapGrouping(grouping);

    }

    public ArrayList<KeyGrouping> getKeyGroupings() {
        return groupingTable;
    }

    public void sendPasswordGrouping(int groupingIndex) {
        KeyGrouping grouping = groupingTable.get(groupingIndex);
        // TODO: add obfuscation
        serverConnection.sendMessage("PWCHAR AUTH " + charactersSent + " "
                + grouping.getKeys() + " " + System.currentTimeMillis());
        serverConnection.sendMessage("PWCHAR NEXT " + charactersSent + " "
                + grouping.getNextKeys() + " " + System.currentTimeMillis());

        if(grouping.isSwapped()) {
            serverConnection.sendMessage("PWCHAR NEXT " + charactersSent + " "
                    + grouping.getSwapGrouping().getNextKeys() + " " + System.currentTimeMillis());
        }

        charactersSent++;

    }

    public void clearFields() {
        charactersSent = 0;
        serverConnection.sendMessage("CLEAR");
    }

    public boolean finishLogin(String username) {
        if(username.isEmpty()) {
            return false;
        }
        serverConnection.sendMessage("USERNAME " + username);
//        serverConnection.sendMessage("SUBMIT");

        return true;
    }

    public SecureRandom getGenerator() {
        generator = new SecureRandom();
        generator.setSeed((long) 123456);
        return generator;
    }
}
