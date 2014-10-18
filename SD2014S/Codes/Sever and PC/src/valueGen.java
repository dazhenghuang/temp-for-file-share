import java.security.*;
import java.util.*;

import security.*;

public class valueGen {
   private static final int NUM_BUTTONS = 16;
   private static final int GROUP_SIZE = 4;
   private static final byte[] SEED = {-94, -110, -32, -54, 88, -59, -67, 91};
   private static ArrayList<String> characterTable = new ArrayList<String>(
            Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
                    "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                    "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7",
                    "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
                    "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
                    "w", "x", "y", "z", "!", "_"));
   private static ArrayList<KeyGrouping> groupingTable = new ArrayList<KeyGrouping>();

   public static void main(String[] args) {
      valueGen vg = new valueGen();
      vg.createKeyGroupings();
   }

   private void createKeyGroupings() {
      SecureRandom random = new SecureRandom(SEED);
      KeyGrouping grouping;
      ArrayList<String> tempTable = new ArrayList<String>(characterTable);
      int index;
      String keys;
   
      // Create the grouping objects
      for(int i = 0; i < NUM_BUTTONS; i++) {
         // Create the key string for the grouping
         keys = "";
         for(int j = 0; j < GROUP_SIZE; j++) {
            index = random.nextInt(tempTable.size());
            keys += tempTable.get(index);
            tempTable.remove(index);
         }
         grouping = new KeyGrouping(i % 4, keys);
         groupingTable.add(grouping);
         System.out.println(grouping.getKeys());
      }
   }
}
