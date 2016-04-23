import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Lambda implements Expression {
  
	Expression e1;
	Expression e2;
    public Lambda(Expression e1, Expression e2) {
    	this.e1 = e1;
    	this.e2 = e2;
    }
    
    public String printExp() {
		return "(\\" + e1.printExp() + "." + e2.printExp() + ")";
	} 
    public List<String> freeVariables(List<String> cur) {
//    	List<String> res = e1.freeVariables(cur);
    	List<String> res = new ArrayList<>();
    	List<String> curr = new ArrayList<String>(cur);
    	curr.add(e1.printExp());
    	res.addAll(e2.freeVariables(curr));
    	return res;
    }
	public String printExp2() {
		return printExp();
	}
    public Expression substitution(List<String> booked, Expression var, Expression sub) {
    	List<String> cur = new ArrayList<String>(booked);
    	cur.add(e1.printExp());
    	return new Lambda(e1.createCopy(), e2.substitution(cur, var, sub));
    }

	public Expression substitution2(List<String> booked, Expression var, Expression sub) {
		List<String> cur = new ArrayList<String>(booked);
		cur.add(e1.printExp());
		e2 = e2.substitution2(booked, var, sub);
		return this;
	}

	public void getSetEquations(Map<Expression, Expression> types, List<Expression> equations, List<Integer> cnt) {
		Map<Expression, Expression> types2 = new HashMap<>(types);
		e1.getSetEquations(types2, equations, cnt);
		e2.getSetEquations(types2, equations, cnt);
		Expression type_e1 = null;
		Expression type_e2 = null;
		for (Expression exp: types2.keySet()) {
			if (this.isEqual(exp)) return;
			if (e1.isEqual(exp)) type_e1 = types2.get(exp).createCopy();
			if (e2.isEqual(exp)) type_e2 = types2.get(exp).createCopy();
		}
		if (type_e1 == null || type_e2 == null) {
			System.err.println("Application e1 or e2 is equal to null");
			return;
		}
		List<Expression> lst = new ArrayList<>();
		lst.add(type_e1);
		lst.add(type_e2);
		types.put(this.createCopy(), new Function("f2", lst));
	}
	public boolean isEqual(Expression b) {
		if (b instanceof Lambda) {
			if (((Lambda) b).e1.isEqual(e1) && ((Lambda) b).e2.isEqual(e2)) return true;
		}
		return false;
	}

	public Expression replace(Expression a, Expression b) {
		if (this.isEqual(a)) {
			return b.createCopy();
		}
		return this.createCopy();
	}

	public Expression createCopy() {
		return new Lambda(e1.createCopy(), e2.createCopy());
	}

	public Expression getNormalForm(Map<String, Expression> normals, Map<String, Expression> headNormals) {
//		System.err.println("lambda norm");
		String printStr = this.printExp();
		if (normals.containsKey(printStr)) {
			return normals.get(printStr).createCopy();
		}
		Expression a = e1;
		Expression b = e2.createCopy();
		List<Expression> exps = new ArrayList<>();
		while (!b.isNormalForm()) {
			exps.add(b);
			b = b.getNormalForm(normals, headNormals);
		}
		Expression res = new Lambda(a, b);
		for (Expression e: exps) normals.put(e.printExp(), b);
		normals.put(printStr, res);
		return res;
	}

	public Expression getHeadNormalForm(Map<String, Expression> normals, Map<String, Expression> headNormals) {
//		System.err.println("lambda head");
		return this;
	}

	public boolean isNormalForm() {
		return e2.isNormalForm();
	}
}