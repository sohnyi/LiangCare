package com.sohnyi.liangcare;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sohnyi.liangcare.database.LiangFile;
import com.sohnyi.liangcare.database.LiangFileLab;
import com.sohnyi.liangcare.utils.AESHelper;
import com.sohnyi.liangcare.utils.Encoder;
import com.sohnyi.liangcare.utils.FileUtil;
import com.sohnyi.liangcare.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import static com.sohnyi.liangcare.database.MyConstant.CURRENT_TYPE;
import static com.sohnyi.liangcare.database.MyConstant.DOCUMENT_TYPES;
import static com.sohnyi.liangcare.database.MyConstant.FILE_ADD_ACTION_REQUEST_CODE;
import static com.sohnyi.liangcare.database.MyConstant.TYPE_AUDIO;
import static com.sohnyi.liangcare.database.MyConstant.TYPE_DOC;
import static com.sohnyi.liangcare.database.MyConstant.TYPE_IMAGE;
import static com.sohnyi.liangcare.database.MyConstant.TYPE_VIDEO;

public class SecCabActivity extends AppCompatActivity {
    private static final String TAG = "SecCabActivity";
    private static Uri CONTENT_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private static String SELECT_ID = MediaStore.Audio.Media._ID;


    private Toolbar mToolbar;
    private FloatingActionButton mActionButton;
    private ProgressDialog mProgressDialog;
    private RecyclerView mRecyclerView;
    private TextView mTextView_NoData;

    private int type;
    private int mPosition;

    private List<String> filesName;
    private List<String> filesMimeType;
    private List<Long> filesSize;
    private List<String> filesPath;

    private List<LiangFile> mLiangFiles;
    private LiangFileLab mFileLab;

