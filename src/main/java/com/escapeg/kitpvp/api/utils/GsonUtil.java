package com.escapeg.kitpvp.api.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public final class GsonUtil {

    private static final GsonBuilder gsonBuilder = new GsonBuilder().disableHtmlEscaping().serializeNulls().serializeSpecialFloatingPointValues().disableInnerClassSerialization();

    public static GsonBuilder getGsonBuilder() {
        return GsonUtil.gsonBuilder;
    }

    public static Gson getGson() {
        return GsonUtil.gsonBuilder.create();
    }

    public static Gson getGson(final boolean prettyPrinting){
        Gson resultGson;
        if (prettyPrinting) {
            GsonUtil.getGsonBuilder().setPrettyPrinting();
            resultGson = GsonUtil.getGsonBuilder().create();
            try {
                Field pretty = GsonUtil.getGsonBuilder().getClass().getDeclaredField("prettyPrinting");
                pretty.setAccessible(true);
                pretty.setBoolean(GsonUtil.getGsonBuilder(), false);
            } catch (final NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            resultGson = GsonUtil.getGsonBuilder().create();
        }
        return resultGson;
    }

    public static void registerTypeHierarchyAdapter(final Class<?> baseType, final Object typeAdapter){
        GsonUtil.gsonBuilder.registerTypeHierarchyAdapter(baseType, typeAdapter);
    }

    public static void registerTypeAdapter(final Type type, final Object typeAdapter){
        GsonUtil.gsonBuilder.registerTypeAdapter(type, typeAdapter);
    }

    public static void registerTypeHierarchyAdapter(final TypeAdapterFactory factory){
        GsonUtil.gsonBuilder.registerTypeAdapterFactory(factory);
    }
}
