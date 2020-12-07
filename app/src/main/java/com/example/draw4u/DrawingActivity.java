package com.example.draw4u;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

public class DrawingActivity extends AppCompatActivity {

    FragmentTransaction tran;
    FragmentManager fm;

    FrameLayout frameLayout;

    ArrayList<String> drawingActivity = new ArrayList<>();

    ImageButton mImgTrans;
    ImageButton mImgTrans2;
    ImageButton mImgTrans3;
    ImageButton mImgTrans4;
    ImageButton mImgTrans5;
    ImageButton mImgTrans6;

    Button mButton;
    Button mButton2;
    Button mButton3;
    Button temp;

    TextView nonImage;

    ImageView changeView;

    DiaryInfo diaryinfo = new DiaryInfo();

    Bitmap mBitmap;

    public String fname;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mImgTrans = (ImageButton) findViewById(R.id.iv_preview);
        mImgTrans2 = (ImageButton) findViewById(R.id.iv_preview2);
        mImgTrans3 = (ImageButton) findViewById(R.id.iv_preview3);
        mImgTrans4 = (ImageButton) findViewById(R.id.iv_preview4);
        mImgTrans5 = (ImageButton) findViewById(R.id.iv_preview5);
        mImgTrans6 = (ImageButton) findViewById(R.id.iv_preview6);

        mButton = (Button)findViewById(R.id.button);
        mButton2 = (Button)findViewById(R.id.button2);
        mButton3 = (Button)findViewById(R.id.button3);
        temp = (Button)findViewById(R.id.temp);

        nonImage = (TextView)findViewById(R.id.nonImage);

        changeView = (ImageView)findViewById(R.id.imgTranslate);

        MyView myview = new MyView(this, getJsonString());


        frameLayout = (FrameLayout)findViewById(R.id.constraintLayout);
        frameLayout.addView(myview);

        Intent intent = getIntent();
        fname = intent.getExtras().getString("fname");

        if(fname.equals("noti")){
            temp.setVisibility(View.GONE);
            mButton2.setVisibility(View.GONE);
        }

