package com.nuls.naboxpro.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.nuls.naboxpro.entity.AuthorizationBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "AUTHORIZATION_BEAN".
*/
public class AuthorizationBeanDao extends AbstractDao<AuthorizationBean, Void> {

    public static final String TABLENAME = "AUTHORIZATION_BEAN";

    /**
     * Properties of entity AuthorizationBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Url = new Property(0, String.class, "url", false, "URL");
        public final static Property Chain = new Property(1, String.class, "chain", false, "CHAIN");
        public final static Property Address = new Property(2, String.class, "address", false, "ADDRESS");
        public final static Property IsAuth = new Property(3, boolean.class, "isAuth", false, "IS_AUTH");
        public final static Property IsLongAuth = new Property(4, boolean.class, "isLongAuth", false, "IS_LONG_AUTH");
    }


    public AuthorizationBeanDao(DaoConfig config) {
        super(config);
    }
    
    public AuthorizationBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"AUTHORIZATION_BEAN\" (" + //
                "\"URL\" TEXT," + // 0: url
                "\"CHAIN\" TEXT," + // 1: chain
                "\"ADDRESS\" TEXT," + // 2: address
                "\"IS_AUTH\" INTEGER NOT NULL ," + // 3: isAuth
                "\"IS_LONG_AUTH\" INTEGER NOT NULL );"); // 4: isLongAuth
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"AUTHORIZATION_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, AuthorizationBean entity) {
        stmt.clearBindings();
 
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(1, url);
        }
 
        String chain = entity.getChain();
        if (chain != null) {
            stmt.bindString(2, chain);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(3, address);
        }
        stmt.bindLong(4, entity.getIsAuth() ? 1L: 0L);
        stmt.bindLong(5, entity.getIsLongAuth() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, AuthorizationBean entity) {
        stmt.clearBindings();
 
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(1, url);
        }
 
        String chain = entity.getChain();
        if (chain != null) {
            stmt.bindString(2, chain);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(3, address);
        }
        stmt.bindLong(4, entity.getIsAuth() ? 1L: 0L);
        stmt.bindLong(5, entity.getIsLongAuth() ? 1L: 0L);
    }

    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    @Override
    public AuthorizationBean readEntity(Cursor cursor, int offset) {
        AuthorizationBean entity = new AuthorizationBean( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // url
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // chain
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // address
            cursor.getShort(offset + 3) != 0, // isAuth
            cursor.getShort(offset + 4) != 0 // isLongAuth
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, AuthorizationBean entity, int offset) {
        entity.setUrl(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setChain(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setAddress(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setIsAuth(cursor.getShort(offset + 3) != 0);
        entity.setIsLongAuth(cursor.getShort(offset + 4) != 0);
     }
    
    @Override
    protected final Void updateKeyAfterInsert(AuthorizationBean entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    @Override
    public Void getKey(AuthorizationBean entity) {
        return null;
    }

    @Override
    public boolean hasKey(AuthorizationBean entity) {
        // TODO
        return false;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}