package com.craftinginterpreters.lox;

import java.util.List;
import java.util.Map;

class LoxClass extends LoxInstance implements LoxCallable {
  final String name;
  private final Map<String, LoxFunction> methods;
  LoxClass(String name, Map<String, LoxFunction> methods, Map<String, LoxFunction> classMethods) {
    super(null);                     
    this.name = name;
    this.methods = methods;


    if (classMethods != null) {
      for (Map.Entry<String, LoxFunction> e : classMethods.entrySet()) {

        this.fields.put(e.getKey(), e.getValue());
      }
    }
  }

  @Override
  public Object call(Interpreter interpreter, List<Object> arguments) {

    LoxInstance instance = new LoxInstance(this);

    LoxFunction initializer = findMethod("init");
    if (initializer != null) {
      initializer.bind(instance).call(interpreter, arguments);
    }

    return instance;
  }

  @Override
  public int arity() {
    LoxFunction initializer = findMethod("init");
    if (initializer == null) return 0;
    return initializer.arity();
  }

  LoxFunction findMethod(String name) {
    if (methods == null) return null;
    return methods.get(name);
  }

  @Override
  public String toString() {
    return name;
  }
}