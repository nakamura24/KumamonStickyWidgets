/*
 * Copyright (C) 2012 M.Nakamura
 *
 * This software is licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 2.1 Japan License.
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 		http://creativecommons.org/licenses/by-nc-sa/2.1/jp/legalcode
 */
package jp.widgets.kumamon.sticky.ics;

import jp.widgets.kumamon.lib.*;
import jp.widgets.kumamon.sticky.R;
import static jp.widgets.kumamon.sticky.StickyWidgetConstant.*;

import java.util.ArrayList;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.LinearLayout.LayoutParams;

public class StickyWidgetConfigure extends Activity {
	private static final String TAG = "StickyWidgetConfigure";
	private static final int REQUEST_CODE = 1000;
	private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private String mComment = "";
	private int mIcon = 0;
	private int mBack = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		try {
			// AppWidgetID の取得
			Intent intent = getIntent();
			Bundle extras = intent.getExtras();
			if (extras != null) {
				mAppWidgetId = extras.getInt(
						AppWidgetManager.EXTRA_APPWIDGET_ID,
						AppWidgetManager.INVALID_APPWIDGET_ID);
				StaticHash hash = new StaticHash(this);
				mComment = hash.get(COMMENT, String.valueOf(mAppWidgetId), "");
				mIcon = hash.get(ICON, String.valueOf(mAppWidgetId), 0);
				mBack = hash.get(BACK, String.valueOf(mAppWidgetId), 0);
				Log.d(TAG, "mAppWidgetId=" + String.valueOf(mAppWidgetId));
				Log.d(TAG,
						"mComment=" + mComment + "mIcon="
								+ String.valueOf(mIcon) + "mBack="
								+ String.valueOf(mBack));
			}

			setContentView(R.layout.widget_sticky_configure);
			getWindow().setLayout(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);

			EditText sticky_comment_EditText = (EditText) findViewById(R.id.sticky_comment_EditText);
			sticky_comment_EditText.setText(mComment);

			Spinner sticky_icons_Spinner = (Spinner) findViewById(R.id.sticky_icons_Spinner);
			sticky_icons_Spinner.setSelection(mIcon);
			// Spinner のアイテムが選択された時に呼び出されるコールバックを登録
			sticky_icons_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				// アイテムが選択された時の動作
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					// Spinner を取得
					Spinner spinner = (Spinner) parent;
					// 選択されたアイテムのテキストを取得
					mIcon = spinner.getSelectedItemPosition();
				}

				// 何も選択されなかった時の動作
				public void onNothingSelected(AdapterView<?> parent) {
					mIcon = 0;
				}
			});
			Spinner sticky_backs_Spinner = (Spinner) findViewById(R.id.sticky_backs_Spinner);
			sticky_backs_Spinner.setSelection(mBack);
			// Spinner のアイテムが選択された時に呼び出されるコールバックを登録
			sticky_backs_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				// アイテムが選択された時の動作
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					// Spinner を取得
					Spinner spinner = (Spinner) parent;
					// 選択されたアイテムのテキストを取得
					mBack = spinner.getSelectedItemPosition();
				}

				// 何も選択されなかった時の動作
				public void onNothingSelected(AdapterView<?> parent) {
					mBack = 0;
				}
			});
			if (Build.VERSION.SDK_INT >= 14) {
				Button sticky_Speech_Button = (Button) findViewById(R.id.sticky_Speech_Button);
				sticky_Speech_Button.setVisibility(View.VISIBLE);
			}
			Log.i(TAG, "onCreate end");
		} catch (Exception e) {
			ExceptionLog.Log(TAG, e);
		}
	}

	// Button の onClick で実装
	public void onCleanButtonClick(View v) {
		try {
			Log.i(TAG, "onCleanButtonClick");
			EditText sticky_comment_EditText = (EditText) findViewById(R.id.sticky_comment_EditText);
			sticky_comment_EditText.setText("");
			Log.i(TAG, "onCleanButtonClick End");
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	// Button の onClick で実装
	public void onSpeechButtonClick(View v) {
		try {
			Log.i(TAG, "onSpeechButtonClick");
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
					RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			startActivityForResult(intent, REQUEST_CODE);
			Log.i(TAG, "onSpeechButtonClick End");
		} catch (Exception e) {
			ExceptionLog.Log(TAG, e);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			// requestCodeを確認して、自分が発行したIntentの結果であれば処理を行う
			if ((REQUEST_CODE == requestCode) && (RESULT_OK == resultCode)) {
				// 結果はArrayListで返ってくる
				ArrayList<String> results = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

				// エディットテキストのテキストを取得します
				EditText sticky_comment_EditText = (EditText) findViewById(R.id.sticky_comment_EditText);
				String comment = sticky_comment_EditText.getText().toString();
				comment += results.get(0);
				sticky_comment_EditText.setText(comment);
			}
			super.onActivityResult(requestCode, resultCode, data);
		} catch (Exception e) {
			ExceptionLog.Log(TAG, e);
		}
	}

	// Button の onClick で実装
	public void onOKButtonClick(View v) {
		try {
			Log.i(TAG, "onOKButtonClick");
			// エディットテキストのテキストを取得します
			EditText sticky_comment_EditText = (EditText) findViewById(R.id.sticky_comment_EditText);
			mComment = sticky_comment_EditText.getText().toString();

			Intent intent = new Intent(this, KumamonStickyWidget.class);
			intent.setAction(CONFIG_DONE);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
			intent.putExtra(ICON, mIcon);
			intent.putExtra(BACK, mBack);
			intent.putExtra(COMMENT, mComment);
			sendBroadcast(intent);
			setResult(RESULT_OK, intent);
			finish();
			Log.i(TAG, "onOKButtonClick End");
		} catch (Exception e) {
			ExceptionLog.Log(TAG, e);
		}
	}
}
