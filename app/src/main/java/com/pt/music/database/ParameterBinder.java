package com.pt.music.database;

import android.database.sqlite.SQLiteStatement;

public interface ParameterBinder {
	void bind(SQLiteStatement st, Object object);
}
