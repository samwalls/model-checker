package modelChecker;

import static org.junit.Assert.*;

import java.io.IOException;

import formula.stateFormula.BoolProp;
import modelChecker.asctl.ASCTLModelChecker;
import org.junit.Before;
import org.junit.Test;

import formula.FormulaParser;
import formula.stateFormula.StateFormula;
import model.Model;

public class ModelCheckerTest {

    private ModelChecker checker;

    @Before
    public void setup() {
        checker = new ASCTLModelChecker();
    }

    @Test
    public void branchCTL1() throws IOException {
        Model model = Model.parseModel("src/test/resources/branch/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/branch/ctl1.json").parse();
        // this should fail without a constraint (the for-all statement no longer holds without constraining the paths)
        assertFalse(checker.check(model, new BoolProp(true), f));
        String[] counterExamplePath = checker.getTrace();
        assertTrue("expected counter example path to be non-empty", counterExamplePath.length > 0);
    }

    @Test
    public void test1CLT1Constraint1() throws IOException {
        Model model = Model.parseModel("src/test/resources/branch/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/branch/ctl1.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/branch/constraint1.json").parse();
        // we are searching for A(p aUb q)
        // this should pass with the constraint, the constraint "!neverq" implies that we only search paths that aren't labelled neverq
        assertTrue(checker.check(model, constraint, f));
    }

    @Test
    public void test1CLT1Constraint2() throws IOException {
        Model model = Model.parseModel("src/test/resources/branch/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/branch/ctl1.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/branch/constraint2.json").parse();
        assertFalse(checker.check(model, constraint, f));
        String[] counterExamplePath = checker.getTrace();
        assertTrue("expected counter example path to be non-empty", counterExamplePath.length > 0);
    }

    @Test
    public void branchCTL2() throws IOException {
        Model model = Model.parseModel("src/test/resources/branch/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/branch/ctl2.json").parse();
        assertTrue(checker.check(model, new BoolProp(true), f));
    }

    @Test
    public void branchCTL2Constraint1() throws IOException {
        Model model = Model.parseModel("src/test/resources/branch/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/branch/ctl2.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/branch/constraint1.json").parse();
        assertTrue(checker.check(model, constraint, f));
    }

    @Test
    public void branchCTL2Constraint2() throws IOException {
        Model model = Model.parseModel("src/test/resources/branch/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/branch/ctl2.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/branch/constraint2.json").parse();
        // if we now constrain the checking to only look at paths where the there exists statement is bound to fail, it should fail
        assertFalse(checker.check(model, constraint, f));
        String[] counterExamplePath = checker.getTrace();
        assertTrue("expected counter example path to be non-empty", counterExamplePath.length > 0);
    }

    @Test
    public void branchCTL3() throws IOException {
        Model model = Model.parseModel("src/test/resources/branch/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/branch/ctl3.json").parse();
        assertTrue(checker.check(model, new BoolProp(true), f));
    }

    @Test
    public void branchCTL3Constraint1() throws IOException {
        Model model = Model.parseModel("src/test/resources/branch/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/branch/ctl3.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/branch/constraint2.json").parse();
        assertTrue(checker.check(model, constraint, f));
    }

    @Test
    public void branchCTL3Constraint2() throws IOException {
        Model model = Model.parseModel("src/test/resources/branch/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/branch/ctl3.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/branch/constraint2.json").parse();
        assertTrue(checker.check(model, constraint, f));
    }

    @Test
    public void branchCTL4() throws IOException {
        Model model = Model.parseModel("src/test/resources/branch/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/branch/ctl4.json").parse();
        assertFalse(checker.check(model, new BoolProp(true), f));
        String[] counterExamplePath = checker.getTrace();
        assertTrue("expected counter example path to be non-empty", counterExamplePath.length > 0);
    }

    @Test
    public void branchCTL4Constraint1() throws IOException {
        Model model = Model.parseModel("src/test/resources/branch/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/branch/ctl4.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/branch/constraint1.json").parse();
        assertTrue(checker.check(model, constraint, f));
    }

    @Test
    public void branchCTL4Constraint2() throws IOException {
        Model model = Model.parseModel("src/test/resources/branch/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/branch/ctl4.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/branch/constraint2.json").parse();
        assertFalse(checker.check(model, constraint, f));
        String[] counterExamplePath = checker.getTrace();
        assertTrue("expected counter example path to be non-empty", counterExamplePath.length > 0);
    }

    @Test
    public void branchCTL5() throws IOException {
        Model model = Model.parseModel("src/test/resources/branch/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/branch/ctl5.json").parse();
        assertFalse(checker.check(model, new BoolProp(true), f));
        String[] counterExamplePath = checker.getTrace();
        assertTrue("expected counter example path to be non-empty", counterExamplePath.length > 0);
    }

