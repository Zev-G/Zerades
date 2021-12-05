package tmw.me.com.app.tools.builders.reflect;

import tmw.me.com.app.tools.builders.Builder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

public class ReflectionBuilder<T> extends Builder<T> {

    private Class<T> genericClass;
    private T base;
    private final ArrayList<MethodGrouping> methodGroupings = new ArrayList<>();
    private final ArrayList<Object> constructorParameters = new ArrayList<>();

    public ReflectionBuilder(Class<T> aClass) {
        genericClass = aClass;
    }
    public ReflectionBuilder(T base) {
        this.base = base;
        genericClass = (Class<T>) base.getClass();
    }

    public static <T> ReflectionBuilder<T> create(Class<T> genericClass) {
        return new ReflectionBuilder<>(genericClass);
    }

    public ReflectionBuilder<T> addMethod(String name, Object... paramValues) {
        methodGroupings.add(new MethodGrouping(name, paramValues));
        return this;
    }

    public ReflectionBuilder<T> addConstructorParameters(Object... paramObjects) {
        constructorParameters.addAll(Arrays.asList(paramObjects));
        return this;
    }

    public T build() {
        Class<?>[] constructorClasses = new Class[constructorParameters.size()];
        int loops = 0;
        for (Object paramObj : constructorParameters) {
            constructorClasses[loops] = paramObj.getClass();
            loops++;
        }
        T obj = base;
        if (obj == null) {
            try {
                obj = genericClass.getConstructor(constructorClasses).newInstance(constructorParameters.toArray());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        for (MethodGrouping methodGrouping : methodGroupings) {
            Class<?>[] classes = new Class[methodGrouping.getObjects().length];
            int i = 0;
            for (Object paramObj : methodGrouping.getObjects()) {
                classes[i] = paramObj.getClass();
                i++;
            }
            Method method;
            try {
                method = genericClass.getMethod(methodGrouping.getName(), classes);
            } catch (NoSuchMethodException e) {
                continue;
            }
            try {
                method.invoke(obj, methodGrouping.getObjects());
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return obj;
    }

    private static class MethodGrouping {

        private Object[] objects;
        private String name;

        public MethodGrouping(String name, Object... objects) {
            this.objects = objects;
            this.name = name;
        }
        public MethodGrouping(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public Object[] getObjects() {
            return objects;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setObjects(Object... objects) {
            this.objects = objects;
        }
    }

}
