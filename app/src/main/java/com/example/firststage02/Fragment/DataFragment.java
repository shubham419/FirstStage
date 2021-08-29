package com.example.firststage02.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.firststage02.Database.ImageDatabase;
import com.example.firststage02.Database.ImageEntity;
import com.example.firststage02.R;
import com.example.firststage02.RecyclerView.Adapter.ImageDataAdapter;
import com.example.firststage02.databinding.FragmentDataBinding;

import java.util.ArrayList;
import java.util.List;


public class DataFragment extends Fragment {

    public DataFragment() {
        // Required empty public constructor
    }

    private FragmentDataBinding binding;

    List<ImageEntity> list = new ArrayList<>();
    ImageDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDataBinding.inflate(inflater, container, false);
        DataFragment context = DataFragment.this;

        database = ImageDatabase.getInstance(context.getContext());
        list = database.getImage().getAlImages();

        ImageDataAdapter imageDataAdapter = new ImageDataAdapter(list, context);
        binding.dataRecyclerview.setAdapter(imageDataAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context.getContext());
        binding.dataRecyclerview.setLayoutManager(layoutManager);


        return binding.getRoot();
    }
}