package com.zhy.springfestival;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity implements View.OnClickListener {

    private ImageView photoView;
    private EditText contentView;
    private Button shareButton;

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        api = WXAPIFactory.createWXAPI(this, "wxcbcdd97297f193b7", false);
        api.registerApp("wxcbcdd97297f193b7");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();


        contentView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/test.ttf"));
        shareButton.setOnClickListener(this);
        photoView.setOnClickListener(this);
    }

    private void initView() {
        photoView = (ImageView) findViewById(R.id.iv_photo);
        contentView = (EditText) findViewById(R.id.et_word);
        shareButton = (Button) findViewById(R.id.btn_share);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1000) {
            photoView.setImageURI(data.getData());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_photo:
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 1000);
                break;
            case R.id.btn_share:
                share();
                shareButton.setVisibility(View.VISIBLE);
//                createPic();
                break;
        }
    }

    private void share() {
        WXWebpageObject webpage = new WXWebpageObject();
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.mediaObject = new WXImageObject(createBitmap());

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }

    private Bitmap createBitmap() {
        shareButton.setVisibility(View.GONE);
        contentView.clearFocus();
        contentView.setFocusable(false);
        View view = getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        return view.getDrawingCache();
//        String path = Environment.getExternalStorageDirectory().getPath() + "/springfastival.png";
//        saveBitmap(bitmap, path);
    }

    public void saveBitmap(Bitmap bm, String picName) {
        File f = new File(picName);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
