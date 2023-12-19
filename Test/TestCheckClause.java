import org.junit.Assert;
import org.junit.Test;
import java.util.Arrays;

public class TestCheckClause {

    @Test
    public void TestCheckClauseTrue(){
        Solver solver = new Solver();
        int [] assignment = new int [] {0,1,1};
        int [] clause = new int [] {1,2};
        boolean result = solver.checkClause(assignment, clause);
        System.out.println(Arrays.toString(assignment));
        System.out.println(Arrays.toString(clause));
        System.out.println(result);
        Assert.assertTrue(result);

    }

    @Test
    public void TestCheckClauseFalse(){
        Solver solver = new Solver();
        int[] assignment = new int[] {0,-1,-1};
        int[] clause = new int[] {1, 2};
        boolean result = solver.checkClause(assignment, clause);
        System.out.println(Arrays.toString(assignment));
        System.out.println(Arrays.toString(clause));
        System.out.println(result);
        Assert.assertFalse(result);

    }
}