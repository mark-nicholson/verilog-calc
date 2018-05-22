package vcalc;

import java.util.HashMap;
import java.util.Map;

import org.nicholnet.util.VerilogInteger;

public class VCalcExprVisitor extends VCalcBaseVisitor<VerilogInteger> {

	Map<String, VerilogInteger> memory = new HashMap<String, VerilogInteger>();
    /** ID '=' expr NEWLINE */
    @Override
    public VerilogInteger visitAssign(VCalcParser.AssignContext ctx) {
        String id = ctx.ID().getText();  // id is left-hand side of '='
        VerilogInteger value = visit(ctx.expr());   // compute value of expression on right
        memory.put(id, value);           // store it in our memory
        System.out.println(id + " <-- " + value.toString(10));
        return value;
    }

    /** expr NEWLINE */
    @Override
    public VerilogInteger visitPrintExpr(VCalcParser.PrintExprContext ctx) {
        VerilogInteger value = visit(ctx.expr()); // evaluate the expr child
        System.out.println(" = " + value.toString(10));         // print the result
        return VerilogInteger.ZERO;        // return dummy value
    }

    /** INT */
    @Override
    public VerilogInteger visitInt(VCalcParser.IntContext ctx) {
        VerilogInteger vi;
        switch (ctx.value.getType()) {
        case VCalcParser.BIN_LITERAL:
        	vi = new VerilogInteger(ctx.value.getText().substring(2), 2);
        	break;
        case VCalcParser.OCTAL_LITERAL:
        	vi = new VerilogInteger(ctx.value.getText().substring(1), 8);
        	break;
        case VCalcParser.HEX_LITERAL:
        	vi = new VerilogInteger(ctx.value.getText().substring(2), 16);
        	break;
        default:
        	vi = new VerilogInteger(ctx.value.getText(), 10);
        	break;
        }
        System.out.print(" " + vi.toString(10) + " ");
        return vi;
    }

    /** Verilog */
    @Override
    public VerilogInteger visitVerilog(VCalcParser.VerilogContext ctx) {
        VerilogInteger vi;
        switch (ctx.value.getType()) {
        case VCalcParser.VERILOG_BIN_LITERAL:
        	vi = VerilogInteger.decode(ctx.value.getText());
        	break;
        case VCalcParser.VERILOG_OCT_LITERAL:
        	vi = VerilogInteger.decode(ctx.value.getText());
        	break;
        case VCalcParser.VERILOG_HEX_LITERAL:
        	vi = VerilogInteger.decode(ctx.value.getText());
        	break;
        default:
        	vi = VerilogInteger.decode(ctx.value.getText());
        	break;
        }
        System.out.print(" " + vi.toString(10) + " ");
        return vi;
    }

    /** ID */
    @Override
    public VerilogInteger visitId(VCalcParser.IdContext ctx) {
        String id = ctx.ID().getText();
        if ( memory.containsKey(id) ) {
        	System.out.print(" " + id + "[" + memory.get(id).toString(10) + "] ");
        	return memory.get(id);
        }
        
        /* hmm... SHOULD raise an error... */
        return VerilogInteger.ZERO;
    }

    /** Exponent */
    @Override
    public VerilogInteger visitExponent(VCalcParser.ExponentContext ctx) {
    	VerilogInteger left = visit(ctx.expr(0));  // get value of left subexpression
    	VerilogInteger right = visit(ctx.expr(1)); // get value of right subexpression
    	System.out.print(" ** ");
    	int exponent = right.intValue();
        return left.pow(exponent);
    }
    
    /** BitInvert */
    @Override
    public VerilogInteger visitBitInvert(VCalcParser.BitInvertContext ctx) {
    	VerilogInteger expr = visit(ctx.expr());
    	System.out.print(" ~ ");
        return expr.not();
    }
    
    /** UnarySign */
    @Override
    public VerilogInteger visitUnarySign(VCalcParser.UnarySignContext ctx) {
    	VerilogInteger expr = visit(ctx.expr());
    	if (ctx.op.getType() == VCalcParser.OP_PLUS) {
    		System.out.print(" + ");
    		return expr;
    	}
    	System.out.print(" - ");
        return expr.negate();
    }

