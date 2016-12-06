package net.nasku.synco.converter;

import android.database.Cursor;

import net.nasku.synco.Sync;
import net.nasku.synco.converter.interf.Converter;
import net.nasku.synco.converter.interf.ConverterList;
import net.nasku.synco.model.SyncEntity;

import java.util.List;
import java.util.Vector;

/**
 * Created by Seven on 19.09.2016.
 */
public class CursorToSyncEntityConverterList implements ConverterList<Cursor, SyncEntity> {

    Converter<Cursor,SyncEntity> cursorSyncEntityConverter;

    public CursorToSyncEntityConverterList() {
        this.cursorSyncEntityConverter = Sync.getConverterByType(Cursor.class, SyncEntity.class);
    }

    @Override
    public List<SyncEntity> convert(Cursor cursor) {
        List<SyncEntity> entities = new Vector<>();
        while(cursor.moveToNext()) {
            entities.add(cursorSyncEntityConverter.convert(cursor));
        }
        return entities;
    }
}
