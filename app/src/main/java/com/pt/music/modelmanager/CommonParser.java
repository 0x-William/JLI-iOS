package com.pt.music.modelmanager;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pt.music.config.WebserviceApi;
import com.pt.music.object.Album;
import com.pt.music.object.CategoryMusic;
import com.pt.music.object.Song;

/**
 * Parsing json data received from API
 */
public class CommonParser {
	private static String TAG = "CommonParser";
	public static List<Song> parseSongFromServer(String json) {
		List<Song> list = new ArrayList<Song>();
		try {
			JSONObject entry = new JSONObject(json);
			if (entry.getString(WebserviceApi.KEY_STATUS).equalsIgnoreCase(
					WebserviceApi.KEY_SUCCESS)) {
				JSONArray items = entry.getJSONArray(WebserviceApi.KEY_DATA);
				Song song = null;
				for (int i = 0; i < items.length(); i++) {
					JSONObject item = items.getJSONObject(i);
					song = new Song();
					song.setId(getStringValue(item, WebserviceApi.KEY_ID));
					song.setName(getStringValue(item, WebserviceApi.KEY_NAME));
					song.setCategoryId(getStringValue(item, WebserviceApi.KEY_CATEGORYID));
					String url = (getStringValue(item, WebserviceApi.KEY_LINK));
					url.replaceAll(" ", "%20");
					song.setUrl(url);
					song.setArtist(getStringValue(item,
							WebserviceApi.KEY_SINGER_NAME));
					song.setImage(getStringValue(item, WebserviceApi.KEY_IMAGE));
					song.setShareLink(getStringValue(item,
							WebserviceApi.KEY_SHARE_LINK));
					song.setListenCount(getIntValue(item, "listen"));
					song.setDownloadCount(getIntValue(item, "download"));

					list.add(song);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<CategoryMusic> parseCategoryFromServer(String json) {
		List<CategoryMusic> list = new ArrayList<CategoryMusic>();
		try {
			JSONObject entry = new JSONObject(json);
			if (entry.getString(WebserviceApi.KEY_STATUS).equalsIgnoreCase(
					WebserviceApi.KEY_SUCCESS)) {
				JSONArray items = entry.getJSONArray(WebserviceApi.KEY_DATA);
				for (int i = 0; i < items.length(); i++) {
					JSONObject item = items.getJSONObject(i);

					CategoryMusic category = new CategoryMusic();
					//add Parent
					category.setId(getIntValue(item, WebserviceApi.KEY_ID));
					category.setTitle(getStringValue(item,
							WebserviceApi.KEY_NAME));
					
					category.setImage(getStringValue(item,
							WebserviceApi.KEY_IMAGE));
					// song.set(getStringValue(item,
					// WebserviceApi.KEY_SINGER_NAME));
					String isParent = getStringValue(item, WebserviceApi.KEY_ISPARENT);
					category.setIsParent(isParent.equalsIgnoreCase("1")?true:false);

					String parentID = getStringValue(item, WebserviceApi.KEY_PARENT_ID);
					//Log.e(TAG, "isParent: " + isParent);
					category.setHasChild(parentID.equalsIgnoreCase("0")?true:false);
					
					list.add(category);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<Album> parseAlbumFromServer(String json) {
		List<Album> list = new ArrayList<Album>();
		try {
			JSONObject entry = new JSONObject(json);
			if (entry.getString(WebserviceApi.KEY_STATUS).equalsIgnoreCase(
					WebserviceApi.KEY_SUCCESS)) {
				JSONArray items = entry.getJSONArray(WebserviceApi.KEY_DATA);
				for (int i = 0; i < items.length(); i++) {
					JSONObject item = items.getJSONObject(i);

					Album album = new Album();

					album.setId(getIntValue(item, WebserviceApi.KEY_ID));
					album.setName(getStringValue(item, WebserviceApi.KEY_NAME));
					album.setImage(getStringValue(item, WebserviceApi.KEY_IMAGE));

					list.add(album);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	private static boolean getBooleanValue(JSONObject obj, String name) {
		try {
			return obj.getBoolean(name);
		} catch (Exception e) {
			//Log.e(TAG, "getBooleanValue error");
			return false;
		}
	}

	private static String getStringValue(JSONObject obj, String key) {
		try {
			return obj.isNull(key) ? "" : obj.getString(key);
		} catch (JSONException e) {
			return "";
		}
	}

	private static long getLongValue(JSONObject obj, String key) {
		try {
			return obj.isNull(key) ? 0L : obj.getLong(key);
		} catch (JSONException e) {
			return 0L;
		}
	}

	private static int getIntValue(JSONObject obj, String key) {
		try {
			return obj.isNull(key) ? 0 : obj.getInt(key);
		} catch (JSONException e) {
			return 0;
		}
	}

	private static Double getDoubleValue(JSONObject obj, String key) {
		double d = 0.0;
		try {
			return obj.isNull(key) ? d : obj.getDouble(key);
		} catch (JSONException e) {
			return d;
		}
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static boolean isJsonObject(JSONObject parent, String key) {
		try {
			JSONObject jObj = parent.getJSONObject(key);
		} catch (JSONException e) {
			return false;
		}
		return true;
	}

	public static boolean isDouble(String string) {
		try {
			Double.parseDouble(string);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

}
