package com.craftinginterpreters.lox;

import java.util.List;

class LoxFunction implements LoxCallable {
  private final Stmt.Function declaration;   

  private final Token name;                
  private final List<Token> params;
  private final List<Stmt> body;

  private final Environment closure;
  private final boolean isInitializer;
  private final boolean isGetter;

  // Named functions / methods / getters
  LoxFunction(Stmt.Function declaration, Environment closure,
              boolean isInitializer, boolean isGetter) {
    this.declaration = declaration;
    this.name = declaration.name;
    this.params = declaration.params;
    this.body = declaration.body;

    this.closure = closure;
    this.isInitializer = isInitializer;
    this.isGetter = isGetter;
  }


  LoxFunction(Expr.Function expr, Environment closure) {
    this.declaration = null;
    this.name = null;
    this.params = expr.params;
    this.body = expr.body;

    this.closure = closure;
    this.isInitializer = false;
    this.isGetter = false;
  }

   boolean isGetter() {
    return isGetter;
    }

  LoxFunction bind(LoxInstance instance) {
    Environment environment = new Environment(closure);
    environment.define("this", instance);

    return new LoxFunction(declaration, environment, isInitializer, isGetter);
  }



  @Override
  public int arity() {
    if (isGetter) return 0;
    return params.size();
  }

  @Override
  public Object call(Interpreter interpreter, List<Object> arguments) {
    Environment environment = new Environment(closure);

    // getters take 0 args, params will be empty
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