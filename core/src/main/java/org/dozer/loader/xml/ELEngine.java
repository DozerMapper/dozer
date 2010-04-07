package org.dozer.loader.xml;

import javax.el.ExpressionFactory;
import javax.el.CompositeELResolver;
import javax.el.ArrayELResolver;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.BeanELResolver;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Method;

/**
 * @author Dmitry Buzdin
 */
public class ELEngine {

  private final ExpressionFactory factory;
  private final CompositeELResolver resolver;
  private ELContext context;

  public ELEngine() {
    factory = ExpressionFactory.newInstance();

    resolver = new CompositeELResolver();
    resolver.add(new ArrayELResolver());
    resolver.add(new ListELResolver());
    resolver.add(new MapELResolver());
    resolver.add(new BeanELResolver());
  }

  public void init() {
    context = new SimpleContext(resolver);
  }

  public <T> void setVariable(String key, T value) {
    setVariable(key, value, value.getClass());
  }

  public <T> void setVariable(String key, T value, Class<? extends T> type) {
    VariableMapper variableMapper = context.getVariableMapper();
    ValueExpression valueExpression = factory.createValueExpression(value, type);
    variableMapper.setVariable(key, valueExpression);
  }

  public void setFunction(String prefix, Method method) {
    Functions functions = (Functions) context.getFunctionMapper();
    functions.setFunction(prefix, method.getName(), method);
  }

  public void setFunction(String prefix, String name, Method method) {
    Functions functions = (Functions) context.getFunctionMapper();
    functions.setFunction(prefix, name, method);
  }

  public String resolve(String expression) {
    ValueExpression expr = factory.createValueExpression(context, expression, String.class);
    return (String) expr.getValue(context);
  }

  static class SimpleContext extends ELContext {

    private Functions functions = new Functions();
    private Variables variables = new Variables();
    private ELResolver resolver;

    SimpleContext(ELResolver resolver) {
      this.resolver = resolver;
    }

    public ELResolver getELResolver() {
      return resolver;
    }

    @Override
    public FunctionMapper getFunctionMapper() {
      return functions;
    }

    @Override
    public VariableMapper getVariableMapper() {
      return variables;
    }

  }

  static class Functions extends FunctionMapper {
    final Map<String, Method> map = new HashMap<String, Method>();

    @Override
    public Method resolveFunction(String prefix, String localName) {
      return map.get(prefix + ":" + localName);
    }

    public void setFunction(String prefix, String localName, Method method) {
      map.put(prefix + ":" + localName, method);
    }
  }

  static class Variables extends VariableMapper {
    final Map<String, ValueExpression> map = new HashMap<String, ValueExpression>();

    @Override
    public ValueExpression resolveVariable(String variable) {
      return map.get(variable);
    }

    @Override
    public ValueExpression setVariable(String variable, ValueExpression expression) {
      return map.put(variable, expression);
    }
  }


}
