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
    public void test1CTL1() throws IOException {
        Model model = Model.parseModel("src/test/resources/test1/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/test1/ctl1.json").parse();
        // this should fail without a constraint (the for-all statement no longer holds without constraining the paths)
        assertFalse(checker.check(model, new BoolProp(true), f));
        String[] counterExamplePath = checker.getTrace();
        assertTrue("expected counter example path to be non-empty", counterExamplePath.length > 0);
    }

    @Test
    public void test1CLT1Constraint1() throws IOException {
        Model model = Model.parseModel("src/test/resources/test1/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/test1/ctl1.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/test1/constraint1.json").parse();
        // we are searching for A(p aUb q)
        // this should pass with the constraint, the constraint "!neverq" implies that we only search paths that aren't labelled neverq
        assertTrue(checker.check(model, constraint, f));
    }

    @Test
    public void test1CLT1Constraint2() throws IOException {
        Model model = Model.parseModel("src/test/resources/test1/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/test1/ctl1.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/test1/constraint2.json").parse();
        assertFalse(checker.check(model, constraint, f));
        String[] counterExamplePath = checker.getTrace();
        assertTrue("expected counter example path to be non-empty", counterExamplePath.length > 0);
    }

    @Test
    public void test1CTL2() throws IOException {
        Model model = Model.parseModel("src/test/resources/test1/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/test1/ctl2.json").parse();
        assertTrue(checker.check(model, new BoolProp(true), f));
    }

    @Test
    public void test1CTL2Constraint1() throws IOException {
        Model model = Model.parseModel("src/test/resources/test1/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/test1/ctl2.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/test1/constraint1.json").parse();
        assertTrue(checker.check(model, constraint, f));
    }

    @Test
    public void test1CTL2Constraint2() throws IOException {
        Model model = Model.parseModel("src/test/resources/test1/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/test1/ctl2.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/test1/constraint2.json").parse();
        // if we now constrain the checking to only look at paths where the there exists statement is bound to fail, it should fail
        assertFalse(checker.check(model, constraint, f));
        String[] counterExamplePath = checker.getTrace();
        assertTrue("expected counter example path to be non-empty", counterExamplePath.length > 0);
    }

    @Test
    public void test1CTL3() throws IOException {
        Model model = Model.parseModel("src/test/resources/test1/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/test1/ctl3.json").parse();
        assertTrue(checker.check(model, new BoolProp(true), f));
    }

    @Test
    public void test1CTL3Constraint1() throws IOException {
        Model model = Model.parseModel("src/test/resources/test1/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/test1/ctl3.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/test1/constraint2.json").parse();
        assertTrue(checker.check(model, constraint, f));
    }

    @Test
    public void test1CTL3Constraint2() throws IOException {
        Model model = Model.parseModel("src/test/resources/test1/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/test1/ctl3.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/test1/constraint2.json").parse();
        assertTrue(checker.check(model, constraint, f));
    }

    @Test
    public void test1CTL4() throws IOException {
        Model model = Model.parseModel("src/test/resources/test1/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/test1/ctl4.json").parse();
        assertFalse(checker.check(model, new BoolProp(true), f));
        String[] counterExamplePath = checker.getTrace();
        assertTrue("expected counter example path to be non-empty", counterExamplePath.length > 0);
    }

    @Test
    public void test1CTL4Constraint1() throws IOException {
        Model model = Model.parseModel("src/test/resources/test1/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/test1/ctl4.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/test1/constraint1.json").parse();
        assertTrue(checker.check(model, constraint, f));
    }

    @Test
    public void test1CTL4Constraint2() throws IOException {
        Model model = Model.parseModel("src/test/resources/test1/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/test1/ctl4.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/test1/constraint2.json").parse();
        assertFalse(checker.check(model, constraint, f));
        String[] counterExamplePath = checker.getTrace();
        assertTrue("expected counter example path to be non-empty", counterExamplePath.length > 0);
    }

    @Test
    public void test1CTL5() throws IOException {
        Model model = Model.parseModel("src/test/resources/test1/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/test1/ctl5.json").parse();
        assertFalse(checker.check(model, new BoolProp(true), f));
        String[] counterExamplePath = checker.getTrace();
        assertTrue("expected counter example path to be non-empty", counterExamplePath.length > 0);
    }

    @Test
    public void test1CTL5Constraint1() throws IOException {
        Model model = Model.parseModel("src/test/resources/test1/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/test1/ctl5.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/test1/constraint1.json").parse();
        assertFalse(checker.check(model, constraint, f));
        String[] counterExamplePath = checker.getTrace();
        assertTrue("expected counter example path to be non-empty", counterExamplePath.length > 0);
    }

    @Test
    public void test1CTL5Constraint2() throws IOException {
        Model model = Model.parseModel("src/test/resources/test1/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/test1/ctl5.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/test1/constraint2.json").parse();
        assertFalse(checker.check(model, constraint, f));
        String[] counterExamplePath = checker.getTrace();
        assertTrue("expected counter example path to be non-empty", counterExamplePath.length > 0);
    }

    @Test
    public void test1CTL6() throws IOException {
        Model model = Model.parseModel("src/test/resources/test1/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/test1/ctl6.json").parse();
        assertTrue(checker.check(model, new BoolProp(true), f));
    }

    @Test
    public void test1CTL6Constraint1() throws IOException {
        Model model = Model.parseModel("src/test/resources/test1/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/test1/ctl6.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/test1/constraint1.json").parse();
        assertTrue(checker.check(model, constraint, f));
    }

    @Test
    public void test1CTL6Constraint2() throws IOException {
        Model model = Model.parseModel("src/test/resources/test1/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/test1/ctl6.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/test1/constraint2.json").parse();
        assertFalse(checker.check(model, constraint, f));
        String[] counterExamplePath = checker.getTrace();
        assertTrue("expected counter example path to be non-empty", counterExamplePath.length > 0);
    }

    @Test
    public void test1CTL8() throws IOException {
        Model model = Model.parseModel("src/test/resources/test1/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/test1/ctl8.json").parse();
        assertFalse(checker.check(model, new BoolProp(true), f));
        String[] counterExamplePath = checker.getTrace();
        assertTrue("expected counter example path to be non-empty", counterExamplePath.length > 0);
    }

    @Test
    public void test1CTL8Constraint1() throws IOException {
        Model model = Model.parseModel("src/test/resources/test1/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/test1/ctl8.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/test1/constraint1.json").parse();
        assertFalse(checker.check(model, constraint, f));
        String[] counterExamplePath = checker.getTrace();
        assertTrue("expected counter example path to be non-empty", counterExamplePath.length > 0);
    }

    @Test
    public void test1CTL8Constraint2() throws IOException {
        Model model = Model.parseModel("src/test/resources/test1/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/test1/ctl8.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/test1/constraint2.json").parse();
        assertTrue(checker.check(model, constraint, f));
    }
}
