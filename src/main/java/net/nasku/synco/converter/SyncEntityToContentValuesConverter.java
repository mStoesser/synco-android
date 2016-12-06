package net.nasku.synco.converter;

import android.content.ContentValues;

import net.nasku.synco.converter.interf.Converter;
import net.nasku.synco.model.SyncEntity;

/**
 * Created by Seven on 19.09.2016.
 */
public class SyncEntityToContentValuesConverter implements Converter<SyncEntity,ContentValues> {

    @Override
    public ContentValues convert(SyncEntity syncEntity) {

        final ContentValues contentValues = new ContentValues();
        for(final String key : syncEntity.keySet()) {
            final Object value = syncEntity.get(key);
            if(value instanceof String) {
                contentValues.put(key, (String)value);
            } else if(value instanceof Short) {
                contentValues.put(key, (Short)value);
            } else if(value instanceof Integer) {
                contentValues.put(key, (Integer)value);
            } else if(value instanceof Long) {
                contentValues.put(key, (Long)value);
            } else if(value instanceof Float) {
                contentValues.put(key, (Float)value);
            } else if(value instanceof Double) {
                contentValues.put(key, (Double)value);
            } else if(value instanceof Boolean) {
                contentValues.put(key, (Boolean)value);
            } else if(value instanceof byte[]) {
                contentValues.put(key, (byte[])value);
            }
        }
        return contentValues;
    }


}