    private FileAdapter mFileAdapter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_audio:
                    type = TYPE_AUDIO;
                    updateUI();
                    return true;
                case R.id.navigation_video:
                    type = TYPE_VIDEO;
                    updateUI();
                    return true;
                case R.id.navigation_photo:
                    type = TYPE_IMAGE;
                    updateUI();
                    return true;
                case R.id.navigation_document:
                    type = TYPE_DOC;
                    updateUI();
                    return true;
                default:
                    break;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec_cab);

        if (savedInstanceState != null) {
            type = savedInstanceState.getInt(CURRENT_TYPE);
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        mFileLab = LiangFileLab.get();
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.file_recycler_view);
        mTextView_NoData = (TextView) findViewById(R.id.noData_text);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(mRecyclerView.getContext(),
                layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(divider);
        mActionButton = (FloatingActionButton) findViewById(R.id.add_action);
        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(Intent.ACTION_DEVICE_STORAGE_OK, true);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                switch (type) {
                    case TYPE_AUDIO:
                        intent.setType("audio/*");
                        CONTENT_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        SELECT_ID = MediaStore.Audio.Media._ID;
                        break;
                    case TYPE_VIDEO:
                        intent.setType("video/*");
                        CONTENT_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        SELECT_ID = MediaStore.Video.Media._ID;
                        break;
                    case TYPE_IMAGE:
                        intent.setType("image/*");
                        CONTENT_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        SELECT_ID = MediaStore.Images.Media._ID;
                        break;
                    case TYPE_DOC:
                        intent.setType("*/*");
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, DOCUMENT_TYPES);
                        break;
                    default:
                        break;
                }
                startActivityForResult(intent, FILE_ADD_ACTION_REQUEST_CODE);
            }
        });

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        initProgressDialog();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sec_cab, menu);
        MenuItem searchItem = menu.findItem(R.id.search_sec_cab);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void initProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getText(R.string.encrypting));
        mProgressDialog.setCancelable(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        mLiangFiles = new ArrayList<>();
        new InitFileInfo().execute();
    }

    private class FileHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private TextView mFileNameText;
        private TextView mFileSizeText;
        private ImageButton mMoreButton;

        private FileHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.image_file_icon);
            mFileNameText = (TextView) itemView.findViewById(R.id.text_file_name);
            mFileSizeText = (TextView) itemView.findViewById(R.id.text_file_size);
            mMoreButton = (ImageButton) itemView.findViewById(R.id.file_more_button);
        }

        private void bindFile(String name, String size) {
            switch (type) {
                case TYPE_AUDIO:
                    mImageView.setImageDrawable(getDrawable(R.drawable.ic_headset_red_500_48dp));
                    break;
                case TYPE_VIDEO:
                    mImageView.setImageDrawable(getDrawable(R.drawable.ic_slow_motion_video_red_500_48dp));
                    break;
                case TYPE_IMAGE:
                    mImageView.setImageDrawable(getDrawable(R.drawable.ic_image_red_500_48dp));
                    break;
                case TYPE_DOC:
                    mImageView.setImageDrawable(getDrawable(R.drawable.ic_insert_drive_file_red_500_48dp));
                    break;
                default:
                    break;
            }
            mFileNameText.setText(name);
            mFileSizeText.setText(size);
        }
    }

    private class FileAdapter extends RecyclerView.Adapter<FileHolder> {
        private List<LiangFile> mFiles;
        private LiangFile mFile;
        private String name;
        private String size;


        private FileAdapter(List<LiangFile> files) {
            mFiles = files;
        }

        @Override
        public FileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            View view = layoutInflater
                    .inflate(R.layout.sec_cab_file_items, parent, false);
            final FileHolder holder = new FileHolder(view);
            holder.mMoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 2017/5/24 show more memu.
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(FileHolder fileHolder, int position) {
            mPosition = position;
            mFile = mFiles.get(position);
            name = mFile.getName();
            size = FileUtil.sizeFormat(mFile.getSize());
            fileHolder.bindFile(name, size);
        }

        @Override
        public int getItemCount() {
            return mFiles.size();
        }

        private void setFiles(List<LiangFile> files) {
            mFiles = files;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        filesName = new ArrayList<>();
        filesMimeType = new ArrayList<>();
        filesSize = new ArrayList<>();
        filesPath = new ArrayList<>();

        if (resultCode == RESULT_OK && requestCode == FILE_ADD_ACTION_REQUEST_CODE) {
            if (data != null) {
                ClipData returnClipData = data.getClipData();
                if (returnClipData != null) {
                    handleResultsInfo(SecCabActivity.this, returnClipData);
                } else {
                    Uri returnUri = data.getData();
                    handleResultInfo(SecCabActivity.this, returnUri);
                }
            }
        }
        if (filesPath.size() > 0 && filesPath.size() == filesMimeType.size()
                && filesPath.size() == filesName.size()
                && filesPath.size() == filesSize.size()) {
            new FileEncryptTask().execute();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleResultsInfo(Context context, ClipData clipData) {
        ClipData.Item item;
        Uri uri;
        for (int i = 0; i < clipData.getItemCount(); ++i) {
            item = clipData.getItemAt(i);
            uri = item.getUri();
            handleResultInfo(context, uri);
        }
    }

    private void handleResultInfo(Context context, Uri uri) {
        LogUtil.i("handleResultInfo", "GET FILE INFO: " + "\n" + "\n");
        LogUtil.i("handleResultInfo", uri.toString());
        String mimeType = null;
        long fileSize = 0;
        String displayName = null;
        String filePath = null;

        mimeType = context.getContentResolver().getType(uri);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (!cursor.isNull(sizeIndex)) {
                    fileSize = cursor.getLong(sizeIndex);
                } else {
                    fileSize = 0;
                }
            }

        } catch (Exception e) {
            LogUtil.e("handleResultInfo", "getPath: " + e.getMessage());
        }
        if (DocumentsContract.isDocumentUri(context, uri)) {
            LogUtil.i("handleResultInfo", "handleResultData: isDocumentUri");
            String docID = DocumentsContract.getDocumentId(uri);
            LogUtil.i("handleResultInfo", uri.getAuthority());
            if ("com.android.providers.media.documents".equals(uri.getAuthority())
                    || "com.android.externalstorage.documents".equals(uri.getAuthority())) {
                LogUtil.i(TAG, "handleResultData: media documents");
                String id = docID.split(":")[1];
                String selection = SELECT_ID + "=" + id;
                filePath = getMediaPath(context, CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                LogUtil.i(TAG, "handleResultData: download documents");
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docID));
                filePath = getMediaPath(context, contentUri, null);
            }

        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            LogUtil.i(TAG, "handleResultData: content");
            filePath = getMediaPath(context, uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            LogUtil.i(TAG, "handleResultData: file");
            filePath = uri.getPath();
        }

        LogUtil.i("handleResultInfo", "TYPE: " + mimeType
                + ", NAME: " + displayName
                + ", SIZE: " + fileSize + "B"
                + ", PATH: " + filePath);
        if (fileSize > 0 && displayName != null && filePath != null && mimeType != null) {
            filesName.add(displayName);
            filesMimeType.add(mimeType);
            filesPath.add(filePath);
            filesSize.add(fileSize);
        }
    }

    private String getMediaPath(Context context, Uri uri, String selection) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            LogUtil.i(TAG, "get Path: cursor is not null");
            if (cursor.moveToFirst()) {
                switch (type) {
                    case TYPE_AUDIO:
                        path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                        break;
                    case TYPE_VIDEO:
                        path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                        break;
                    case TYPE_IMAGE:
                        path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                        break;
                    default:
                        break;
                }
            }
            cursor.close();
        }
        return path;
    }

    class FileEncryptTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String key = null;
                String desPath = getApplicationInfo().dataDir;
                LogUtil.i(TAG, "desPath: " + desPath);
                switch (type) {
                    case TYPE_AUDIO:
                        desPath = desPath + "/audios/";
                        break;
                    case TYPE_VIDEO:
                        desPath = desPath + "/videos/";
                        break;
                    case TYPE_IMAGE:
                        desPath = desPath + "/images/";
                        break;
                    case TYPE_DOC:
                        desPath = desPath + "/docs/";
                        break;
                    default:
                        break;
                }
                for (int i = 0; i < filesPath.size(); ++i) {
                    key = Encoder.sha256Encode(Long.toString(System.currentTimeMillis()));
                    if (key != null) {
                        String socPath = filesPath.get(i);
                        desPath = desPath + "/" + key.substring(0, 16);
                        LogUtil.i(TAG, socPath);
                        AESHelper.encryptFile(key.substring(0, 32), socPath, desPath);
//                        FileUtil.deleteFile(socPath);
                        mFileLab.addFile(filesName.get(i), type, filesMimeType.get(i),
                                filesSize.get(i), socPath, key);
                    }
                }
                return true;
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage(), e);
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mProgressDialog.dismiss();
            updateUI();
        }
    }

    private class InitFileInfo extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            LogUtil.i(TAG, "type: " + type);
            mLiangFiles = mFileLab.getFiles(type);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (mLiangFiles.size() > 0) {
                LogUtil.i(TAG, "mLiangFiles is not null");
                mTextView_NoData.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mFileAdapter = new FileAdapter(mLiangFiles);
                mRecyclerView.setAdapter(mFileAdapter);
            } else {
                mRecyclerView.setVisibility(View.GONE);
                mTextView_NoData.setVisibility(View.VISIBLE);
                LogUtil.e(TAG, "mLiangFiles is null");
            }
            super.onPostExecute(aBoolean);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_TYPE, type);
    }
}
