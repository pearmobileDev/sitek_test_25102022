package com.example.sitek_test;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class DatabaseHelper
{
	// Инициализация класса, здесь ищем базу данных и проверяем ее (может быть)
	public void initDB(Context ctx, String sourceResFile, String destPath, String destFile, int curDBVer)
	{
		File dir = new File(destPath);
		if (!dir.exists()) dir.mkdirs();

		// Раскатываем базу, если не совпадалово или база отсутствует
		if ((!isExist(destPath, destFile))) reWriteFileFromRaw(ctx, ctx.getPackageName(), sourceResFile, destPath, destFile);
	}

	// раскатка файла базы данных в директорию
	public void reWriteFileFromRaw(Context ctx, String packageName, String resName, String destPath, String destFileName)
	{
		// проверяем наличие необходимого для раскатки бд пути
		File dir = new File(destPath);
		if (!dir.exists()) dir.mkdirs();

		File file = new File(destPath, destFileName);
		// создаем пустой файл/путь если еще не создан
		try
		{
			file.delete();
		} catch (Exception e){}

		try
		{
			file.createNewFile();
		} catch (IOException ioException) {}

		// начинаем процесс записи
		try
		{
			BufferedOutputStream sqlStream = new BufferedOutputStream(new FileOutputStream(file));
			int resID = ctx.getResources().getIdentifier(packageName + ":raw/" + resName, null, null);
			BufferedInputStream resStream = new BufferedInputStream(ctx.getResources().openRawResource(resID));

			while (resStream.available() > 0)
			{
				byte[] b = new byte[resStream.available()];
				resStream.read(b);
				sqlStream.write(b);
			}
			resStream.close();
			sqlStream.close();
		} catch (IOException ioException) {}

	}

	public SQLiteDatabase open(SQLiteDatabase db, String path, String file)
	{
		if (!isExist(path, file)) return null;

		if (db!=null) if (db.isOpen()) return db;

		try
		{
			db = SQLiteDatabase.openDatabase(path + file, null, SQLiteDatabase.OPEN_READWRITE);
		}
		catch (SQLiteException e)
		{
			db = null;
		}

		return db;
	}

	private boolean isExist(String path, String file)
	{
		File sqlFile = new File(path, file);
		return sqlFile.exists();
	}

	public SQLiteDatabase close(SQLiteDatabase db)
	{
		if (db != null)
			if (db.isOpen()) {db.close();}

		return null;
	}

	public boolean isOpen(SQLiteDatabase db)
	{
		boolean rVal = false;
		if (db!=null)
		{
			try
			{
				rVal = db.isOpen();

			} catch (SQLiteException se) {}
		}


		return rVal;
	}

}
