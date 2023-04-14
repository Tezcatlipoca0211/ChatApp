package com.example.chatapp.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewItemContactActivity extends AppCompatActivity {
    Toolbar toolbar_singleContact;
    TextView tvDescribeSingleContact, tvUserNameSingleContact, tvEmailSingleContact;
    Button btnSendFriendRequest, btnCancelSendFriendRequest, btnBackInViewSingleContact;
    CircleImageView civAvatarSingleContact;
    String profilePicURL, userName, status, email, gender, describe, friendID, userID, currentState;
    String myProfilePic, myUsername, myEmail, myGender, myDescribe, myUserID;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mUserReference, mRequestReference, mFriendsReference, mDataReference;
    FirebaseStorage storage;
    StorageReference mStorageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item_contact);
        setControl();
        setEvent();
    }


    private void setControl() {
        toolbar_singleContact = findViewById(R.id.toolbar_singleContact);
        civAvatarSingleContact = findViewById(R.id.civAvatarSingleContact);
        tvDescribeSingleContact = findViewById(R.id.tvDescribeSingleContact);
        tvUserNameSingleContact = findViewById(R.id.tvUserNameSingleContact);
        tvEmailSingleContact = findViewById(R.id.tvEmailSingleContact);
        btnSendFriendRequest = findViewById(R.id.btnSendFriendRequest);
        btnCancelSendFriendRequest = findViewById(R.id.btnCancelSendFriendRequest);
        btnBackInViewSingleContact = findViewById(R.id.btnBackInViewSingleContact);
        userID = getIntent().getStringExtra("userID"); // Sử dụng put/getExtra để truyền dữ liệu từ ContactAdapter sang ViewItemContactActivity
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDataReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        mRequestReference = FirebaseDatabase.getInstance().getReference().child("Requests");
        mFriendsReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        storage = FirebaseStorage.getInstance();
        mStorageReference = storage.getReference().child("profilePic/default_avatar.png");
        currentState = "nothing_happen";
    }


    private void setEvent() {

        btnBackInViewSingleContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        /* Xử lý sự kiện nút GỬI kết bạn */
        btnSendFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAction(userID);
            }
        });
        /* Xử lý sự kiện nút HỦY kết bạn */
        btnCancelSendFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAction(userID);
            }
        });
        checkUserExistance(userID); //Check trạng thái của Request

        callInformationItemContact(); // Hiển thị thông tin của người dùng khi click vào
        loadMyProfile(); //Load dữ liệu của bản thân
    }

    private void loadMyProfile() {
        mDataReference.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    myUserID = snapshot.child("userID").getValue().toString();
                    myUsername = snapshot.child("userName").getValue().toString();
                    myEmail = snapshot.child("email").getValue().toString();
                    // Lấy thông tin ảnh đại diện của User
                    if (snapshot.hasChild("profilePic")) {
                        myProfilePic = snapshot.child("profilePic").getValue().toString();
                    } else {
                        mStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                myProfilePic = uri.toString();
                            }
                        });
                    }
                    if (snapshot.hasChild("describe")) {
                        myDescribe = snapshot.child("describe").getValue().toString();
                    } else {
                        myDescribe = "";
                    }
                    if (snapshot.hasChild("gender")) {
                        myGender = snapshot.child("gender").getValue().toString();
                    } else {
                        myGender = "";
                    }
                } else {
                    Toast.makeText(ViewItemContactActivity.this, "Không tìm thấy dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewItemContactActivity.this, "" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void callInformationItemContact() {
        mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    friendID = snapshot.child("userID").getValue().toString();
                    userName = snapshot.child("userName").getValue().toString();
                    email = snapshot.child("email").getValue().toString();
                    // Lấy thông tin ảnh đại diện của User
                    if (snapshot.hasChild("profilePic")) {
                        profilePicURL = snapshot.child("profilePic").getValue().toString();
                    } else {
                        mStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                profilePicURL = uri.toString();
                            }
                        });
                    }
                    if (snapshot.hasChild("describe")) {
                        describe = snapshot.child("describe").getValue().toString();
                    } else {
                        describe = "";
                    }
                    if (snapshot.hasChild("gender")) {
                        gender = snapshot.child("gender").getValue().toString();
                    } else {
                        gender = "";
                    }
                    Picasso.get().load(profilePicURL).placeholder(R.drawable.default_avatar).into(civAvatarSingleContact);
                    tvDescribeSingleContact.setText(describe);
                    tvUserNameSingleContact.setText(userName);
                    tvEmailSingleContact.setText(email);
                } else {
                    Toast.makeText(ViewItemContactActivity.this, "Không tìm thấy dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewItemContactActivity.this, "" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void sendAction(String userID) {

        if (currentState.equals("nothing_happen")) {
            HashMap hashMap = new HashMap();
            hashMap.put("status", "pending");
            mRequestReference.child(mUser.getUid()).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        HashMap mHashMap = new HashMap();
                        mHashMap.put("status", "wait_confirm");
                        mHashMap.put("userName", myUsername);
                        mHashMap.put("profilePic", myProfilePic);
                        mHashMap.put("userID", myUserID);
                        mRequestReference.child(userID).child(mUser.getUid()).updateChildren(mHashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ViewItemContactActivity.this, "Bạn đã gửi lời mời kết bạn", Toast.LENGTH_SHORT).show();
                                    btnCancelSendFriendRequest.setVisibility(View.GONE);
                                    currentState = "i_sent_pending";
                                    btnSendFriendRequest.setText(R.string.button_cancel_send);
                                } else {
                                    Toast.makeText(ViewItemContactActivity.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }
        if (currentState.equals("i_sent_pending") || currentState.equals("i_sent_decline")) {
            mRequestReference.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        mRequestReference.child(userID).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ViewItemContactActivity.this, "Bạn đã hủy yêu cầu kết bạn", Toast.LENGTH_SHORT).show();
                                    currentState = "nothing_happen";
                                    btnSendFriendRequest.setText(R.string.button_send_friend_request);
                                    btnCancelSendFriendRequest.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(ViewItemContactActivity.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }
        if (currentState.equals("he_sent_pending")) {
            mRequestReference.child(userID).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        mRequestReference.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    final HashMap hashMap = new HashMap();
                                    hashMap.put("status", "friend");
                                    hashMap.put("friendID",friendID);
                                    hashMap.put("userName", userName);
                                    hashMap.put("profilePic", profilePicURL);
                                    hashMap.put("email",email);
                                    hashMap.put("describe",describe);
                                    hashMap.put("gender",gender);

                                    //Thông tin của bản thân sẽ lưu trong node của bạn bè
                                    final HashMap hashMap1 = new HashMap();
                                    hashMap1.put("status", "friend");
                                    hashMap1.put("friendID",myUserID);
                                    hashMap1.put("userName", myUsername);
                                    hashMap1.put("profilePic", myProfilePic);
                                    hashMap1.put("email",myEmail);
                                    hashMap1.put("describe",myDescribe);
                                    hashMap1.put("gender",myGender);
                                    mFriendsReference.child(mUser.getUid()).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task.isSuccessful()) {
                                                mFriendsReference.child(userID).child(mUser.getUid()).updateChildren(hashMap1).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {
                                                        Toast.makeText(ViewItemContactActivity.this, "Các bạn đã là bạn bè", Toast.LENGTH_SHORT).show();
                                                        currentState = "friend";
                                                        btnSendFriendRequest.setText(R.string.button_send_message);
                                                        btnCancelSendFriendRequest.setText(R.string.button_unfriend);
                                                        btnCancelSendFriendRequest.setVisibility(View.VISIBLE);
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }

                }
            });
        }
        if (currentState.equals("friend")) {
            Intent intent = new Intent(ViewItemContactActivity.this, ChatActivity.class);
            intent.putExtra("userID", userID);
            startActivity(intent);
        }
    }

    private void checkUserExistance(String userID) {
        mFriendsReference.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentState = "friend";
                    btnSendFriendRequest.setText(R.string.button_send_message);
                    btnCancelSendFriendRequest.setText(R.string.button_unfriend);
                    btnCancelSendFriendRequest.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mFriendsReference.child(userID).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentState = "friend";
                    btnSendFriendRequest.setText(R.string.button_send_message);
                    btnCancelSendFriendRequest.setText(R.string.button_unfriend);
                    btnCancelSendFriendRequest.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mRequestReference.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("status").getValue().toString().equals("pending")) {
                        currentState = "i_sent_pending";
                        btnSendFriendRequest.setText(R.string.button_cancel_send);
                        btnCancelSendFriendRequest.setVisibility(View.GONE);
                    }
                    if (snapshot.child("status").getValue().toString().equals("decline")) {
                        currentState = "i_sent_decline";
                        btnSendFriendRequest.setText(R.string.button_cancel_send);
                        btnCancelSendFriendRequest.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mRequestReference.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("status").getValue().toString().equals("wait_confirm")) {
                        mRequestReference.child(userID).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    if (snapshot.child("status").getValue().toString().equals("pending")) {
                                        currentState = "he_sent_pending";
                                        btnSendFriendRequest.setText(R.string.button_accept_friend_request);
                                        btnCancelSendFriendRequest.setText(R.string.button_decline);
                                        btnCancelSendFriendRequest.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (currentState.equals("nothing_happen")) {
            currentState = "nothing_happen";
            btnSendFriendRequest.setText(R.string.button_send_friend_request);
            btnCancelSendFriendRequest.setVisibility(View.GONE);
        }
    }

    private void cancelAction(String userID) {
        if (currentState.equals("friend")){
            mFriendsReference.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        mFriendsReference.child(userID).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    openConfirmUnfriendDialog(Gravity.CENTER);
                                }

                            }
                        });
                    }
                }
            });
        }
        if (currentState.equals("he_sent_pending")) {
            mRequestReference.child(userID).child(mAuth.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener() {

                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        mRequestReference.child(mAuth.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ViewItemContactActivity.this, "Đã từ chối kết bạn", Toast.LENGTH_SHORT).show();
                                    currentState = "nothing_happen";
                                    btnSendFriendRequest.setText(R.string.button_send_friend_request);
                                    btnCancelSendFriendRequest.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void openConfirmUnfriendDialog(int gravity) {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.confirm_unfriend_dialog);
            Window window = (Window) dialog.getWindow();
            if (window == null) {
                return;
            } else {
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                WindowManager.LayoutParams windowAttributes = window.getAttributes();
                window.setAttributes(windowAttributes);

                if (Gravity.CENTER == gravity) {
                    dialog.setCancelable(true);
                } else {
                    dialog.setCancelable(false);
                }
                Button btnConfirm = dialog.findViewById(R.id.btnConfirmUnfriend);
                Button btnCancelConfirm = dialog.findViewById(R.id.btnCancelConfirmUnfriend);

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        Toast.makeText(ViewItemContactActivity.this, "Đã hủy kết bạn", Toast.LENGTH_SHORT).show();
                        currentState = "nothing_happen";
                        btnSendFriendRequest.setText(R.string.button_send_friend_request);
                        btnCancelSendFriendRequest.setVisibility(View.GONE);
                    }
                });
                btnCancelConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
            dialog.show();
        }
}