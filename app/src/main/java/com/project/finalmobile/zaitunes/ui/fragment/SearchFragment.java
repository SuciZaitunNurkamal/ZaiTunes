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
import android.widget.Toast;

// Fragment untuk pencarian lagu
public class SearchFragment extends Fragment implements TrackAdapter.OnTrackClickListener {

    private FragmentSearchBinding binding;
    private ApiService apiService;
    private TrackAdapter trackAdapter;
    private RatedTrackHelper ratedTrackHelper;

    public SearchFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Inisialisasi API service dan database helper
        apiService = RetrofitClient.getApiService();
        ratedTrackHelper = RatedTrackHelper.getInstance(requireContext());
        ratedTrackHelper.open();
        setUpRecyclerView();

        // Setup tombol pencarian
        binding.searchButton.setOnClickListener(v -> {
            String searchQuery = binding.searchEditText.getText().toString().trim();
            if (!searchQuery.isEmpty()) {
                performSearch(searchQuery);
            }
        });
    }

    // Setup RecyclerView untuk hasil pencarian
    private void setUpRecyclerView() {
        trackAdapter = new TrackAdapter(new ArrayList<>(), this);
        binding.trackRv.setAdapter(trackAdapter);
        binding.trackRv.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    // Lakukan pencarian lagu
    private void performSearch(String searchQuery) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.trackRv.setVisibility(View.GONE);
        
        apiService.searchMusicSongs(searchQuery, 20).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(@NonNull Call<Response> call, @NonNull retrofit2.Response<Response> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<ResultsItem> tracks = response.body().getResults();
                    if (tracks != null && !tracks.isEmpty()) {
                        binding.trackRv.setVisibility(View.VISIBLE);
                        trackAdapter.updateData(tracks);
                    } else {
                        binding.trackRv.setVisibility(View.GONE);
                        Toast.makeText(requireContext(), getString(R.string.tidak_ada_hasil_ditemukan), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    binding.trackRv.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), getString(R.string.gagal_memuat_hasil_pencarian), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Response> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.trackRv.setVisibility(View.GONE);
                Toast.makeText(requireContext(), getString(R.string.error) + ": " + getString(R.string.periksa_koneksi), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTrackClick(ResultsItem track) {
        if (track != null) {
            ContentValues values = new ContentValues();
            values.put(TrackColumns.TRACK_ID, track.getTrackId());
            values.put(TrackColumns.TRACK_NAME, track.getTrackName());
            values.put(TrackColumns.ARTIST_NAME, track.getArtistName());
            values.put(TrackColumns.COLLECTION_NAME, track.getCollectionName());
            values.put(TrackColumns.GENRE, track.getPrimaryGenreName());
            values.put(TrackColumns.RELEASE_DATE, track.getReleaseDate());
            values.put(TrackColumns.TRACK_VIEW_URL, track.getTrackViewUrl());
            values.put(TrackColumns.ARTWORK_URL, track.getArtworkUrl100());
            values.put(TrackColumns.RATING, 0);

            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    // Cek apakah lagu sudah ada di database
                    Cursor cursor = null;
                    try {
                        cursor = ratedTrackHelper.queryById(String.valueOf(track.getTrackId()));
                        boolean exists = false;
                        if (cursor != null && cursor.moveToFirst()) {
                            exists = true;
                            android.util.Log.d("SearchFragment", "Track already exists in DB: " + track.getTrackId());
                        } else {
                            android.util.Log.d("SearchFragment", "Track does not exist in DB, attempting to insert: " + track.getTrackId());
                        }
                        if (cursor != null) {
                            cursor.close();
                        }

                        if (!exists) {
                            long id = ratedTrackHelper.insert(values);
                            if (id > 0) {
                                requireActivity().runOnUiThread(() -> {
                                    Toast.makeText(requireContext(), getString(R.string.lagu_berhasil_disimpan), Toast.LENGTH_SHORT).show();
                                    android.util.Log.d("SearchFragment", "Track saved successfully with ID: " + id);
                                });
                            } else {
                                requireActivity().runOnUiThread(() -> {
                                    Toast.makeText(requireContext(), getString(R.string.gagal_menyimpan_lagu), Toast.LENGTH_SHORT).show();
                                    android.util.Log.e("SearchFragment", "Failed to save track: insert returned ID <= 0");
                                });
                            }
                        }
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                } catch (Exception e) {
                    android.util.Log.e("SearchFragment", "Error saving track: " + e.getMessage());
                    e.printStackTrace();
                    requireActivity().runOnUiThread(() -> 
                        Toast.makeText(requireContext(), getString(R.string.gagal_menyimpan_lagu), Toast.LENGTH_SHORT).show()
                    );
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
        if (ratedTrackHelper != null) {
            ratedTrackHelper.close();
        }
        binding = null;
    }
}