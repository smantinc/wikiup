package org.wikiup.core.impl.setter;

import org.wikiup.core.inf.Setter;

import java.util.Stack;

public class StackSetter<E> implements Setter<E> {
    private Stack<Setter<E>> stack = new Stack<Setter<E>>();

    public StackSetter() {
    }

    public StackSetter(Setter<E> setter) {
        push(setter);
    }

    public StackSetter(Setter<E> a1, Setter<E> a2) {
        push(a1);
        push(a2);
    }

    public void push(Setter<E> setter) {
        stack.add(setter);
    }

    public Setter<E> pop() {
        return stack.pop();
    }

    public void set(String name, E value) {
        if(!stack.isEmpty())
            stack.peek().set(name, value);
    }
}
