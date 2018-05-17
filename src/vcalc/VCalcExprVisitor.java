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
        return value;
    }

    /** expr NEWLINE */
    @Override
    public VerilogInteger visitPrintExpr(VCalcParser.PrintExprContext ctx) {
        VerilogInteger value = visit(ctx.expr()); // evaluate the expr child
        System.out.println(value);         // print the result
        return VerilogInteger.ZERO;        // return dummy value
    }

    /** INT */
    @Override
    public VerilogInteger visitInt(VCalcParser.IntContext ctx) {
        return VerilogInteger.decode(ctx.INT().getText());
    }

    /** ID */
    @Override
    public VerilogInteger visitId(VCalcParser.IdContext ctx) {
        String id = ctx.ID().getText();
        if ( memory.containsKey(id) ) return memory.get(id);
        return VerilogInteger.ZERO;
    }

    /** expr op=('*'|'/') expr */
    @Override
    public VerilogInteger visitMulDiv(VCalcParser.MulDivContext ctx) {
        VerilogInteger left = visit(ctx.expr(0));  // get value of left subexpression
        VerilogInteger right = visit(ctx.expr(1)); // get value of right subexpression
        if ( ctx.op.getType() == VCalcParser.MUL )
        	return left.multiply(right);
        return left.divide(right); // must be DIV
    }

    /** expr op=('+'|'-') expr */
    @Override
    public VerilogInteger visitAddSub(VCalcParser.AddSubContext ctx) {
    	VerilogInteger left = visit(ctx.expr(0));  // get value of left subexpression
    	VerilogInteger right = visit(ctx.expr(1)); // get value of right subexpression
        if ( ctx.op.getType() == VCalcParser.ADD )
        	return left.add(right);
        return left.subtract(right); // must be SUB
    }

    /** '(' expr ')' */
    @Override
    public VerilogInteger visitParens(VCalcParser.ParensContext ctx) {
        return visit(ctx.expr()); // return child expr's value
    }

}
