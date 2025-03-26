package test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SongReaderTests {
    @Test
    public void evaluatesExpression() {
        SongReader sr = new SongReader();
        sr.works();
        assertEquals(6, sum);
    }
}
