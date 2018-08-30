package com.github.emilbayes.downloadmanager;

import android.content.Context;
import android.content.pm.LauncherApps;
import android.database.Cursor;
import android.net.Uri;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DownloadManager;

import java.util.HashMap;
import java.util.Map;

public class DownloadManagerPlugin extends CordovaPlugin {
    DownloadManager downloadManager;

    @Override
    public void initialize(final CordovaInterface cordova, final CordovaWebView webView) {
        super.initialize(cordova, webView);

        downloadManager = (DownloadManager) cordova.getActivity()
                .getApplication()
                .getApplicationContext()
                .getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
      if (action.equals("enqueue")) return enqueue(args.getJSONObject(0), callbackContext);
      if (action.equals("query")) return query(args.getJSONObject(0), callbackContext);
      if (action.equals("remove")) return remove(args, callbackContext);
      if (action.equals("addCompletedDownload")) return addCompletedDownload(args.getJSONObject(0), callbackContext);

      callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
      return false;
    }

    protected boolean enqueue(JSONObject obj, CallbackContext callbackContext) throws JSONException {
        DownloadManager.Request req = deserialiseRequest(obj);

        long id = downloadManager.enqueue(req);

        callbackContext.success(Long.toString(id));

        return true;
    }

    protected boolean query(JSONObject obj, CallbackContext callbackContext) throws JSONException {
        DownloadManager.Query query = deserialiseQuery(obj);

        Cursor downloads = downloadManager.query(query);

        callbackContext.success(JSONFromCursor(downloads));

        downloads.close();

        return true;
    }

    protected boolean remove(JSONArray arr, CallbackContext callbackContext) throws JSONException {
        long[] ids = longsFromJSON(arr);

        int removed = downloadManager.remove(ids);
        callbackContext.success(removed);

        return true;
    }

    protected boolean addCompletedDownload(JSONObject obj, CallbackContext callbackContext) throws JSONException {
	    
        long id = downloadManager.addCompletedDownload(
            obj.optString("title"),
            obj.optString("description"),
            obj.optBoolean("isMediaScannerScannable",false),
            obj.optString("mimeType"),
            obj.optString("path"),
            obj.optLong("length"),
            obj.optBoolean("showNotification",true));
	// NOTE: If showNotification is false, you need
	// <uses-permission android: name = "android.permission.DOWNLOAD_WITHOUT_NOTIFICATION" />

	callbackContext.success(Long.toString(id));

        return true;
    }
	
    protected DownloadManager.Request deserialiseRequest(JSONObject obj) throws JSONException {
        DownloadManager.Request req = new DownloadManager.Request(Uri.parse(obj.getString("uri")));

        req.setTitle(obj.optString("title"));
        req.setDescription(obj.optString("description"));
        req.setMimeType(obj.optString("mimeType", null));

        if (obj.has("destinationInExternalFilesDir")) {
            Context context = cordova.getActivity()
                    .getApplication()
                    .getApplicationContext();

            JSONObject params = obj.getJSONObject("destinationInExternalFilesDir");

            req.setDestinationInExternalFilesDir(context, params.optString("dirType"), params.optString("subPath"));
        }
        else if (obj.has("destinationInExternalPublicDir")) {
            JSONObject params = obj.getJSONObject("destinationInExternalPublicDir");

            req.setDestinationInExternalPublicDir(params.optString("dirType"), params.optString("subPath"));
        }
        else if (obj.has("destinationUri")) req.setDestinationUri(Uri.parse(obj.getString("destinationUri")));

        req.setVisibleInDownloadsUi(obj.optBoolean("visibleInDownloadsUi", true));
        req.setNotificationVisibility(obj.optInt("notificationVisibility"));
				
        if (obj.has("headers")) {
          JSONArray arrHeaders = obj.optJSONArray("headers");
          for (int i = 0; i < arrHeaders.length(); i++) {
            JSONObject headerObj = arrHeaders.getJSONObject(i);
            req.addRequestHeader(headerObj.optString("header"), headerObj.optString("value"));
          }
        }

        return req;
    }

    protected DownloadManager.Query deserialiseQuery(JSONObject obj) throws JSONException {
        DownloadManager.Query query = new DownloadManager.Query();

        long[] ids = longsFromJSON(obj.optJSONArray("ids"));
        query.setFilterById(ids);

        if (obj.has("status")) {
            query.setFilterByStatus(obj.getInt("status"));
        }

        return query;
    }

    private static PluginResult OK(Map obj) throws JSONException {
        return createPluginResult(obj, PluginResult.Status.OK);
    }

    private static PluginResult ERROR(Map obj) throws JSONException {
        return createPluginResult(obj, PluginResult.Status.ERROR);
    }

    private static PluginResult createPluginResult(Map map, PluginResult.Status status) throws JSONException {
        JSONObject json = new JSONObject(map);
        PluginResult result = new PluginResult(status, json);
        return result;
    }

    private static JSONArray JSONFromCursor(Cursor cursor) throws JSONException {
        JSONArray result = new JSONArray();

        cursor.moveToFirst();
        do {
            JSONObject rowObject = new JSONObject();
            rowObject.put("id", cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_ID)));
            rowObject.put("title", cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE)));
            rowObject.put("description", cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION)));
            rowObject.put("mediaType", cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE)));
            rowObject.put("localFilename", cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME)));
            rowObject.put("localUri", cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
            rowObject.put("mediaproviderUri", cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIAPROVIDER_URI)));
            rowObject.put("uri", cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI)));
            rowObject.put("lastModifiedTimestamp", cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP)));
            rowObject.put("status", cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)));
            rowObject.put("reason", cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON)));
            rowObject.put("bytesDownloadedSoFar", cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)));
            rowObject.put("totalSizeBytes", cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)));
            result.put(rowObject);
        } while (cursor.moveToNext());

        return result;
    }

    private static long[] longsFromJSON(JSONArray arr) throws JSONException {
        if (arr == null) return null;

        long[] longs = new long[arr.length()];

        for (int i = 0; i < arr.length(); i++) {
            String str = arr.getString(i);
            longs[i] = Long.valueOf(str);
        }

        return longs;
    }
}
