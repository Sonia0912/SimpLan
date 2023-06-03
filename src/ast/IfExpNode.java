package ast;

import java.util.ArrayList;

import evaluator.SimpLanlib;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;

public class IfExpNode implements Node {
	private Node guard ;
	private Node thenbranch ;
	private Node elsebranch ;
  
	public IfExpNode (Node _guard, Node _thenbranch, Node _elsebranch) {
    	guard = _guard ;
    	thenbranch = _thenbranch ;
    	elsebranch = _elsebranch ;
    }
  
   @Override
  public ArrayList<SemanticError> checkSemantics(SymbolTable ST, int _nesting) {
	  ArrayList<SemanticError> errors = new ArrayList<SemanticError>();

	  errors.addAll(guard.checkSemantics(ST, _nesting));

	  errors.addAll(thenbranch.checkSemantics(ST, _nesting));
	  errors.addAll(elsebranch.checkSemantics(ST, _nesting));

	  return errors;
  }
  
	public Type typeCheck() {
		if (guard.typeCheck() instanceof BoolType) {

			Type thenType = thenbranch.typeCheck() ;
			Type elseType = elsebranch.typeCheck() ;

			if (thenType.getClass().equals(elseType.getClass()))
        		return thenType;
			else {
        		System.out.println("Type Error: incompatible types in then and else branches");
        		return new ErrorType() ;	
			}
		} else {
			System.out.println("Type Error: non boolean condition in if");
			return new ErrorType() ;
		}   
	}
  
  	public String codeGeneration() {
  		String lthen = SimpLanlib.freshLabel(); 
  		String lend = SimpLanlib.freshLabel();
  		return guard.codeGeneration() +
			 "storei T1 1 \n" +
			 "beq A0 T1 "+ lthen + "\n" +			  
			 elsebranch.codeGeneration() +
			 "b " + lend + "\n" +
			 lthen + ":\n" +
			 thenbranch.codeGeneration() +
	         lend + ":\n" ; 
  	}

  	public String toPrint(String s) {
		String thenString = thenbranch.toPrint(s+"  ");
		String elseString = elsebranch.toPrint(s+"  ");
	    return s+"If\n" + guard.toPrint(s+"  ") + thenString  + elseString ;
	}
	  
}  