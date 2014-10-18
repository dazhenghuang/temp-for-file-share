package security;

import java.security.*;

public class Obfuscator {
    private MappingTable mapper;
    
    public Obfuscator(SecureRandom generator) {
        this.mapper = new MappingTable(generator);
    }
    
    public String obfuscate(String text) {
        return mapper.getMappedString(text);
    }
}
