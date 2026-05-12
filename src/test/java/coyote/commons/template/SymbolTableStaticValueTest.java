package coyote.commons.template;

import coyote.commons.rtw.Symbols;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SymbolTableStaticValueTest {

    @Test
    public void testStaticValues() {
        SymbolTable symbols = new SymbolTable();

        // Network
        assertNotNull(symbols.getString(Symbols.HOSTNAME));
        assertNotNull(symbols.getString(Symbols.FQDN));
        assertNotNull(symbols.getString(Symbols.IPADDR));

        // OS / User
        assertEquals(System.getProperty("user.name"), symbols.getString(Symbols.USERNAME));
        assertEquals(System.getProperty("user.home"), symbols.getString(Symbols.USER_HOME));
        assertEquals(System.getProperty("java.io.tmpdir"), symbols.getString(Symbols.TMP_DIR));
        assertEquals(System.getProperty("os.name"), symbols.getString(Symbols.OS_NAME));
        assertEquals(System.getProperty("os.version"), symbols.getString(Symbols.OS_VERSION));
        assertEquals(System.getProperty("os.arch"), symbols.getString(Symbols.OS_ARCH));

        // Logic / Random
        String randInt = symbols.getString(Symbols.RANDOM_INT);
        assertNotNull(randInt);
        // Verify it's an integer
        Integer.parseInt(randInt);

        String randLong = symbols.getString(Symbols.RANDOM_LONG);
        assertNotNull(randLong);
        // Verify it's a long
        Long.parseLong(randLong);
    }
}
