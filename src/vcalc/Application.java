package vcalc;

import java.io.IOException;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import vcalc.VCalcParser;
import vcalc.VCalcLexer;
import vcalc.VCalcExprVisitor;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;

public class Application {

	public static void main(String[] args) {
		CharStream input = null;
		InputStream istream = System.in;
		
		try {
			if (args.length > 0 && args[0] != null) {
				istream = new FileInputStream(args[0]);
			}
			input = CharStreams.fromStream(istream);
		}
		catch (FileNotFoundException fnfe) {
			System.out.println("Cannot locate file.");
			System.exit(-1);
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
