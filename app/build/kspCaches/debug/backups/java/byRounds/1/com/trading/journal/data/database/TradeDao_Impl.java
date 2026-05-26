package com.trading.journal.data.database;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TradeDao_Impl implements TradeDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TradeEntity> __insertionAdapterOfTradeEntity;

  private final EntityDeletionOrUpdateAdapter<TradeEntity> __deletionAdapterOfTradeEntity;

  private final EntityDeletionOrUpdateAdapter<TradeEntity> __updateAdapterOfTradeEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public TradeDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTradeEntity = new EntityInsertionAdapter<TradeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `trades` (`id`,`symbol`,`direction`,`market`,`entryPrice`,`exitPrice`,`quantity`,`entryDate`,`exitDate`,`fees`,`stopLoss`,`takeProfit`,`tags`,`notes`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TradeEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getSymbol());
        statement.bindString(3, entity.getDirection());
        statement.bindString(4, entity.getMarket());
        statement.bindDouble(5, entity.getEntryPrice());
        if (entity.getExitPrice() == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.getExitPrice());
        }
        statement.bindDouble(7, entity.getQuantity());
        statement.bindLong(8, entity.getEntryDate());
        if (entity.getExitDate() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getExitDate());
        }
        statement.bindDouble(10, entity.getFees());
        if (entity.getStopLoss() == null) {
          statement.bindNull(11);
        } else {
          statement.bindDouble(11, entity.getStopLoss());
        }
        if (entity.getTakeProfit() == null) {
          statement.bindNull(12);
        } else {
          statement.bindDouble(12, entity.getTakeProfit());
        }
        statement.bindString(13, entity.getTags());
        statement.bindString(14, entity.getNotes());
      }
    };
    this.__deletionAdapterOfTradeEntity = new EntityDeletionOrUpdateAdapter<TradeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `trades` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TradeEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfTradeEntity = new EntityDeletionOrUpdateAdapter<TradeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `trades` SET `id` = ?,`symbol` = ?,`direction` = ?,`market` = ?,`entryPrice` = ?,`exitPrice` = ?,`quantity` = ?,`entryDate` = ?,`exitDate` = ?,`fees` = ?,`stopLoss` = ?,`takeProfit` = ?,`tags` = ?,`notes` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TradeEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getSymbol());
        statement.bindString(3, entity.getDirection());
        statement.bindString(4, entity.getMarket());
        statement.bindDouble(5, entity.getEntryPrice());
        if (entity.getExitPrice() == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.getExitPrice());
        }
        statement.bindDouble(7, entity.getQuantity());
        statement.bindLong(8, entity.getEntryDate());
        if (entity.getExitDate() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getExitDate());
        }
        statement.bindDouble(10, entity.getFees());
        if (entity.getStopLoss() == null) {
          statement.bindNull(11);
        } else {
          statement.bindDouble(11, entity.getStopLoss());
        }
        if (entity.getTakeProfit() == null) {
          statement.bindNull(12);
        } else {
          statement.bindDouble(12, entity.getTakeProfit());
        }
        statement.bindString(13, entity.getTags());
        statement.bindString(14, entity.getNotes());
        statement.bindLong(15, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM trades WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertTrade(final TradeEntity trade, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTradeEntity.insertAndReturnId(trade);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTrade(final TradeEntity trade, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfTradeEntity.handle(trade);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTrade(final TradeEntity trade, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTradeEntity.handle(trade);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TradeEntity>> getAllTrades() {
    final String _sql = "SELECT * FROM trades ORDER BY entryDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"trades"}, new Callable<List<TradeEntity>>() {
      @Override
      @NonNull
      public List<TradeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSymbol = CursorUtil.getColumnIndexOrThrow(_cursor, "symbol");
          final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
          final int _cursorIndexOfMarket = CursorUtil.getColumnIndexOrThrow(_cursor, "market");
          final int _cursorIndexOfEntryPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "entryPrice");
          final int _cursorIndexOfExitPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "exitPrice");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfEntryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "entryDate");
          final int _cursorIndexOfExitDate = CursorUtil.getColumnIndexOrThrow(_cursor, "exitDate");
          final int _cursorIndexOfFees = CursorUtil.getColumnIndexOrThrow(_cursor, "fees");
          final int _cursorIndexOfStopLoss = CursorUtil.getColumnIndexOrThrow(_cursor, "stopLoss");
          final int _cursorIndexOfTakeProfit = CursorUtil.getColumnIndexOrThrow(_cursor, "takeProfit");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<TradeEntity> _result = new ArrayList<TradeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TradeEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSymbol;
            _tmpSymbol = _cursor.getString(_cursorIndexOfSymbol);
            final String _tmpDirection;
            _tmpDirection = _cursor.getString(_cursorIndexOfDirection);
            final String _tmpMarket;
            _tmpMarket = _cursor.getString(_cursorIndexOfMarket);
            final double _tmpEntryPrice;
            _tmpEntryPrice = _cursor.getDouble(_cursorIndexOfEntryPrice);
            final Double _tmpExitPrice;
            if (_cursor.isNull(_cursorIndexOfExitPrice)) {
              _tmpExitPrice = null;
            } else {
              _tmpExitPrice = _cursor.getDouble(_cursorIndexOfExitPrice);
            }
            final double _tmpQuantity;
            _tmpQuantity = _cursor.getDouble(_cursorIndexOfQuantity);
            final long _tmpEntryDate;
            _tmpEntryDate = _cursor.getLong(_cursorIndexOfEntryDate);
            final Long _tmpExitDate;
            if (_cursor.isNull(_cursorIndexOfExitDate)) {
              _tmpExitDate = null;
            } else {
              _tmpExitDate = _cursor.getLong(_cursorIndexOfExitDate);
            }
            final double _tmpFees;
            _tmpFees = _cursor.getDouble(_cursorIndexOfFees);
            final Double _tmpStopLoss;
            if (_cursor.isNull(_cursorIndexOfStopLoss)) {
              _tmpStopLoss = null;
            } else {
              _tmpStopLoss = _cursor.getDouble(_cursorIndexOfStopLoss);
            }
            final Double _tmpTakeProfit;
            if (_cursor.isNull(_cursorIndexOfTakeProfit)) {
              _tmpTakeProfit = null;
            } else {
              _tmpTakeProfit = _cursor.getDouble(_cursorIndexOfTakeProfit);
            }
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            _item = new TradeEntity(_tmpId,_tmpSymbol,_tmpDirection,_tmpMarket,_tmpEntryPrice,_tmpExitPrice,_tmpQuantity,_tmpEntryDate,_tmpExitDate,_tmpFees,_tmpStopLoss,_tmpTakeProfit,_tmpTags,_tmpNotes);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getTradeById(final long id, final Continuation<? super TradeEntity> $completion) {
    final String _sql = "SELECT * FROM trades WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TradeEntity>() {
      @Override
      @Nullable
      public TradeEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSymbol = CursorUtil.getColumnIndexOrThrow(_cursor, "symbol");
          final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
          final int _cursorIndexOfMarket = CursorUtil.getColumnIndexOrThrow(_cursor, "market");
          final int _cursorIndexOfEntryPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "entryPrice");
          final int _cursorIndexOfExitPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "exitPrice");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfEntryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "entryDate");
          final int _cursorIndexOfExitDate = CursorUtil.getColumnIndexOrThrow(_cursor, "exitDate");
          final int _cursorIndexOfFees = CursorUtil.getColumnIndexOrThrow(_cursor, "fees");
          final int _cursorIndexOfStopLoss = CursorUtil.getColumnIndexOrThrow(_cursor, "stopLoss");
          final int _cursorIndexOfTakeProfit = CursorUtil.getColumnIndexOrThrow(_cursor, "takeProfit");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final TradeEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSymbol;
            _tmpSymbol = _cursor.getString(_cursorIndexOfSymbol);
            final String _tmpDirection;
            _tmpDirection = _cursor.getString(_cursorIndexOfDirection);
            final String _tmpMarket;
            _tmpMarket = _cursor.getString(_cursorIndexOfMarket);
            final double _tmpEntryPrice;
            _tmpEntryPrice = _cursor.getDouble(_cursorIndexOfEntryPrice);
            final Double _tmpExitPrice;
            if (_cursor.isNull(_cursorIndexOfExitPrice)) {
              _tmpExitPrice = null;
            } else {
              _tmpExitPrice = _cursor.getDouble(_cursorIndexOfExitPrice);
            }
            final double _tmpQuantity;
            _tmpQuantity = _cursor.getDouble(_cursorIndexOfQuantity);
            final long _tmpEntryDate;
            _tmpEntryDate = _cursor.getLong(_cursorIndexOfEntryDate);
            final Long _tmpExitDate;
            if (_cursor.isNull(_cursorIndexOfExitDate)) {
              _tmpExitDate = null;
            } else {
              _tmpExitDate = _cursor.getLong(_cursorIndexOfExitDate);
            }
            final double _tmpFees;
            _tmpFees = _cursor.getDouble(_cursorIndexOfFees);
            final Double _tmpStopLoss;
            if (_cursor.isNull(_cursorIndexOfStopLoss)) {
              _tmpStopLoss = null;
            } else {
              _tmpStopLoss = _cursor.getDouble(_cursorIndexOfStopLoss);
            }
            final Double _tmpTakeProfit;
            if (_cursor.isNull(_cursorIndexOfTakeProfit)) {
              _tmpTakeProfit = null;
            } else {
              _tmpTakeProfit = _cursor.getDouble(_cursorIndexOfTakeProfit);
            }
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            _result = new TradeEntity(_tmpId,_tmpSymbol,_tmpDirection,_tmpMarket,_tmpEntryPrice,_tmpExitPrice,_tmpQuantity,_tmpEntryDate,_tmpExitDate,_tmpFees,_tmpStopLoss,_tmpTakeProfit,_tmpTags,_tmpNotes);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TradeEntity>> getOpenTrades() {
    final String _sql = "SELECT * FROM trades WHERE exitPrice IS NULL ORDER BY entryDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"trades"}, new Callable<List<TradeEntity>>() {
      @Override
      @NonNull
      public List<TradeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSymbol = CursorUtil.getColumnIndexOrThrow(_cursor, "symbol");
          final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
          final int _cursorIndexOfMarket = CursorUtil.getColumnIndexOrThrow(_cursor, "market");
          final int _cursorIndexOfEntryPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "entryPrice");
          final int _cursorIndexOfExitPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "exitPrice");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfEntryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "entryDate");
          final int _cursorIndexOfExitDate = CursorUtil.getColumnIndexOrThrow(_cursor, "exitDate");
          final int _cursorIndexOfFees = CursorUtil.getColumnIndexOrThrow(_cursor, "fees");
          final int _cursorIndexOfStopLoss = CursorUtil.getColumnIndexOrThrow(_cursor, "stopLoss");
          final int _cursorIndexOfTakeProfit = CursorUtil.getColumnIndexOrThrow(_cursor, "takeProfit");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<TradeEntity> _result = new ArrayList<TradeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TradeEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSymbol;
            _tmpSymbol = _cursor.getString(_cursorIndexOfSymbol);
            final String _tmpDirection;
            _tmpDirection = _cursor.getString(_cursorIndexOfDirection);
            final String _tmpMarket;
            _tmpMarket = _cursor.getString(_cursorIndexOfMarket);
            final double _tmpEntryPrice;
            _tmpEntryPrice = _cursor.getDouble(_cursorIndexOfEntryPrice);
            final Double _tmpExitPrice;
            if (_cursor.isNull(_cursorIndexOfExitPrice)) {
              _tmpExitPrice = null;
            } else {
              _tmpExitPrice = _cursor.getDouble(_cursorIndexOfExitPrice);
            }
            final double _tmpQuantity;
            _tmpQuantity = _cursor.getDouble(_cursorIndexOfQuantity);
            final long _tmpEntryDate;
            _tmpEntryDate = _cursor.getLong(_cursorIndexOfEntryDate);
            final Long _tmpExitDate;
            if (_cursor.isNull(_cursorIndexOfExitDate)) {
              _tmpExitDate = null;
            } else {
              _tmpExitDate = _cursor.getLong(_cursorIndexOfExitDate);
            }
            final double _tmpFees;
            _tmpFees = _cursor.getDouble(_cursorIndexOfFees);
            final Double _tmpStopLoss;
            if (_cursor.isNull(_cursorIndexOfStopLoss)) {
              _tmpStopLoss = null;
            } else {
              _tmpStopLoss = _cursor.getDouble(_cursorIndexOfStopLoss);
            }
            final Double _tmpTakeProfit;
            if (_cursor.isNull(_cursorIndexOfTakeProfit)) {
              _tmpTakeProfit = null;
            } else {
              _tmpTakeProfit = _cursor.getDouble(_cursorIndexOfTakeProfit);
            }
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            _item = new TradeEntity(_tmpId,_tmpSymbol,_tmpDirection,_tmpMarket,_tmpEntryPrice,_tmpExitPrice,_tmpQuantity,_tmpEntryDate,_tmpExitDate,_tmpFees,_tmpStopLoss,_tmpTakeProfit,_tmpTags,_tmpNotes);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TradeEntity>> getClosedTrades() {
    final String _sql = "SELECT * FROM trades WHERE exitPrice IS NOT NULL ORDER BY exitDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"trades"}, new Callable<List<TradeEntity>>() {
      @Override
      @NonNull
      public List<TradeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSymbol = CursorUtil.getColumnIndexOrThrow(_cursor, "symbol");
          final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
          final int _cursorIndexOfMarket = CursorUtil.getColumnIndexOrThrow(_cursor, "market");
          final int _cursorIndexOfEntryPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "entryPrice");
          final int _cursorIndexOfExitPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "exitPrice");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfEntryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "entryDate");
          final int _cursorIndexOfExitDate = CursorUtil.getColumnIndexOrThrow(_cursor, "exitDate");
          final int _cursorIndexOfFees = CursorUtil.getColumnIndexOrThrow(_cursor, "fees");
          final int _cursorIndexOfStopLoss = CursorUtil.getColumnIndexOrThrow(_cursor, "stopLoss");
          final int _cursorIndexOfTakeProfit = CursorUtil.getColumnIndexOrThrow(_cursor, "takeProfit");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<TradeEntity> _result = new ArrayList<TradeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TradeEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSymbol;
            _tmpSymbol = _cursor.getString(_cursorIndexOfSymbol);
            final String _tmpDirection;
            _tmpDirection = _cursor.getString(_cursorIndexOfDirection);
            final String _tmpMarket;
            _tmpMarket = _cursor.getString(_cursorIndexOfMarket);
            final double _tmpEntryPrice;
            _tmpEntryPrice = _cursor.getDouble(_cursorIndexOfEntryPrice);
            final Double _tmpExitPrice;
            if (_cursor.isNull(_cursorIndexOfExitPrice)) {
              _tmpExitPrice = null;
            } else {
              _tmpExitPrice = _cursor.getDouble(_cursorIndexOfExitPrice);
            }
            final double _tmpQuantity;
            _tmpQuantity = _cursor.getDouble(_cursorIndexOfQuantity);
            final long _tmpEntryDate;
            _tmpEntryDate = _cursor.getLong(_cursorIndexOfEntryDate);
            final Long _tmpExitDate;
            if (_cursor.isNull(_cursorIndexOfExitDate)) {
              _tmpExitDate = null;
            } else {
              _tmpExitDate = _cursor.getLong(_cursorIndexOfExitDate);
            }
            final double _tmpFees;
            _tmpFees = _cursor.getDouble(_cursorIndexOfFees);
            final Double _tmpStopLoss;
            if (_cursor.isNull(_cursorIndexOfStopLoss)) {
              _tmpStopLoss = null;
            } else {
              _tmpStopLoss = _cursor.getDouble(_cursorIndexOfStopLoss);
            }
            final Double _tmpTakeProfit;
            if (_cursor.isNull(_cursorIndexOfTakeProfit)) {
              _tmpTakeProfit = null;
            } else {
              _tmpTakeProfit = _cursor.getDouble(_cursorIndexOfTakeProfit);
            }
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            _item = new TradeEntity(_tmpId,_tmpSymbol,_tmpDirection,_tmpMarket,_tmpEntryPrice,_tmpExitPrice,_tmpQuantity,_tmpEntryDate,_tmpExitDate,_tmpFees,_tmpStopLoss,_tmpTakeProfit,_tmpTags,_tmpNotes);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TradeEntity>> searchTrades(final String query) {
    final String _sql = "SELECT * FROM trades WHERE symbol LIKE '%' || ? || '%' ORDER BY entryDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"trades"}, new Callable<List<TradeEntity>>() {
      @Override
      @NonNull
      public List<TradeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSymbol = CursorUtil.getColumnIndexOrThrow(_cursor, "symbol");
          final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
          final int _cursorIndexOfMarket = CursorUtil.getColumnIndexOrThrow(_cursor, "market");
          final int _cursorIndexOfEntryPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "entryPrice");
          final int _cursorIndexOfExitPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "exitPrice");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfEntryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "entryDate");
          final int _cursorIndexOfExitDate = CursorUtil.getColumnIndexOrThrow(_cursor, "exitDate");
          final int _cursorIndexOfFees = CursorUtil.getColumnIndexOrThrow(_cursor, "fees");
          final int _cursorIndexOfStopLoss = CursorUtil.getColumnIndexOrThrow(_cursor, "stopLoss");
          final int _cursorIndexOfTakeProfit = CursorUtil.getColumnIndexOrThrow(_cursor, "takeProfit");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<TradeEntity> _result = new ArrayList<TradeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TradeEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSymbol;
            _tmpSymbol = _cursor.getString(_cursorIndexOfSymbol);
            final String _tmpDirection;
            _tmpDirection = _cursor.getString(_cursorIndexOfDirection);
            final String _tmpMarket;
            _tmpMarket = _cursor.getString(_cursorIndexOfMarket);
            final double _tmpEntryPrice;
            _tmpEntryPrice = _cursor.getDouble(_cursorIndexOfEntryPrice);
            final Double _tmpExitPrice;
            if (_cursor.isNull(_cursorIndexOfExitPrice)) {
              _tmpExitPrice = null;
            } else {
              _tmpExitPrice = _cursor.getDouble(_cursorIndexOfExitPrice);
            }
            final double _tmpQuantity;
            _tmpQuantity = _cursor.getDouble(_cursorIndexOfQuantity);
            final long _tmpEntryDate;
            _tmpEntryDate = _cursor.getLong(_cursorIndexOfEntryDate);
            final Long _tmpExitDate;
            if (_cursor.isNull(_cursorIndexOfExitDate)) {
              _tmpExitDate = null;
            } else {
              _tmpExitDate = _cursor.getLong(_cursorIndexOfExitDate);
            }
            final double _tmpFees;
            _tmpFees = _cursor.getDouble(_cursorIndexOfFees);
            final Double _tmpStopLoss;
            if (_cursor.isNull(_cursorIndexOfStopLoss)) {
              _tmpStopLoss = null;
            } else {
              _tmpStopLoss = _cursor.getDouble(_cursorIndexOfStopLoss);
            }
            final Double _tmpTakeProfit;
            if (_cursor.isNull(_cursorIndexOfTakeProfit)) {
              _tmpTakeProfit = null;
            } else {
              _tmpTakeProfit = _cursor.getDouble(_cursorIndexOfTakeProfit);
            }
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            _item = new TradeEntity(_tmpId,_tmpSymbol,_tmpDirection,_tmpMarket,_tmpEntryPrice,_tmpExitPrice,_tmpQuantity,_tmpEntryDate,_tmpExitDate,_tmpFees,_tmpStopLoss,_tmpTakeProfit,_tmpTags,_tmpNotes);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getTradeCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM trades";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
