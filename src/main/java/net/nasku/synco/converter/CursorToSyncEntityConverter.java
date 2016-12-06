package net.nasku.synco.converter;

import android.database.Cursor;

import net.nasku.synco.converter.interf.Converter;
import net.nasku.synco.model.SyncEntity;

/**
 * Created by Seven on 19.09.2016.
 */
public class CursorToSyncEntityConverter implements Converter<Cursor,SyncEntity> {

    @Override
    public SyncEntity convert(final Cursor cursor) {
        final SyncEntity syncEntity = new SyncEntity();
        final String[] keys = cursor.getColumnNames();
        for(final String key : keys) {
            final int index = cursor.getColumnIndex(key);
            final int type = cursor.getType(index);
            switch(type) {
                case Cursor.FIELD_TYPE_NULL:
                    syncEntity.put(key, null);
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    syncEntity.put(key, cursor.getString(index));
                    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    syncEntity.put(key, cursor.getLong(index));
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    syncEntity.put(key,cursor.getDouble(index));
                    break;
            }
        }
        return syncEntity;
    }
}
