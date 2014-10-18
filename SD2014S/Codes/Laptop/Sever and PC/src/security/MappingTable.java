package security;
import java.util.Arrays;
import java.util.ArrayList;
import java.security.*;

public class MappingTable {
    private SecureRandom generator;
    private String[] mapping;
    private static ArrayList<String> characterTable = new ArrayList<String>(
            Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
                    "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                    "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7",
                    "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
                    "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
                    "w", "x", "y", "z", "!", "_"));

    public MappingTable(SecureRandom generator) {
        this.generator = generator;
        this.mapping = new String[64];
        generateMapping(8);
    }

    public String getMappedString(String input) {
        // Returns a string constructed from the input characters' mappings.
        String returnValue = "";
        for (int i = 0; i < input.length(); i++) {
            returnValue += this.mapping[characterTable.indexOf(""
                    + input.charAt(i))];
        }
        return returnValue;
    }

    private void generateMapping(int StringLength) {
        // Generates a mapping of length StringLength
        for (int i = 0; i < this.mapping.length; i++) {
            this.mapping[i] = "";
            while (this.mapping[i].length() < StringLength) {
                this.mapping[i] += getStringFromInt(generator.nextInt(64));
            }
        }
    }

    private String getStringFromInt(int value) {
        return characterTable.get(value);
    }
}