    @Test
    public void branchCTL5Constraint1() throws IOException {
        Model model = Model.parseModel("src/test/resources/branch/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/branch/ctl5.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/branch/constraint1.json").parse();
        assertFalse(checker.check(model, constraint, f));
        String[] counterExamplePath = checker.getTrace();
        assertTrue("expected counter example path to be non-empty", counterExamplePath.length > 0);
    }

    @Test
    public void branchCTL5Constraint2() throws IOException {
        Model model = Model.parseModel("src/test/resources/branch/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/branch/ctl5.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/branch/constraint2.json").parse();
        assertFalse(checker.check(model, constraint, f));
        String[] counterExamplePath = checker.getTrace();
        assertTrue("expected counter example path to be non-empty", counterExamplePath.length > 0);
    }

    @Test
    public void branchCTL6() throws IOException {
        Model model = Model.parseModel("src/test/resources/branch/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/branch/ctl6.json").parse();
        assertTrue(checker.check(model, new BoolProp(true), f));
    }

    @Test
    public void branchCTL6Constraint1() throws IOException {
        Model model = Model.parseModel("src/test/resources/branch/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/branch/ctl6.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/branch/constraint1.json").parse();
        assertTrue(checker.check(model, constraint, f));
    }

    @Test
    public void branchCTL6Constraint2() throws IOException {
        Model model = Model.parseModel("src/test/resources/branch/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/branch/ctl6.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/branch/constraint2.json").parse();
        assertFalse(checker.check(model, constraint, f));
        String[] counterExamplePath = checker.getTrace();
        assertTrue("expected counter example path to be non-empty", counterExamplePath.length > 0);
    }

    @Test
    public void branchCTL7() throws IOException {
        Model model = Model.parseModel("src/test/resources/branch/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/branch/ctl7.json").parse();
        assertFalse(checker.check(model, new BoolProp(true), f));
        String[] counterExamplePath = checker.getTrace();
        assertTrue("expected counter example path to be non-empty", counterExamplePath.length > 0);
    }

    @Test
    public void branchCTL7Constraint1() throws IOException {
        Model model = Model.parseModel("src/test/resources/branch/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/branch/ctl7.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/branch/constraint1.json").parse();
        assertFalse(checker.check(model, constraint, f));
        String[] counterExamplePath = checker.getTrace();
        assertTrue("expected counter example path to be non-empty", counterExamplePath.length > 0);
    }

    @Test
    public void branchCTL7Constraint2() throws IOException {
        Model model = Model.parseModel("src/test/resources/branch/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/branch/ctl7.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/branch/constraint2.json").parse();
        assertFalse(checker.check(model, constraint, f));
        String[] counterExamplePath = checker.getTrace();
        assertTrue("expected counter example path to be non-empty", counterExamplePath.length > 0);
    }

    @Test
    public void branchCTL8() throws IOException {
        Model model = Model.parseModel("src/test/resources/branch/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/branch/ctl8.json").parse();
        assertFalse(checker.check(model, new BoolProp(true), f));
        String[] counterExamplePath = checker.getTrace();
        assertTrue("expected counter example path to be non-empty", counterExamplePath.length > 0);
    }

    @Test
    public void branchCTL8Constraint1() throws IOException {
        Model model = Model.parseModel("src/test/resources/branch/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/branch/ctl8.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/branch/constraint1.json").parse();
        assertFalse(checker.check(model, constraint, f));
        String[] counterExamplePath = checker.getTrace();
        assertTrue("expected counter example path to be non-empty", counterExamplePath.length > 0);
    }

    @Test
    public void branchCTL8Constraint2() throws IOException {
        Model model = Model.parseModel("src/test/resources/branch/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/branch/ctl8.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/branch/constraint2.json").parse();
        assertTrue(checker.check(model, constraint, f));
    }

    //******** "COLOUR" TESTS ********//

    @Test
    public void colourCTL1() throws IOException {
        Model model = Model.parseModel("src/test/resources/colour/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/colour/ctl1.json").parse();
        assertTrue(checker.check(model, new BoolProp(true), f));
    }

    @Test
    public void colourCTL2() throws IOException {
        Model model = Model.parseModel("src/test/resources/colour/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/colour/ctl2.json").parse();
        assertTrue(checker.check(model, new BoolProp(true), f));
    }

    @Test
    public void colourCTL3() throws IOException {
        Model model = Model.parseModel("src/test/resources/colour/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/colour/ctl3.json").parse();
        assertFalse(checker.check(model, new BoolProp(true), f));
        String[] path = checker.getTrace();
        assertNotNull(path);
        assertEquals(1, path.length);
    }

    @Test
    public void colourCTL4() throws IOException {
        Model model = Model.parseModel("src/test/resources/colour/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/colour/ctl4.json").parse();
        assertFalse(checker.check(model, new BoolProp(true), f));
        String[] path = checker.getTrace();
        assertNotNull(path);
        assertTrue(path.length > 0);
    }
}
