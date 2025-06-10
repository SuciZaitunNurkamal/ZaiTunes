package com.project.finalmobile.zaitunes.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.project.finalmobile.R;
import com.project.finalmobile.databinding.FragmentHomeBinding;
import com.project.finalmobile.zaitunes.local.AppDatabase;
import com.project.finalmobile.zaitunes.local.RatedTrack;
import com.project.finalmobile.zaitunes.api.ApiService;
import com.project.finalmobile.zaitunes.api.RetrofitClient;
import com.project.finalmobile.zaitunes.model.ResultsItem;
import com.project.finalmobile.zaitunes.model.RssResponse;
import com.project.finalmobile.zaitunes.ui.adapter.PopularSongAdapter;
import com.project.finalmobile.zaitunes.ui.adapter.RatedTrackAdapter;
import com.project.finalmobile.zaitunes.ui.adapter.DisplayMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements PopularSongAdapter.OnTrackClickListener, RatedTrackAdapter.OnTrackClickListener {

    private FragmentHomeBinding binding;
    private AppDatabase db;
    private ApiService apiService;
    private RatedTrackAdapter ratedTrackAdapter;
    private PopularSongAdapter popularSongAdapter;

    public HomeFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = AppDatabase.getDatabase(requireContext());
        apiService = RetrofitClient.getApiService();
        setupRecyclerViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecentlyRatedTracks();
        fetchPopularSongs();
    }

    private void setupRecyclerViews() {
        ratedTrackAdapter = new RatedTrackAdapter(new ArrayList<>(), this, DisplayMode.RECENT);
        binding.recentlyRatedRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recentlyRatedRv.setAdapter(ratedTrackAdapter);
        binding.recentlyRatedRv.setNestedScrollingEnabled(false);

        popularSongAdapter = new PopularSongAdapter(new ArrayList<>(), this);
        binding.popularSongsRv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.popularSongsRv.setAdapter(popularSongAdapter);
    }

    private void loadRecentlyRatedTracks() {
        Executors.newSingleThreadExecutor().execute(() -> {
            final List<RatedTrack> tracks = db.ratedTrackDao().getAllRatedTracks();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    if (tracks.isEmpty()) {
                        binding.recentlyRatedRv.setVisibility(View.GONE);
                        binding.emptyRatedText.setVisibility(View.VISIBLE);
                    } else {
                        binding.recentlyRatedRv.setVisibility(View.VISIBLE);
                        binding.emptyRatedText.setVisibility(View.GONE);
                        ratedTrackAdapter.updateData(tracks);
                    }
                });
            }
        });
    }

    private void fetchPopularSongs() {
        binding.popularSongsProgressBar.setVisibility(View.VISIBLE);
        String topSongsUrl = "https://rss.applemarketingtools.com/api/v2/id/music/most-played/20/songs.json";
        apiService.getTopSongs(topSongsUrl).enqueue(new Callback<RssResponse>() {
            @Override
            public void onResponse(@NonNull Call<RssResponse> call, @NonNull Response<RssResponse> response) {
                if (binding == null) return;
                binding.popularSongsProgressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<ResultsItem> popularTracks = response.body().getFeed().getResults();
                    if (popularTracks == null || popularTracks.isEmpty()) {
                        binding.popularSongsRv.setVisibility(View.GONE);
                        binding.emptyPopularText.setVisibility(View.VISIBLE);
                    } else {
                        binding.popularSongsRv.setVisibility(View.VISIBLE);
                        binding.emptyPopularText.setVisibility(View.GONE);
                        popularSongAdapter.updateData(popularTracks);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<RssResponse> call, @NonNull Throwable t) {
                if (binding == null) return;
                Log.e("HomeFragment", "API call for top songs failed.", t);
                binding.popularSongsProgressBar.setVisibility(View.GONE);
                binding.popularSongsRv.setVisibility(View.GONE);
                binding.emptyPopularText.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onPopularSongClick(ResultsItem song) {
        if (binding == null || song == null) return;
        RatedTrack trackToSave = new RatedTrack();
        trackToSave.setTrackId(song.getTrackId() != 0 ? song.getTrackId() : System.currentTimeMillis());
        trackToSave.setTrackName(song.getTrackName());
        trackToSave.setArtistName(song.getArtistName());
        trackToSave.setArtworkUrl100(song.getArtworkUrl100());
        trackToSave.setRating(0);
        trackToSave.setCollectionName(song.getCollectionName());
        trackToSave.setPrimaryGenreName(song.getPrimaryGenreName());
        trackToSave.setReleaseDate(song.getReleaseDate());

        Executors.newSingleThreadExecutor().execute(() -> db.ratedTrackDao().insertOrUpdate(trackToSave));
        Bundle bundle = new Bundle();
        bundle.putLong("trackId", trackToSave.getTrackId());
        Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_playerFragment, bundle);
    }

    @Override
    public void onRatedTrackClick(RatedTrack track) {
        if (binding == null || track == null) return;
        Bundle bundle = new Bundle();
        bundle.putLong("trackId", track.getTrackId());
        Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_playerFragment, bundle);
    }

    // --- IMPLEMENTASI FUNGSI DELETE ---
    @Override
    public void onDeleteClick(RatedTrack track) {
        if (binding == null || track == null) return;
        new AlertDialog.Builder(requireContext())
                .setTitle("Hapus Lagu")
                .setMessage("Anda yakin ingin menghapus '" + track.getTrackName() + "'?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        db.ratedTrackDao().delete(track);
                    });
                })
                .setNegativeButton("Batal", null)
                .show();
    }
    // ---------------------------------

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}