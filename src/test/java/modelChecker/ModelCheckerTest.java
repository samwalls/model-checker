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

    /*
     * An example of how to set up and call the model building methods and make
     * a call to the model checker itself. The contents of model.json,
     * constraint1.json and ctl.json are just examples, you need to add new
     * models and formulas for the mutual exclusion task.
     */
    @Test
    public void buildAndCheckModel() throws IOException {
        Model model = Model.parseModel("src/test/resources/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/ctl1.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        assertFalse(checker.check(model, constraint, f));
    }

    @Test
    public void testBasicCounterexample() throws IOException {
        Model model = Model.parseModel("src/test/resources/test1/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/test1/ctl1.json").parse();
        StateFormula constraint = new FormulaParser("src/test/resources/test1/constraint1.json").parse();
        // this should pass with the constraint
        assertTrue(checker.check(model, constraint, f));
        // this should fail without a constraint (the for-all statement no longer holds without constraining the paths)
        assertFalse(checker.check(model, constraint, f));
    }

    @Test
    public void testBasicThereExistsUntil() throws IOException {
        Model model = Model.parseModel("src/test/resources/test1/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/test1/ctl2.json").parse();
        assertTrue(checker.check(model, new BoolProp(true), f));
        // if we now constrain the checking to only look at paths where the there exists statement is bound to fail, it should fail
        StateFormula constraint = new FormulaParser("src/test/resources/test1/constraint2.json").parse();
        assertFalse(checker.check(model, constraint, f));
        String[] counterExamplePath = checker.getTrace();
        assertTrue("expected counter example path to be non-empty", counterExamplePath.length > 0);
    }

    @Test
    public void testBasicThereExistsAlways() throws IOException {
        Model model = Model.parseModel("src/test/resources/test1/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/test1/ctl3.json").parse();
        assertTrue(checker.check(model, new BoolProp(true), f));
    }

    @Test
    public void testBasicThereExistsAlwaysWithActionSet() throws IOException {
        Model model = Model.parseModel("src/test/resources/test1/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/test1/ctl5.json").parse();
        assertFalse(checker.check(model, new BoolProp(true), f));
    }

    @Test
    public void testBasicThereExistsEventually() throws IOException {
        Model model = Model.parseModel("src/test/resources/test1/model1.json");
        StateFormula f = new FormulaParser("src/test/resources/test1/ctl4.json").parse();
        assertFalse(checker.check(model, new BoolProp(true), f));
    }
}
