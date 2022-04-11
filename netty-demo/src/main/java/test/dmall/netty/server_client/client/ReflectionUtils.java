package test.dmall.netty.server_client.client;

import java.lang.reflect.Field;

public class ReflectionUtils {
    public static Field findField(Class<?> clazz, String name) {
        return findField(clazz, name, (Class)null);
    }

    public static Field findField(Class<?> clazz, String name, Class<?> type) {

        for(Class searchType = clazz; Object.class != searchType && searchType != null; searchType = searchType.getSuperclass()) {
            Field[] fields = getDeclaredFields(searchType);
            Field[] var5 = fields;
            int var6 = fields.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                Field field = var5[var7];
                if ((name == null || name.equals(field.getName())) && (type == null || type.equals(field.getType()))) {
                    return field;
                }
            }
        }

        return null;
    }

    private static Field[] getDeclaredFields(Class<?> clazz) {
        Field[] result;
        try {
            result = clazz.getDeclaredFields();
        } catch (Throwable var3) {
            throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() + "] from ClassLoader [" + clazz.getClassLoader() + "]", var3);
        }
        return result;
    }
}
