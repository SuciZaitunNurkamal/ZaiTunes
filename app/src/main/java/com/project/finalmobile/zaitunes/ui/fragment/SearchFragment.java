package com.project.finalmobile.zaitunes.ui.fragment;

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
import com.project.finalmobile.zaitunes.local.AppDatabase;
import com.project.finalmobile.zaitunes.local.RatedTrack;
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

public class SearchFragment extends Fragment implements TrackAdapter.OnTrackClickListener {

    private FragmentSearchBinding binding;
    private ApiService apiService;
    private TrackAdapter trackAdapter;
    private AppDatabase db;

    public SearchFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getApiService();
        db = AppDatabase.getDatabase(requireContext());
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
        apiService.searchMusicSongs(searchQuery, 20).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(@NonNull Call<Response> call, @NonNull retrofit2.Response<Response> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ResultsItem> tracks = response.body().getResults();
                    if (tracks != null) {
                        trackAdapter.updateData(tracks);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Response> call, @NonNull Throwable t) {
                // Handle failure
            }
        });
    }

    // Metode ini dipanggil saat item di RecyclerView diklik
    @Override
    public void onTrackClick(ResultsItem track) {
        if (track != null) {
            // 1. Buat objek RatedTrack dari data API
            RatedTrack trackToSave = new RatedTrack();
            trackToSave.setTrackId(track.getTrackId());
            trackToSave.setTrackName(track.getTrackName());
            trackToSave.setArtistName(track.getArtistName());
            trackToSave.setArtworkUrl100(track.getArtworkUrl100());
            trackToSave.setCollectionName(track.getCollectionName());
            trackToSave.setPrimaryGenreName(track.getPrimaryGenreName());
            trackToSave.setReleaseDate(track.getReleaseDate());
            // Beri rating awal 0 karena belum dinilai
            trackToSave.setRating(0);

            // 2. Simpan ke database di background thread
            Executors.newSingleThreadExecutor().execute(() -> {
                db.ratedTrackDao().insert(trackToSave);
            });

            // 3. Siapkan argumen dan lakukan navigasi
            Bundle bundle = new Bundle();
            bundle.putLong("trackId", track.getTrackId());
            Navigation.findNavController(requireView()).navigate(R.id.action_searchFragment_to_playerFragment, bundle);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}