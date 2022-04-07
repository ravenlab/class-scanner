package com.github.ravenlab.classscanner;

import java.util.Collection;

public interface ClassScanner<S> {

    <T> Collection<Class<? extends T>> collect(String packagePattern, S source, Class<? extends T> superClazz);

}