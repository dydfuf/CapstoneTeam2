package com.example.draw4u;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoFromPhone extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Button btChoose;
    private Button btUpload;
    private ImageView ivPreview;
    public String fname;

    private Uri filePath;
    DiaryInfo diaryinfo = new DiaryInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_photo_from_phone);

        Intent intent = getIntent();
        fname = intent.getExtras().getString("fname");

        btChoose = (Button) findViewById(R.id.bt_choose);
        btUpload = (Button) findViewById(R.id.bt_upload);
        ivPreview = (ImageView) findViewById(R.id.iv_preview);

        //버튼 클릭 이벤트
        btChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //이미지를 선택
                Intent intent = new Intent();
                intent.setType("image/Camera/");
                //intent.setType("DCIM/Camera/*");

                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
            }
        });

        btUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //업로드
                uploadFile();
                /*Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), DiaryDayView.class);
                intent.putExtra("fname",fname);
                startActivity(intent);*/
            }
        });
    }

    //결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
        if(requestCode == 0 && resultCode == RESULT_OK){
            filePath = data.getData();
            Log.d(TAG, "uri:" + String.valueOf(filePath));
            try {
                //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ivPreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //upload the file
    private void uploadFile() {
        //업로드할 파일이 있으면 수행
        if (filePath != null) {
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

            storageRef.putFile(filePath)
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
                            @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                             double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            //progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
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
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        return num[0];
    }

}