package com.pt.music.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.android.volley.NetworkError;
import com.android.volley.VolleyError;
import com.pt.music.BaseFragment;
import com.pt.music.R;
import com.pt.music.activity.MainActivity;
import com.pt.music.adapter.CategoryMusicAdapter;
import com.pt.music.config.Args;
import com.pt.music.config.GlobalValue;
import com.pt.music.config.WebserviceApi;
import com.pt.music.modelmanager.CommonParser;
import com.pt.music.modelmanager.ModelManager;
import com.pt.music.modelmanager.ModelManagerListener;
import com.pt.music.object.CategoryMusic;
import com.pt.music.slidingmenu.SlidingMenu;
import com.pt.music.util.AppUtil;
import com.pt.music.util.SmartLog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CategoryMusicFragment extends BaseFragment {
    private GridView grvCategoryMusic;
    private CategoryMusicAdapter categoryMusicAdapter;
    private List<CategoryMusic> arrCategory;
    public static boolean isShowing = false;

    public static boolean isParent = true;
    //public int currentParentID = 0;
    private View currentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_music,
                container, false);
        initUIBase(view);
        setButtonMenu(view);
        currentView = view;
        return view;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
//		loadCategory();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (!isShowing) {
                loadCategory(GlobalValue.currentParentCategoryId);
                isShowing = true;
            }
            getMainActivity().menu
                    .setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
            getMainActivity().setVisibilityFooter();
        }
    }


    @Override
    protected void initUIBase(final View view) {
        super.initUIBase(view);
        setHeaderTitle(R.string.courses);
        grvCategoryMusic = (GridView) view.findViewById(R.id.grvCategoryMusic);
        grvCategoryMusic.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int position,
                                    long l) {
                CategoryMusic selectedCategory = arrCategory.get(position);
                //Log.e(TAG, "selectedCategory "+ selectedCategory.getIsParent());

                GlobalValue.currentCategoryId = selectedCategory.getId();
                GlobalValue.currentCategoryName = selectedCategory.getTitle();

                if (selectedCategory.getIsParent() == false) {
                    ListSongsFragment.isShowing = false;
                    getMainActivity().gotoFragment(MainActivity.LIST_SONG_FRAGMENT);
                } else {
                    setCurentParentCategory(selectedCategory.getId(), selectedCategory.getTitle());
                }
            }
        });
    }

    public void setCurentParentCategory(int parentId, String title) {
        if (parentId != 0) {
            setButtonBack(currentView);
            GlobalValue.currentParentCategoryId = parentId;
            setHeaderTitle(title);
            loadCategory(GlobalValue.currentParentCategoryId);
            isShowing = true;
        } else {
            setButtonMenu(currentView);
            GlobalValue.currentParentCategoryId = 0;
            setHeaderTitle(R.string.courses);
            loadCategory(GlobalValue.currentParentCategoryId);
            isShowing = true;
        }
    }

    private void loadCategory(int parentID) {
        final int pid = parentID;
        String url = WebserviceApi.GET_CATEGORIES + "?parentId=" + parentID + "&user_id=" + GlobalValue.pref.getStringValue(Args.USER_ID);
        ModelManager.sendGetRequest(self, url, null, true, new ModelManagerListener() {
            @Override
            public void onError(VolleyError error) {
                if (error instanceof NetworkError) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Internet Unavailable");
                    alertDialog.setCancelable(false);
                    alertDialog.setMessage("Something went wrong \n" +
                            "The app can't access the Internet");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadCategory(pid);
                            return;
                        }
                    });
                    alertDialog.show();
                } else {
                    AppUtil.alert(getActivity(),
                            getString(R.string.server_error));
                }

            }

            @Override
            public void onSuccess(String json) {
                processCategoryResponse(pid, json.substring(json
                        .indexOf("{")));
            }
        });
    }

    private void processCategoryResponse(int parentID, String response) {
        final int pid = parentID;
        String json = "";
        try {
            json = response;
            if (json == null) {
                AppUtil.alert(getActivity(), getString(R.string.json_server_error));
                return;
            }

            SmartLog.log(TAG, json);
            JSONObject entry = new JSONObject(json);

            if (entry.getString(WebserviceApi.KEY_STATUS).equalsIgnoreCase(
                    WebserviceApi.KEY_SUCCESS)) {

                arrCategory = CommonParser.parseCategoryFromServer(json);

                if(pid == 0) {
                    List<CategoryMusic> list = new ArrayList<CategoryMusic>();
                    for(int i =0; i<arrCategory.size(); i++){
                        CategoryMusic temp = arrCategory.get(i);
                        if(temp.getHasChild() == true || temp.getIsParent() == true)
                            list.add(temp);
                    }

                    arrCategory = list;
                }
                categoryMusicAdapter = new CategoryMusicAdapter(self,
                        arrCategory);
                grvCategoryMusic.setAdapter(categoryMusicAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
