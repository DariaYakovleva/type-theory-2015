import java.util.List;
import java.util.Map;

public interface Expression {
    public Expression createCopy();
    public String printExp();
    public String printExp2();
    public List<String> freeVariables(List<String> cur);
    public Expression substitution(List<String> booked, Expression var, Expression sub); //replace with copy
    public Expression substitution2(List<String> booked, Expression var, Expression sub); //replace without copy
    public void getSetEquations(Map<Expression, Expression> types, List<Expression> equations, List<Integer> cnt);
    public boolean isEqual(Expression b);
    public Expression replace(Expression a, Expression b);
    public Expression getNormalForm(Map<String, Expression> normals, Map<String, Expression> headNormals);
    public Expression getHeadNormalForm(Map<String, Expression> normals, Map<String, Expression> headNormals);
    public boolean isNormalForm();
}
