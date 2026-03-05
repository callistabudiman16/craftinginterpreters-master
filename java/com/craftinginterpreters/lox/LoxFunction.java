//> Functions lox-function
package com.craftinginterpreters.lox;

import java.util.List;

class LoxFunction implements LoxCallable {
  private final Token name;              // may be null
  private final List<Token> params;
  private final List<Stmt> body;
//> closure-field
  private final Environment closure;
  
//< closure-field
/* Functions lox-function < Functions closure-constructor
  LoxFunction(Stmt.Function declaration) {
*/
/* Functions closure-constructor < Classes is-initializer-field
  LoxFunction(Stmt.Function declaration, Environment closure) {
*/
//> Classes is-initializer-field
  private final boolean isInitializer;

  LoxFunction(Stmt.Function declaration, Environment closure, boolean isInitializer) {
    this.name = declaration.name; 
    this.params = declaration.params;
    this.body = declaration.body;
    this.closure = closure;
    this.isInitializer = isInitializer;
  }

  LoxFunction(Expr.Function expr, Environment closure, boolean isInitializer) {
    this.name = null;
    this.params = expr.params;
    this.body = expr.body;
    this.closure = closure;
    this.isInitializer = isInitializer;
  }
//< Classes is-initializer-field
//> closure-constructor
//> Classes bind-instance
  LoxFunction bind(LoxInstance instance) {
    Environment environment = new Environment(closure);
    environment.define("this", instance);
/* Classes bind-instance < Classes lox-function-bind-with-initializer
    return new LoxFunction(declaration, environment);
*/
//> lox-function-bind-with-initializer
    return new LoxFunction(
            new Stmt.Function(name, params, body),
            environment,
            isInitializer);
//< lox-function-bind-with-initializer
  }
//< Classes bind-instance
//> function-to-string
  @Override
  public String toString() {
    if (name == null) return "<fn>";
    return "<fn " + name.lexeme + ">";
  }
//< function-to-string
//> function-arity
  @Override
  public int arity() {
    return params.size();
}
//< function-arity
//> function-call
  @Override
  public Object call(Interpreter interpreter,
                     List<Object> arguments) {
  
/* Functions function-call < Functions call-closure
    Environment environment = new Environment(interpreter.globals);
*/
//> call-closure
    Environment environment = new Environment(closure);
//< call-closure
    for (int i = 0; i < params.size(); i++) {
  environment.define(params.get(i).lexeme, arguments.get(i));
}

    try {
      interpreter.executeBlock(body, environment);
    } catch (Return returnValue) {
      if (isInitializer) return closure.getAt(0, "this");
      return returnValue.value;
    }

    if (isInitializer) return closure.getAt(0, "this");
    return null;

  }
}
