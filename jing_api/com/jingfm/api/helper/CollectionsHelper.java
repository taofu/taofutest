package com.jingfm.api.helper;

import java.util.Collection;

public class CollectionsHelper {
	public static boolean isEmpty(final Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    public static boolean isNotEmpty(final Collection<?> coll) {
        return !isEmpty(coll);
    }
}