        nonImage.setVisibility(View.VISIBLE);
        mImgTrans.setVisibility(View.GONE);
        mImgTrans2.setVisibility(View.GONE);
        mImgTrans3.setVisibility(View.GONE);
        mImgTrans4.setVisibility(View.GONE);
        mImgTrans5.setVisibility(View.GONE);
        mImgTrans6.setVisibility(View.GONE);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myview.reset();
            }
        });

        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable drawable = myview.thisDraw();
                mBitmap = drawable.getBitmap();
                uploadFile();
                finish();
            }
        });

        mButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myview.backDraw();
            }
        });


        mImgTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fname.equals("noti")){
                    if(!myview.svgFileName.isEmpty()){
                        SVG svg = null;
                        try {
                            System.out.println("before open");
                            svg = SVG.getFromAsset(getResources().getAssets(),myview.svgFileName.get(0));
                            System.out.println("svg name : " + myview.svgFileName.get(0));
                        } catch (SVGParseException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        assert svg != null;
                        PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());
                        mBitmap = pictureDrawableToBitmap(drawable);
                        uploadFile();
                        finish();
                    }
                }

            }
        });

        mImgTrans2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fname.equals("noti")){
                    if(!myview.svgFileName.isEmpty()) {
                        SVG svg = null;
                        try {
                            System.out.println("before open");
                            svg = SVG.getFromAsset(getResources().getAssets(),myview.svgFileName.get(1));
                            System.out.println("svg name : " + myview.svgFileName.get(0));
                        } catch (SVGParseException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        assert svg != null;
                        PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());
                        mBitmap = pictureDrawableToBitmap(drawable);
                        uploadFile();
                        finish();
                    }

                }

            }
        });

        mImgTrans3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fname.equals("noti")){
                    if(!myview.svgFileName.isEmpty()) {
                        SVG svg = null;
                        try {
                            System.out.println("before open");
                            svg = SVG.getFromAsset(getResources().getAssets(),myview.svgFileName.get(2));
                            System.out.println("svg name : " + myview.svgFileName.get(0));
                        } catch (SVGParseException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        assert svg != null;
                        PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());
                        mBitmap = pictureDrawableToBitmap(drawable);
                        uploadFile();
                        finish();
                    }

                }

            }
        });

        mImgTrans4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fname.equals("noti")){
                    if(!myview.svgFileName.isEmpty()) {
                        SVG svg = null;
                        try {
                            System.out.println("before open");
                            svg = SVG.getFromAsset(getResources().getAssets(),myview.svgFileName.get(3));
                            System.out.println("svg name : " + myview.svgFileName.get(0));
                        } catch (SVGParseException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        assert svg != null;
                        PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());
                        mBitmap = pictureDrawableToBitmap(drawable);
                        uploadFile();
                        finish();
                    }

                }

            }
        });

        mImgTrans5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fname.equals("noti")){
                    if(!myview.svgFileName.isEmpty()) {
                        SVG svg = null;
                        try {
                            System.out.println("before open");
                            svg = SVG.getFromAsset(getResources().getAssets(),myview.svgFileName.get(4));
                            System.out.println("svg name : " + myview.svgFileName.get(0));
                        } catch (SVGParseException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        assert svg != null;
                        PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());
                        mBitmap = pictureDrawableToBitmap(drawable);
                        uploadFile();
                        finish();
                    }

                }

            }
        });

        mImgTrans6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fname.equals("noti")){
                    if(!myview.svgFileName.isEmpty()) {
                        SVG svg = null;
                        try {
                            System.out.println("before open");
                            svg = SVG.getFromAsset(getResources().getAssets(),myview.svgFileName.get(5));
                            System.out.println("svg name : " + myview.svgFileName.get(0));
                        } catch (SVGParseException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        assert svg != null;
                        PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());
                        mBitmap = pictureDrawableToBitmap(drawable);
                        uploadFile();
                        finish();
                    }
                }
            }
        });
    }

    private Bitmap pictureDrawableToBitmap(PictureDrawable pictureDrawable){
        Bitmap bmp = Bitmap.createBitmap(pictureDrawable.getIntrinsicWidth(), pictureDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        //canvas.drawColor(-1);
        canvas.drawRGB(214,215,219);
        canvas.drawPicture(pictureDrawable.getPicture());
        return bmp;
    }

    private void uploadFile() {
        //업로드할 파일이 있으면 수행
        if (mBitmap != null) {
            //업로드 진행 Dialog 보이기
            /*final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("업로드중...");
            progressDialog.show();*/

            //storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseAuth mAuth;
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();

            //Unique한 파일명을 만들자.
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
            Date now = new Date();
            String filename = formatter.format(now) + currentUser.getUid() + ".png";
            //storage 주소와 폴더 파일명을 지정해 준다.
            StorageReference storageRef = storage.getReferenceFromUrl("gs://drawforyou-51628.appspot.com/")
                    .child("images/" + currentUser.getUid()+ filename);

            String fileURL = "https://firebasestorage.googleapis.com/v0/b/drawforyou-51628.appspot.com/o/images%2F"
                    + currentUser.getUid()+ filename + "?alt=media";//firebase storage에 올린 주소

            storageRef.putFile(this.getImageUri(this,mBitmap))
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
                            DocumentReference docRef = db.collection(mAuth.getUid()).document(fname);
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        diaryinfo = task.getResult().toObject(DiaryInfo.class);
                                        if(diaryinfo != null){
                                            db.collection(currentUser.getUid()).document(fname).update("imageURL",fileURL);
                                            Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), DiaryDayView.class);
                                            intent.putExtra("fname",fname);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else{
                                            DiaryInfo tempdiaryinfo = new DiaryInfo();
                                            tempdiaryinfo.setDate(fname);
                                            tempdiaryinfo.setDiary(" ");
                                            tempdiaryinfo.setImageURL(fileURL);
                                            db.collection(currentUser.getUid()).document(fname).set(tempdiaryinfo);
                                            Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), DiaryDayView.class);
                                            intent.putExtra("fname",fname);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                    else{ }
                                }
                            });

                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") //이게 뭘까
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            //progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "/Title/", null);
        return Uri.parse(path);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    class MyView extends View {

        private Paint paint = new Paint();

        //여러가지의 그리기 명령을 모았다가 한번에 출력해주는 버펴역할을 담당
        private Path path = new Path();


        //list
        private ArrayList<Integer> x_cord = new ArrayList<>();
        private ArrayList<Integer> y_cord = new ArrayList<>();
        private ArrayList<Integer> drag_time = new ArrayList<>();

        ArrayList<ArrayList<Integer>> shapes = new ArrayList<>();

        ArrayList<ArrayList<ArrayList<Integer>>> retShapes = new ArrayList<>();

        ArrayList<Path> pathStack = new ArrayList<>();

        LocalTime time = LocalTime.now();

        String json;
        ArrayList<String> srcs = new ArrayList<>();
        ArrayList<String> svgName = new ArrayList<>();
        ArrayList<String> svgFileName = new ArrayList<>();

        //좌표
        private int x, y;

        public MyView(Context context){
            super(context);
        }

        public MyView(Context context,String json){
            super(context);
            this.json = json;
        }

        void reset(){
            mImgTrans.setVisibility(View.GONE);
            mImgTrans2.setVisibility(View.GONE);
            mImgTrans3.setVisibility(View.GONE);
            mImgTrans4.setVisibility(View.GONE);
            mImgTrans5.setVisibility(View.GONE);
            mImgTrans6.setVisibility(View.GONE);
            nonImage.setVisibility(View.VISIBLE);
            paint.reset();
            path.reset();
            setResetAsyncTask setResetAsyncTask = new setResetAsyncTask(DrawingActivity.this);
            setResetAsyncTask.execute("");
            for(Iterator<ArrayList<ArrayList<Integer>>> iter = retShapes.iterator(); iter.hasNext();){
                ArrayList<ArrayList<Integer>> elem = iter.next();
                iter.remove();
            }
            for(Iterator<String> iter = srcs.iterator(); iter.hasNext();){
                String elem = iter.next();
                iter.remove();
            }
            for(Iterator<Path> iter = pathStack.iterator(); iter.hasNext();){
                Path elem = iter.next();
                iter.remove();
            }
            for(Iterator<String> iter = svgName.iterator(); iter.hasNext();){
                String elem = iter.next();
                iter.remove();
            }
            for(Iterator<String> iter = svgFileName.iterator(); iter.hasNext();){
                String elem = iter.next();
                iter.remove();
            }
            for(Iterator<Integer> iter = x_cord.iterator(); iter.hasNext();){
                int elem = iter.next();
                iter.remove();
            }
            for(Iterator<Integer> iter = y_cord.iterator(); iter.hasNext();){
                int elem = iter.next();
                iter.remove();
            }
            for(Iterator<Integer> iter = drag_time.iterator(); iter.hasNext();){
                int elem = iter.next();
                iter.remove();
            }
            for(Iterator<ArrayList<Integer>> iter = shapes.iterator(); iter.hasNext();){
                ArrayList<Integer> elem = iter.next();
                iter.remove();
            }

            invalidate();


        }

        BitmapDrawable thisDraw(){
            Bitmap bitmap = Bitmap.createBitmap(800,800, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            canvas.drawRGB(214,215,219);

            paint.setColor(Color.BLACK);

            //STROCK 속성?
            paint.setStyle(Paint.Style.STROKE);

            //두께
            paint.setStrokeWidth(18);

            //path객체가 가지고 있는 경로를 화면에 그린다.
            canvas.drawPath(path,paint);

            BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);

            return drawable;
        }

        void backDraw(){

            if(pathStack.size() > 0){
                pathStack.remove(pathStack.size()-1);
            }
            if(pathStack.size() == 0){
                nonImage.setVisibility(View.VISIBLE);
                mImgTrans.setVisibility(View.GONE);
                mImgTrans2.setVisibility(View.GONE);
                mImgTrans3.setVisibility(View.GONE);
                mImgTrans4.setVisibility(View.GONE);
                mImgTrans5.setVisibility(View.GONE);
                mImgTrans6.setVisibility(View.GONE);
                path = new Path();
                reset();
            }
            else{
                path = pathStack.get(pathStack.size()-1);
                srcs = new ArrayList<>();
                svgName = new ArrayList<>();
                svgFileName = new ArrayList<>();

                retShapes.remove(retShapes.size()-1);
                Thread t = new Thread(){
                    public void run(){
                        try {
                            requestPost(retShapes);
                            change(svgFileName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };

                DrawingActivity da = new DrawingActivity();
                da.drawingActivity.addAll(srcs);

                t.start();


                try{
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas){

            canvas.drawColor(Color.argb(0,0,0,0));

            paint.setColor(Color.BLACK);

            //STROCK 속성?
            //paint.setStyle(Paint.Style.STROKE);
            paint.setStyle(Paint.Style.STROKE);

            //두께
            paint.setStrokeWidth(12);
            //path객체가 가지고 있는 경로를 화면에 그린다.
            canvas.drawPath(path,paint);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public boolean onTouchEvent(MotionEvent event){
            x = (int)event.getX();
            y = (int)event.getY();
            srcs = new ArrayList<>();
            svgName = new ArrayList<>();
            svgFileName = new ArrayList<>();
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(x,y);
                    time = LocalTime.now();
                    break;
                case MotionEvent.ACTION_MOVE:
                    x = (int)event.getX();
                    y = (int)event.getY();

                    path.lineTo(x,y);

                    LocalTime currentTime = LocalTime.now();
                    long l = time.until(currentTime, ChronoUnit.MILLIS);

                    x_cord.add(x);
                    y_cord.add(y);
                    drag_time.add((int) l);
                    break;
                case MotionEvent.ACTION_UP:

                    ArrayList<Integer> new_x = new ArrayList<>(x_cord);
                    ArrayList<Integer> new_y = new ArrayList<>(y_cord);
                    ArrayList<Integer> new_drag = new ArrayList<>(drag_time);

                    shapes.add(new_x);
                    shapes.add(new_y);
                    shapes.add(new_drag);

                    ArrayList<ArrayList<Integer>> new_shapes = new ArrayList<>(shapes);

                    retShapes.add(new_shapes);

                    Thread t = new Thread(){
                        public void run(){
                            try {
                                requestPost(retShapes);
                                change(svgFileName);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    };

                    DrawingActivity da = new DrawingActivity();
                    da.drawingActivity.addAll(srcs);

                    t.start();


                    try{
                        t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    for(Iterator<Integer> iter = x_cord.iterator(); iter.hasNext();){
                        int elem = iter.next();
                        iter.remove();
                    }
                    for(Iterator<Integer> iter = y_cord.iterator(); iter.hasNext();){
                        int elem = iter.next();
                        iter.remove();
                    }
                    for(Iterator<Integer> iter = drag_time.iterator(); iter.hasNext();){
                        int elem = iter.next();
                        iter.remove();
                    }
                    for(Iterator<ArrayList<Integer>> iter = shapes.iterator(); iter.hasNext();){
                        ArrayList<Integer> elem = iter.next();
                        iter.remove();
                    }
                    pathStack.add(new Path(path));
                    nonImage.setVisibility(View.GONE);
                    mImgTrans.setVisibility(View.VISIBLE);
                    mImgTrans2.setVisibility(View.VISIBLE);
                    mImgTrans3.setVisibility(View.VISIBLE);
                    mImgTrans4.setVisibility(View.VISIBLE);
                    mImgTrans5.setVisibility(View.VISIBLE);
                    mImgTrans6.setVisibility(View.VISIBLE);
                    break;
            }

            //View의 onDraw()를 호출하는 메서드
            invalidate();

            return true;
        }



        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public void requestPost(ArrayList<ArrayList<ArrayList<Integer>>> ink) throws JSONException {

            String requestURL = "https://inputtools.google.com/request?ime=handwriting&app=autodraw&dbg=1&cs=1&oe=UTF-8";
            JSONObject jsonVar = new JSONObject();
            JSONObject jsonWriting_guide = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonFinal = new JSONObject();
            jsonFinal.put("input_type",0);
            JSONArray inkarr = new JSONArray();
            JSONArray inkarr2 = new JSONArray();
            JSONArray inkarr3 = new JSONArray();


            for(int i = 0; i < ink.size(); i++){
                for(int j = 0; j < ink.get(i).size(); j++){
                    for(int k = 0; k < ink.get(i).get(j).size(); k++){
                        inkarr.put(ink.get(i).get(j).get(k));
                    }
                    inkarr2.put(inkarr);
                    inkarr = new JSONArray();
                }
                inkarr3.put(inkarr2);
                inkarr2 = new JSONArray();
            }

            jsonWriting_guide.put("width",900);
            jsonWriting_guide.put("height", 900);

            jsonVar.put("language","autodraw");
            jsonVar.put("writing_guide",jsonWriting_guide);
            jsonVar.put("ink", inkarr3);
            jsonArray.put(jsonVar);

            jsonFinal.put("requests",jsonArray);

            try {
                CloseableHttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
                HttpPost postRequest = new HttpPost(requestURL); //POST 메소드 URL 새성
                postRequest.setHeader("Accept", "application/json");
                postRequest.setHeader("Connection", "keep-alive");
                postRequest.setHeader("Content-Type", "application/json");
                postRequest.setHeader("Accept-Encoding","UTF-8");
                //postRequest.addHeader("Authorization", token); // token 이용시

                postRequest.setEntity(new StringEntity(String.valueOf(jsonFinal),"UTF-8")); //json 메시지 입력

                HttpResponse response = client.execute(postRequest);

                //Response 출력
                if (response.getStatusLine().getStatusCode() == 200) {
                    ResponseHandler<String> handler = new BasicResponseHandler();
                    String body = handler.handleResponse(response);

                    //System.out.println(body);

                    String parsing = body.toString();
                    ArrayList<String> list = new ArrayList<>();
                    String[] splitStr = parsing.split("\\]");
                    for(int i=0; i<splitStr.length; i++){
                        list.add(splitStr[i]);
                    }
                    //System.out.println(list.get(0));
                    ArrayList<String> list2 = new ArrayList<>();
                    String[] splitStr2 = list.get(0).split("\\[");
                    for(int i=0; i<splitStr2.length; i++){
                        list2.add(splitStr2[i]);
                    }

                    ArrayList<String> list3 = new ArrayList<>();
                    String[] splitStr3 = list2.get(4).replace(" ","").replace("-","").split(",");
                    for(int i = 0; i < splitStr3.length; i++){
                        list3.add(splitStr3[i].replace("\n","").replace("\"",""));
                    }

                    for(int i = 0; i < list3.size(); i++){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            jsonParsing(json,list3.get(i));
                        }
                    }

                    for(int i = 0; i < srcs.size(); i++){
                        System.out.println(srcs.get(i));
                    }

                    for(int i = 0; i < list3.size(); i++){
                        svgFileName = findSvg(svgFileName, list3.get(i));
                    }


                } else {
                    System.out.println("response is error : " + response.getStatusLine().getStatusCode());
                }
            } catch (Exception e){
                System.err.println(e.toString());
            }
        }

        private ArrayList<String> findSvg(ArrayList<String> fileName , String key){
            try {
                for(int i = 1; ;i++){
                    getAssets().open(key+"-0"+ i +".svg");
                    System.out.println(key+"-0"+ i +".svg");
                    fileName.add(key+"-0"+ i +".svg");
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
            return fileName;
        }

        private void jsonParsing(String json, String key)
        {
            try{
                JSONObject jsonObject = new JSONObject(json);

                JSONArray movieArray = jsonObject.getJSONArray(key);

                for(int i=0; i<movieArray.length(); i++)
                {
                    JSONObject movieObject = movieArray.getJSONObject(i);
//                    if(srcs.size() == 6)
//                        break;
                    String[] splitStr2 = movieObject.getString("src").split("/");
                    System.out.println(splitStr2[splitStr2.length-1]);
                    svgName.add(splitStr2[splitStr2.length-1]);
                    srcs.add(movieObject.getString("src"));
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String getJsonString()
    {
        String json = "";

        try {
            InputStream is = getAssets().open("stencils.json");
            int fileSize = is.available();

            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        return json;
    }

    @SuppressLint("StaticFieldLeak")
    private class setResetAsyncTask extends AsyncTask<String, String, Boolean> {
        private Context mContext = null;

        public setResetAsyncTask(Context context){
            mContext = context;
        }

        @SafeVarargs
        @Override
        protected final Boolean doInBackground(String... strings) {
            publishProgress("");
            return null;
        }

        @SafeVarargs
        @Override
        protected final void onProgressUpdate(String... strings){

        }


    }

    @SuppressLint("StaticFieldLeak")
    private class setSelectAsyncTask extends AsyncTask<String, String, Boolean> {
        private Context mContext = null;

        public setSelectAsyncTask(Context context){
            mContext = context;
        }

        @SafeVarargs
        @Override
        protected final Boolean doInBackground(String... strings) {
            publishProgress(strings);
            return null;
        }

        @SafeVarargs
        @Override
        protected final void onProgressUpdate(String... strings){

            SVG svg = null;
            try {
                svg = SVG.getFromAsset(getAssets(),strings[0]);
                Drawable drawable = new PictureDrawable(svg.renderToPicture());
                changeView.setImageDrawable(drawable);
            } catch (SVGParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    @SuppressLint("StaticFieldLeak")
    private  class setSvgAsyncTask extends AsyncTask<ArrayList<String>, ArrayList<String>, Boolean>{

        private Context mContext = null;

        public setSvgAsyncTask(Context context){
            mContext = context;
        }
        @SafeVarargs
        @Override
        protected final Boolean doInBackground(ArrayList<String>... svgFileName) {
            System.out.println("svgname : "+svgFileName[0].toString());
            System.out.println("svg size : "+svgFileName.length);
            publishProgress(svgFileName[svgFileName.length-1]);
            return null;
        }

        @SafeVarargs
        @Override
        protected final void onProgressUpdate(ArrayList<String>... svgFileName){
            try {
                System.out.println("progressUpdate");
                SVG svg = SVG.getFromAsset(getAssets(),svgFileName[svgFileName.length-1].get(0));
                Drawable drawable = new PictureDrawable(svg.renderToPicture());
                mImgTrans.setImageDrawable(drawable);

                SVG svg2 = SVG.getFromAsset(getAssets(),svgFileName[svgFileName.length-1].get(1));
                Drawable drawable2 = new PictureDrawable(svg2.renderToPicture());
                mImgTrans2.setImageDrawable(drawable2);

                SVG svg3 = SVG.getFromAsset(getAssets(),svgFileName[svgFileName.length-1].get(2));
                Drawable drawable3 = new PictureDrawable(svg3.renderToPicture());
                mImgTrans3.setImageDrawable(drawable3);

                SVG svg4 = SVG.getFromAsset(getAssets(),svgFileName[svgFileName.length-1].get(3));
                Drawable drawable4 = new PictureDrawable(svg4.renderToPicture());
                mImgTrans4.setImageDrawable(drawable4);

                SVG svg5 = SVG.getFromAsset(getAssets(),svgFileName[svgFileName.length-1].get(4));
                Drawable drawable5 = new PictureDrawable(svg5.renderToPicture());
                mImgTrans5.setImageDrawable(drawable5);

                SVG svg6 = SVG.getFromAsset(getAssets(),svgFileName[svgFileName.length-1].get(5));
                Drawable drawable6 = new PictureDrawable(svg6.renderToPicture());
                mImgTrans6.setImageDrawable(drawable6);

            } catch (SVGParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void change(ArrayList<String> svgFileName){
        setSvgAsyncTask setSvgAsyncTask = new setSvgAsyncTask(DrawingActivity.this);
        setSvgAsyncTask.execute(svgFileName);
    }

    private int checkFile(){
        //storage
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        final int[] num = {0};

        db.collection(mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.get("date").toString().contains(fname)
                                        &&(document.get("diary").toString() != null)){
                                    num[0] = 1;
                                }
                                else {
                                    num[0] = 0;
                                }
                            }
                        } else {

                        }
                    }
                });
        return num[0];
    }
}