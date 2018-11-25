package com.example.laura_000.inclass10;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessagesActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private DatabaseReference ref, dbRef;
    private Intent intent;

    TextView userName;
    ImageView logOut, addImage;
    Button sendMessage;
    EditText editMessage;
    ListView listView;
    private MessageAdapter messageAdapter;
    private boolean imageWasAdded;
    private Bitmap currentImage;


    String userId;
    ArrayList<MessageItem> messageArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        setTitle("Chat Room");
        userId = getIntent().getExtras().getString("id");

        imageWasAdded = false;

        userName = findViewById(R.id.textUserName);
        logOut = findViewById(R.id.imageLogOut);
        addImage = findViewById(R.id.imageAdd);
        sendMessage = findViewById(R.id.buttonSend);
        editMessage = findViewById(R.id.editMessage);
        listView = findViewById(R.id.listViewMessages);

        messageArray = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference("Messages");
        ref = db.getReference().child("users").child(userId).getRef();


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName.setText(dataSnapshot.child("firstName").getValue() + " " + dataSnapshot.child("lastName").getValue());
                String post = (String)dataSnapshot.child("firstName").getValue();
                Log.d("demo", "post " + post);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("demo", "The read failed: " + databaseError.getCode());
            }
        });

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageArray.clear();

                for (DataSnapshot postSnap : dataSnapshot.getChildren()) {
                    messageArray.add(postSnap.getValue(MessageItem.class));
                }
                Log.d("demo", "eregjf " + messageArray.toString());

                messageAdapter = new MessageAdapter(MessagesActivity.this, R.layout.message_item, messageArray, userId);
                listView.setAdapter(messageAdapter);

                scrollMyListViewToBottom();
                addImage.setImageResource(R.drawable.addimage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                intent = new Intent(MessagesActivity.this, MainActivity.class);
                intent.putExtra("id", userId);
                startActivity(intent);
                finish();
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);

            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MessageItem message = new MessageItem();

                message.setUser((String)userName.getText());

                message.setContent(editMessage.getText().toString());
                message.setUserId(userId);

                Date dt = new Date();
                message.setDateTime(new SimpleDateFormat("dd/MM/yy").format(dt) + ", " + new SimpleDateFormat("hh:mm a").format(dt));

                message.setMsgKey(dbRef.child("Messages").push().getKey());

                if (imageWasAdded) {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + message.getMsgKey() + ".jpeg");

                    addImage.setDrawingCacheEnabled(true);
                    addImage.buildDrawingCache();

                    Bitmap bitmap = ((BitmapDrawable) addImage.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = storageReference.putBytes(data);

                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String url = task.getResult().toString();
                                    message.setMsgImage(url);
                                    dbRef.child(message.getMsgKey()).setValue(message);

                                    editMessage.setText("");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MessagesActivity.this, "Could not add image.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    });

                    imageWasAdded = false;
                    return;
                }

                if (!message.equals("")) {
                    message.setMsgImage("null");
                    dbRef.child(message.getMsgKey()).setValue(message);

                    editMessage.setText("");
                } else {
                    Toast.makeText(MessagesActivity.this, "No text and/or image.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            imageWasAdded = true;

            Uri photoUri = data.getData();
            if (photoUri != null) {
                try {
                    currentImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                    addImage.setImageBitmap(currentImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void scrollMyListViewToBottom() {
        listView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView.setSelection(messageAdapter.getCount() - 1);
            }
        });
    }


}
