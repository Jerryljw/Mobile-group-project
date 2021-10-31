package com.comp90018.proj2.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.request.RequestOptions;
import com.comp90018.proj2.R;
import com.comp90018.proj2.databinding.FragmentAccountBinding;
import com.comp90018.proj2.ui.login.LoginActivity;
import com.comp90018.proj2.utils.GlideApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class AccountFragment extends Fragment {

    private AccountViewModel accountViewModel;
    private FragmentAccountBinding binding;
    private TextView occupationTxtView, nameTxtView, workTxtView;
    private TextView emailTxtView, phoneTxtView, videoTxtView, facebookTxtView, twitterTxtView;
    private ImageView userImageView, emailImageView, phoneImageView, videoImageView;
    private ImageView facebookImageView, twitterImageView;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAccount;
        occupationTxtView = binding.occupationTextview;
        workTxtView = binding.workplaceTextview;
        emailTxtView = binding.emailTextview;
        phoneTxtView = binding.phoneTextview;
        videoTxtView = binding.videoTextview;
        facebookTxtView = binding.facebookTextview;
//        twitterTxtView = binding.twitterTextview;

        userImageView = binding.userImageview;
        emailImageView = binding.emailImageview;
        phoneImageView = binding.phoneImageview;
        videoImageView = binding.videoImageview;
        facebookImageView = binding.facebookImageview;
//        twitterImageView = binding.twitterImageview;


        FirebaseUser currentUser = mAuth.getCurrentUser();

        String Name = currentUser.getDisplayName();
        if (Name.equals("")){
            Name = "Default Name";
        }
        emailTxtView.setText(currentUser.getEmail());
        occupationTxtView.setText(currentUser.getUid());
        workTxtView.setText(Name);
        phoneTxtView.setText("phone");
        videoTxtView.setText("Country");


        GlideApp.with(this)
                .load(String.valueOf(mAuth.getCurrentUser().getPhotoUrl()))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_card_portrait)
                        .fitCenter())
                .into(userImageView);

        accountViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        final Button logoutButton = binding.logout;
        logoutButton.setOnClickListener(view -> {
            mAuth.signOut();

            Intent intent = new Intent();
            intent.setClass(requireActivity().getApplicationContext(), LoginActivity.class);
            startActivity(intent);

        });

        return root;




    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}