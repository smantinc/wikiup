package org.wikiup.core.impl.setter;

import org.wikiup.core.inf.Dictionary;

import java.util.Stack;

public class StackSetter<E> implements Dictionary.Mutable<E> {
    private Stack<Dictionary.Mutable<E>> stack = new Stack<Dictionary.Mutable<E>>();

    public StackSetter() {
    }

    public StackSetter(Dictionary.Mutable<E> mutable) {
        push(mutable);
    }

    public StackSetter(Dictionary.Mutable<E> a1, Dictionary.Mutable<E> a2) {
        push(a1);
        push(a2);
    }

    public void push(Dictionary.Mutable<E> mutable) {
        stack.add(mutable);
    }

    public Dictionary.Mutable<E> pop() {
        return stack.pop();
    }

    public void set(String name, E value) {
        if(!stack.isEmpty())
            stack.peek().set(name, value);
    }
}
