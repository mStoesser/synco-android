package net.nasku.synco.endpoint;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.nasku.synco.Sync;
import net.nasku.synco.SyncConfig;
import net.nasku.synco.converter.interf.Converter;
import net.nasku.synco.converter.interf.ConverterList;
import net.nasku.synco.endpoint.interf.Endpoint;
import net.nasku.synco.model.HttpPostVar;
import net.nasku.synco.model.SyncEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by Seven on 19.09.2016.
 */
public class CursorEndpoint implements Endpoint {

    SQLiteDatabase readableDatabase;
    SQLiteDatabase writableDatabase;
    SQLiteOpenHelper sqLiteOpenHelper;

    String table;
    String[] columns;
    String dirtyCondition;
    String[] dirtyConditionArgs;
    String orderBy;

    Converter<SyncEntity,ContentValues> pushConverter;
    Converter<Cursor,SyncEntity> pullConverter;
    Converter<Cursor,List<SyncEntity>> pullConverterList;

    public CursorEndpoint(SyncConfig config) {

        table = config.getString("table", null);
        columns = config.getStringArray("columns", null);
        dirtyCondition = config.getString("dirtyCondition", null);
        dirtyConditionArgs = config.getStringArray("dirtyConditionArgs", null);
        orderBy = config.getString("orderBy", null);

        sqLiteOpenHelper = (SQLiteOpenHelper) config.getObject("sqLiteOpenHelper", null);
        readableDatabase = (SQLiteDatabase) config.getObject("readableDatabase", null);
        writableDatabase = (SQLiteDatabase) config.getObject("writableDatabase", null);

        final String pullConverterNames = config.getString("pullConverter", null);
        if(null != pullConverterNames) {
            Converter converter = Sync.getConverterByName(pullConverterNames.split(","));
            if(converter instanceof ConverterList)
                pullConverterList = converter;
            else if(converter instanceof Converter)
                pullConverter = converter;
        }
        if(null == pullConverterList)
            pullConverterList = Sync.getConverterListByType(Cursor.class, SyncEntity.class);
        if(null == pullConverter)
            pullConverter = Sync.getConverterByType(Cursor.class, SyncEntity.class);
        pushConverter = Sync.getConverterByName(config.getString("pushConverter", "").split(","));
        if(null == pushConverter)
            pushConverter = Sync.getConverterByType(SyncEntity.class, ContentValues.class);
    }

    @Override
    public void pull(List<SyncEntity> entities, int limit) {

        if(null == table) {
            return;
        }

        SQLiteDatabase database = null == this.readableDatabase ? sqLiteOpenHelper.getReadableDatabase() : this.readableDatabase;

        if (null == dirtyCondition) {
            analyzeTable(database);

        }
        Cursor cursor = database.query(table, columns, dirtyCondition, dirtyConditionArgs, null, null, orderBy, "" + limit);

        if (null != pullConverterList) {

            entities.addAll(pullConverterList.convert(cursor));

        } else if (null != pullConverter) {

            while (cursor.moveToNext()) {
                entities.add(pullConverter.convert(cursor));
            }
        }
    }

    protected void analyzeTable(final SQLiteDatabase database) {
        final Cursor cursor = database.rawQuery("PRAGMA table_info("+table+");", null);
        while(cursor.moveToNext()) {
            final String columnName = cursor.getString(1);
            if(null != columnName) {
                if (null == dirtyCondition && columnName.contains("dirty")) {
                    dirtyCondition = columnName + " > 0"; //TODO: add null clause and verify type
                }
            }
        }
        cursor.close();
    }

    @Override
    public void push(List<SyncEntity> entities) {

        SQLiteDatabase database = null == this.writableDatabase? sqLiteOpenHelper.getWritableDatabase() : this.writableDatabase;

        for(SyncEntity syncEntity : entities) {
            database.replace(table, null, pushConverter.convert(syncEntity));
        }

    }
}