    /** expr op=('*'|'/'|'%'|'//') expr */
    @Override
    public VerilogInteger visitMulDivMod(VCalcParser.MulDivModContext ctx) {
        VerilogInteger left = visit(ctx.expr(0));  // get value of left subexpression
        VerilogInteger right = visit(ctx.expr(1)); // get value of right subexpression
        if ( ctx.op.getType() == VCalcParser.OP_MULT ) {
        	System.out.print(" * ");
        	return left.multiply(right);
        }
        
        if ( ctx.op.getType() == VCalcParser.OP_DIV ) {
        	System.out.print(" / ");
        	return left.divide(right);
        }
        
        VerilogInteger[] result = left.divideAndRemainder(right);
        
        if ( ctx.op.getType() == VCalcParser.OP_MODULUS ) {
        	System.out.print(" % ");
        	return result[1];
        }
        
        // must be OP_DIV_FLOOR
        System.out.print(" /_ ");
        return result[0];
    }

    /** expr op=('+'|'-') expr */
    @Override
    public VerilogInteger visitAddSub(VCalcParser.AddSubContext ctx) {
    	VerilogInteger left = visit(ctx.expr(0));  // get value of left subexpression
    	VerilogInteger right = visit(ctx.expr(1)); // get value of right subexpression
        if ( ctx.op.getType() == VCalcParser.OP_PLUS ) {
        	System.out.print(" + ");
        	return left.add(right);
        }
        System.out.print(" - ");
        return left.subtract(right); // must be SUB
    }
    
    /** BitShift */
    @Override
    public VerilogInteger visitBitShift(VCalcParser.BitShiftContext ctx) {
    	VerilogInteger left = visit(ctx.expr(0));  // get value of left subexpression
    	VerilogInteger right = visit(ctx.expr(1)); // get value of right subexpression

    	switch (ctx.op.getType()) {
    	case VCalcParser.OP_DOWN_SHIFT:
    		System.out.print(" >> ");
    		return left.shiftRight(right.intValue(), true, false);
    	case VCalcParser.OP_UP_SHIFT:
    		System.out.print(" << ");
    		return left.shiftLeft(right.intValue(), true, false);
    	case VCalcParser.OP_DOWN_SHIFT_EXTEND:
    		System.out.print(" >>> ");
    		return left.shiftRight(right.intValue(), true, true);
    	case VCalcParser.OP_UP_SHIFT_EXTEND:
    		System.out.print(" <<< ");
    		return left.shiftLeft(right.intValue(), true, true);
    	default:
    		System.err.println("Invalid operator for bit-shift");
    		return VerilogInteger.ZERO;
    	}
    }
    
    /** BitAnd */
    @Override
    public VerilogInteger visitBitAnd(VCalcParser.BitAndContext ctx) {
    	VerilogInteger left = visit(ctx.expr(0));  // get value of left subexpression
    	VerilogInteger right = visit(ctx.expr(1)); // get value of right subexpression
    	//int l = left.intValue();
    	//int r = right.intValue();
    	//System.out.println("Eval: " + left + " AND " + right);
    	//System.out.println("Result: " + (l&r));
    	System.out.print(" & ");
        return left.and(right);
    }
    
    /** BitXor */
    @Override
    public VerilogInteger visitBitXor(VCalcParser.BitXorContext ctx) {
    	VerilogInteger left = visit(ctx.expr(0));  // get value of left subexpression
    	VerilogInteger right = visit(ctx.expr(1)); // get value of right subexpression
    	System.out.print(" ^ ");
        return left.xor(right);
    }
    
    /** BitOr */
    @Override
    public VerilogInteger visitBitOr(VCalcParser.BitOrContext ctx) {
    	VerilogInteger left = visit(ctx.expr(0));  // get value of left subexpression
    	VerilogInteger right = visit(ctx.expr(1)); // get value of right subexpression
    	System.out.print(" | ");
    	return left.or(right);
    }

    /** '(' expr ')' */
    @Override
    public VerilogInteger visitParens(VCalcParser.ParensContext ctx) {
        return visit(ctx.expr()); // return child expr's value
    }

}
