package test;

import main.SongReader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SongReaderTests {
    @Test
    public void evaluatesExpression() {
        SongReader sr = new SongReader();
        assertTrue(sr.works());
    }
}
