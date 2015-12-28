import junit.framework.Assert;
import org.junit.Test;


public class Test1 {

    public int sum(int a, int b) {
        return a + b;
    }

    @Test
    public void testSum() {
    Assert.assertEquals(6, sum(5, 2));
    }
}
