package com.company;
/*
 * Создание коллекции Set
 * С полной возможнстью сериализации и десериализации,
 * А также полным клонированием map и множества
 */

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class MySet<E> extends AbstractSet<E> implements Set<E>, Cloneable, Serializable {
    private static final Object PRESENT = new Object();
    private transient HashMap<E, Object> map;

    public MySet() {
        this.map = new HashMap<>();
    }
    public MySet(Collection<? extends E> collection) {
        this.map = new HashMap<>(Math.max((int) (collection.size() / .75f) + 1, 16));
        addAll(collection);
    }


    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @Override
    public boolean remove(Object o) {
        return map.remove(o)==PRESENT;
    }

    @Override
    public void clear() {
        map.clear();
    }

    public boolean add(E e) {
        return map.put(e, PRESENT) == null;
    }

    @Override
    public Object clone() {
        MySet<E> newSet = null;
        try {
            newSet = (MySet<E>) super.clone();
            newSet.map = (HashMap<E, Object>) map.clone();
            return newSet;
        } catch (Exception e) {
            throw new InternalError();
        }

    }

    private void writeObject(ObjectOutputStream s) throws java.io.IOException {
        s.defaultWriteObject();
        s.writeInt(callHiddenMethod(map, "capacity"));
        s.writeFloat(callHiddenMethod(map, "loadFactor"));
        s.writeInt(map.size());
        for (E e : map.keySet())
            s.writeObject(e);
    }


    private void readObject(ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();

        int capacity = s.readInt();
        float loadFactor = s.readFloat();
        map = new HashMap<>(capacity, loadFactor);

        int size = s.readInt();

        for (int i = 0; i < size; i++) {
            E e = (E) s.readObject();
            map.put(e, PRESENT);
        }
    }


    public static <T> T callHiddenMethod(HashMap map, String methodName) {
        try {
            Method method = map.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            return (T) method.invoke(map);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
        }
        return null;
    }

}
