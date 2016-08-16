package com.my.FetchData;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.List;

public class FetchData extends Activity
{
    private static final String TAG = "FetchData";
    private static final String AUTHORITY = "com.android.browser";
    private static final String PROVIDER_PATH = "bookmarks";
    /** Called when the activity is first created. */
    private Cursor bmksCursor;
    SimpleCursorAdapter adapter;
    ListView list;
    private static final String PASSWORD = "c74c05a9c41ab22172d0e5342e628c11";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //testfunc();

        list = (ListView) findViewById(R.id.listview);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Operator opDlg = new Operator(new Operator.Observer() {
                    @Override
                    public void onOperation(Operator.Op p, int id) {
                        final Cursor bmk = (Cursor)list.getItemAtPosition(id);
                        if (p == Operator.Op.DELETE) {
                            getContentResolver().delete(genUri(PROVIDER_PATH),
                                    "url = ?",
                                    new String[] { bmk.getString(1) });
                        } else if (p == Operator.Op.EDIT) {
                            Info info = new Info(bmk.getString(5),
                                    bmk.getString(1),
                                    Info.Type.EDIT,
                                    new Info.Listener() {
                                @Override
                                public void onOK (String title, String url, Info.Type type, boolean hsy) {
                                    if (type == Info.Type.EDIT) {
                                        getContentResolver().update(
                                                genUri(PROVIDER_PATH + "/" + bmk.getLong(0)),
                                                createValues(title, url),
                                                "url LIKE ?",
                                                new String[] {url});
                                    }
                                }
                            });
                            info.show(getFragmentManager(), "Edit");
                        }
                    }
                }, i);
                opDlg.show(getFragmentManager(), "Operator");
            }
        });

         adapter = new SimpleCursorAdapter(
                this,
                R.layout.item,
                null,
                new String[] {"title", "url"},
                new int[] {R.id.title, R.id.url});
        list.setAdapter(adapter);

        Button addBtn = (Button) findViewById(R.id.add);
        addBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                addBookmark();
            }
        });

        Button searchBtn = (Button) findViewById(R.id.search);
        searchBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Info info = new Info(Info.Type.SEARCH, new Info.Listener() {
                    @Override
                    public void onOK(String title, String url, Info.Type type, boolean hsy) {
                        Uri uri = getBrowserProviderUri(hsy);
                        if (uri == null) return;
                        Cursor res = getContentResolver().query(uri, null, "url LIKE ?", new String[] { url }, null);
                        if (res != null) {
                            adapter.changeCursorAndColumns(res,
                                    new String[] {SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2},
                                    new int[] {R.id.title, R.id.url});
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
                info.show(getFragmentManager(), "search");
            }
        });

        Button bmksBtn = (Button) findViewById(R.id.bmks);
        bmksBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                if (bmksCursor == null) {
                    bmksCursor = getContentResolver().query(genUri(PROVIDER_PATH), null, null, null, null);
                }
                adapter.changeCursor(bmksCursor);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void addBookmark() {
        Info info = new Info(Info.Type.ADD, new Info.Listener() {
            @Override
            public void onOK(String title, String url, Info.Type type, boolean hsy) {
                if (type == Info.Type.ADD) {
                    getContentResolver().insert(genUri(PROVIDER_PATH), createValues(title, url));
                }
            }
        });
        info.show(getFragmentManager(), "adddialog");
    }

    private ContentValues createValues(String title, String url) {
        ContentValues values = new ContentValues();
        values.put("bookmark", 1);
        values.put("title", title);
        values.put("url", url);
        return values;
    }

    private Uri genUri(String path) {
        Uri.Builder builder = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(AUTHORITY);
        return builder.appendEncodedPath(path).build();
    }

    private Uri getBrowserProviderUri(boolean includeHistory) {
        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        List<SearchableInfo> searchableInfos = searchManager.getSearchablesInGlobalSearch();
        if (searchableInfos == null) return null;
        for (SearchableInfo si : searchableInfos) {
            if (si.getSuggestAuthority().equals(AUTHORITY)) {
                Uri.Builder builder = new Uri.Builder()
                        .scheme(ContentResolver.SCHEME_CONTENT)
                        .authority(si.getSuggestAuthority());
                if (!includeHistory) {
                    builder.appendEncodedPath(si.getSuggestPath());
                }
                builder.appendEncodedPath(SearchManager.SUGGEST_URI_PATH_QUERY);
                return builder.build();
            }
        }
        return null;
    }

    private void testfunc() {
        ProviderInfo pi = getPackageManager().resolveContentProvider(AUTHORITY, 0);
        if (pi != null) {
            Log.d("zjzjzj", pi.readPermission);
            Log.d("zjzjzj", pi.writePermission);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

