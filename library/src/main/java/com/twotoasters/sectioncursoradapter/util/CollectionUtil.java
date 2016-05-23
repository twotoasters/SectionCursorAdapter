package com.twotoasters.sectioncursoradapter.util;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class CollectionUtil {

    public static <T> Set<T> createWeakHashSet() {
        return Collections.newSetFromMap(new WeakHashMap<T, Boolean>());
    }
}
