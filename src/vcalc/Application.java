package vcalc;

import java.io.IOException;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import org.nicholnet.util.VerilogInteger;

import vcalc.VCalcParser;
import vcalc.VCalcLexer;
import vcalc.VCalcExprVisitor;

public class Application {

	public static void main(String[] args) {
		CharStream input = null;
		VerilogInteger bi = new VerilogInteger("ff", 16);

		try {
			input = CharStreams.fromStream(System.in);
		}
		catch (IOException io) {
			System.out.println("Failed to setup input stream");
			System.exit(-1);
		}
	    
		VCalcLexer lexer = new VCalcLexer(input);

		CommonTokenStream tokens = new CommonTokenStream(lexer);

		VCalcParser parser = new VCalcParser(tokens);
		ParseTree tree = parser.prog(); // begin parsing at rule 'r'
		
		VCalcExprVisitor eval = new VCalcExprVisitor();
		eval.visit(tree);
		
		System.out.println(tree.toStringTree(parser)); // print LISP-style tree
	}

}
