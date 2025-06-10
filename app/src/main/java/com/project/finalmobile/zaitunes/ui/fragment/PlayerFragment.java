package com.project.finalmobile.zaitunes.ui.fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.bumptech.glide.Glide;
import com.project.finalmobile.databinding.FragmentPlayerBinding;
import com.project.finalmobile.zaitunes.local.DatabaseContract.TrackColumns;
import com.project.finalmobile.zaitunes.local.MappingHelper;
import com.project.finalmobile.zaitunes.local.RatedTrack;
import com.project.finalmobile.zaitunes.local.RatedTrackHelper;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class PlayerFragment extends Fragment {

    private FragmentPlayerBinding binding;
    private RatedTrackHelper ratedTrackHelper; // Mengganti AppDatabase
    private long trackId;
    private RatedTrack currentTrack;

    public PlayerFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            trackId = getArguments().getLong("trackId");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPlayerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ratedTrackHelper = RatedTrackHelper.getInstance(requireContext());
        ratedTrackHelper.open();
        loadTrackDetails();

        binding.backButton.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
    }

    private void loadTrackDetails() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Cursor cursor = ratedTrackHelper.queryById(String.valueOf(trackId));
            ArrayList<RatedTrack> list = MappingHelper.mapCursorToArrayList(cursor);
            if (!list.isEmpty()) {
                currentTrack = list.get(0);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> updateUi(currentTrack));
                }
            }
        });
    }

    private void updateUi(RatedTrack track) {
        if (binding == null) return;
        binding.trackNameTextView.setText(track.getTrackName());
        // ... set text dan gambar lainnya ...
        binding.detailRatingBar.setRating(track.getRating());

        binding.detailRatingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                saveNewRating(rating);
            }
        });
    }

    private void saveNewRating(float rating) {
        ContentValues values = new ContentValues();
        values.put(TrackColumns.RATING, rating);

        Executors.newSingleThreadExecutor().execute(() -> {
            ratedTrackHelper.update(String.valueOf(currentTrack.getTrackId()), values);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Rating diperbarui!", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ratedTrackHelper.close();
        binding = null;
    }
}