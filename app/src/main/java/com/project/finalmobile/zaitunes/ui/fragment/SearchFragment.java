package com.project.finalmobile.zaitunes.ui.fragment;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.project.finalmobile.R;
import com.project.finalmobile.databinding.FragmentSearchBinding;
import com.project.finalmobile.zaitunes.local.DatabaseContract.TrackColumns;
import com.project.finalmobile.zaitunes.local.RatedTrackHelper;
import com.project.finalmobile.zaitunes.ui.adapter.TrackAdapter;
import com.project.finalmobile.zaitunes.api.ApiService;
import com.project.finalmobile.zaitunes.api.RetrofitClient;
import com.project.finalmobile.zaitunes.model.Response;
import com.project.finalmobile.zaitunes.model.ResultsItem;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import android.database.Cursor;

public class SearchFragment extends Fragment implements TrackAdapter.OnTrackClickListener {

    private FragmentSearchBinding binding;
    private ApiService apiService;
    private TrackAdapter trackAdapter;
    private RatedTrackHelper ratedTrackHelper; // Mengganti AppDatabase

    public SearchFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService = RetrofitClient.getApiService();
        // Menggunakan RatedTrackHelper
        ratedTrackHelper = RatedTrackHelper.getInstance(requireContext());
        ratedTrackHelper.open();
        setUpRecyclerView();

        binding.searchButton.setOnClickListener(v -> {
            String searchQuery = binding.searchEditText.getText().toString().trim();
            if (!searchQuery.isEmpty()) {
                performSearch(searchQuery);
            }
        });
    }

    private void setUpRecyclerView() {
        trackAdapter = new TrackAdapter(new ArrayList<>(), this);
        binding.trackRv.setAdapter(trackAdapter);
        binding.trackRv.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void performSearch(String searchQuery) {
        // Metode ini tidak berubah
        // ... (biarkan isi metode performSearch tetap sama)
    }

    @Override
    public void onTrackClick(ResultsItem track) {
        if (track != null) {
            // Menggunakan ContentValues untuk menyimpan data
            ContentValues values = new ContentValues();
            values.put(TrackColumns._ID, track.getTrackId());
            values.put(TrackColumns.TRACK_NAME, track.getTrackName());
            values.put(TrackColumns.ARTIST_NAME, track.getArtistName());
            values.put(TrackColumns.COLLECTION_NAME, track.getCollectionName());
            values.put(TrackColumns.GENRE, track.getPrimaryGenreName());
            values.put(TrackColumns.RELEASE_DATE, track.getReleaseDate());
            values.put(TrackColumns.TRACK_VIEW_URL, track.getTrackViewUrl());
            values.put(TrackColumns.RATING, 0); // Rating awal

            Executors.newSingleThreadExecutor().execute(() -> {
                // Cek dulu apakah data sudah ada, jika sudah update, jika belum insert
                Cursor cursor = ratedTrackHelper.queryById(String.valueOf(track.getTrackId()));
                if (cursor != null && cursor.getCount() > 0) {
                    // Data sudah ada, tidak perlu di-insert lagi agar tidak menimpa rating
                    cursor.close();
                } else {
                    ratedTrackHelper.insert(values);
                }
            });

            Bundle bundle = new Bundle();
            bundle.putLong("trackId", track.getTrackId());
            Navigation.findNavController(requireView()).navigate(R.id.action_searchFragment_to_playerFragment, bundle);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ratedTrackHelper.close(); // Tutup helper
        binding = null;
    }
}