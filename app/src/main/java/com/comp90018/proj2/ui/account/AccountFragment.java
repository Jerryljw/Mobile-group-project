package com.comp90018.proj2.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.comp90018.proj2.databinding.FragmentAccountBinding;

public class AccountFragment extends Fragment {

    private AccountViewModel accountViewModel;
    private FragmentAccountBinding binding;
    private TextView occupationTxtView, nameTxtView, workTxtView;
    private TextView emailTxtView, phoneTxtView, videoTxtView, facebookTxtView, twitterTxtView;
    private ImageView userImageView, emailImageView, phoneImageView, videoImageView;
    private ImageView facebookImageView, twitterImageView;

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
        twitterTxtView = binding.twitterTextview;

        userImageView = binding.userImageview;
        emailImageView = binding.emailImageview;
        phoneImageView = binding.phoneImageview;
        videoImageView = binding.videoImageview;
        facebookImageView = binding.facebookImageview;
        twitterImageView = binding.twitterImageview;

        emailTxtView.setText("email");
        occupationTxtView.setText("profession");
        workTxtView.setText("workplace");
        phoneTxtView.setText("phone");
        videoTxtView.setText("phone");

        accountViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;




    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}