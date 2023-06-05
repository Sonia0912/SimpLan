package ast;

import evaluator.SimpLanlib;
import semanticanalysis.STentry;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;

import java.util.ArrayList;
import java.util.HashMap;

public class BodyNode implements Node{

    private ArrayList<Node> dec ;
    private ArrayList<Node> stm ;
    private Node exp ;
    private int nesting ;

    public BodyNode (ArrayList<Node> _dec, ArrayList<Node> _stm, Node _exp) {
        dec = _dec ;
        stm = _stm ;
        exp = _exp ;
    }

    public ArrayList<SemanticError> checkSemantics(SymbolTable ST, int _nesting) {
        //nesting = _nesting + 1;
        nesting = _nesting;

        //HashMap<String, STentry> H = new HashMap<String, STentry>();
        //ST.add(H);

        //declare resulting list
        ArrayList<SemanticError> errors = new ArrayList<SemanticError>();

        for (Node d : dec) {
            errors.addAll(d.checkSemantics(ST, nesting)) ;
        }

        for (Node s : stm) {
            errors.addAll(s.checkSemantics(ST, nesting)) ;
        }

        if(exp != null){
            errors.addAll(exp.checkSemantics(ST, nesting)) ;
        }

        //ST.remove();


        return errors;
    }

    public Type typeCheck () {
        for (Node s: stm)
            s.typeCheck();
        if(exp != null)
            return exp.typeCheck();
        return new VoidType();
    }

    public String codeGeneration() {
        String declCode = "addi SP " + dec.size() + "\n";
        String stmsCode = "";
        String expCode = "";
        for (Node s: stm)
            stmsCode += s.codeGeneration();
            //stmsCode += "pushr A0 \n";
        if(exp != null)
            expCode = exp.codeGeneration();

        return    //   "pushr FP \n"
                  // + "move SP FP \n"
                  // + "addi FP 1 \n"
                  // + "popr FP \n"
                  // + "move AL T1 \n"
                  // + "pushr T1 \n"
                  // + "move FP AL \n"
                  // + "subi AL 1 \n"
                  // + "popr AL \n"

                   "pushr A0 \n" +
                  stmsCode
                + expCode
                + declCode;
    }

    public String toPrint(String s) {
        String statementstr = "";
        String declstr = "";
        for (Node d : dec)
            declstr += d.toPrint(s+"\t");
        for (Node st : stm)
            statementstr += st.toPrint(s+"\t");
        if(exp != null)
            return s+"Body \n" + declstr + statementstr + "\n" + exp.toPrint(s+"\t") ;
        return s+"Body \n" + declstr + statementstr + "\n";
    }

}
