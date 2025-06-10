package com.project.finalmobile.zaitunes.ui.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import com.project.finalmobile.R;
import com.project.finalmobile.databinding.FragmentPlayerBinding;
import com.project.finalmobile.zaitunes.local.DatabaseContract.TrackColumns;
import com.project.finalmobile.zaitunes.local.MappingHelper;
import com.project.finalmobile.zaitunes.local.RatedTrack;
import com.project.finalmobile.zaitunes.local.RatedTrackHelper;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class PlayerFragment extends Fragment {

    private FragmentPlayerBinding binding;
    private RatedTrackHelper ratedTrackHelper;
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
            Cursor cursor = null;
            try {
                cursor = ratedTrackHelper.queryById(String.valueOf(trackId));
                // Use MappingHelper to convert Cursor to List
                ArrayList<RatedTrack> list = MappingHelper.mapCursorToArrayList(cursor);

                // Debug logging
                if (cursor != null) {
                    android.util.Log.d("PlayerFragment", "Cursor count: " + cursor.getCount());
                    if (cursor.moveToFirst()) {
                        android.util.Log.d("PlayerFragment", "Track ID: " + cursor.getLong(cursor.getColumnIndexOrThrow(TrackColumns.TRACK_ID)));
                        android.util.Log.d("PlayerFragment", "Track Name: " + cursor.getString(cursor.getColumnIndexOrThrow(TrackColumns.TRACK_NAME)));
                        android.util.Log.d("PlayerFragment", "Artist Name: " + cursor.getString(cursor.getColumnIndexOrThrow(TrackColumns.ARTIST_NAME)));
                        android.util.Log.d("PlayerFragment", "Collection Name: " + cursor.getString(cursor.getColumnIndexOrThrow(TrackColumns.COLLECTION_NAME)));
                        android.util.Log.d("PlayerFragment", "Genre: " + cursor.getString(cursor.getColumnIndexOrThrow(TrackColumns.GENRE)));
                        android.util.Log.d("PlayerFragment", "Release Date: " + cursor.getString(cursor.getColumnIndexOrThrow(TrackColumns.RELEASE_DATE)));
                        android.util.Log.d("PlayerFragment", "Artwork URL: " + cursor.getString(cursor.getColumnIndexOrThrow(TrackColumns.ARTWORK_URL)));
                    }
                }

                // Make sure list is not empty before getting first item
                if (!list.isEmpty()) {
                    currentTrack = list.get(0);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> updateUi(currentTrack));
                    }
                } else {
                    android.util.Log.e("PlayerFragment", "List is empty!");
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), getString(R.string.gagal_memuat_detail_lagu), Toast.LENGTH_SHORT).show());
                    }
                }
            } catch (Exception e) {
                android.util.Log.e("PlayerFragment", "Error loading track details: " + e.getMessage());
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), getString(R.string.terjadi_kesalahan), Toast.LENGTH_SHORT).show());
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        });
    }

    private void updateUi(RatedTrack track) {
        if (binding == null || track == null) {
            android.util.Log.e("PlayerFragment", "Binding or track is null!");
            return;
        }

        // Debug logging
        android.util.Log.d("PlayerFragment", "Updating UI with track: " + track.getTrackName());
        android.util.Log.d("PlayerFragment", "Artist: " + track.getArtistName());
        android.util.Log.d("PlayerFragment", "Album: " + track.getCollectionName());
        android.util.Log.d("PlayerFragment", "Genre: " + track.getPrimaryGenreName());
        android.util.Log.d("PlayerFragment", "Artwork URL: " + track.getArtworkUrl100());

        binding.trackNameTextView.setText(track.getTrackName());
        binding.artistNameTextView.setText(track.getArtistName());
        binding.albumTextView.setText(getString(R.string.album) + ": " + track.getCollectionName());
        binding.genreTextView.setText(getString(R.string.genre) + ": " + track.getPrimaryGenreName());

        // Get year from release date
        String releaseDate = track.getReleaseDate();
        if (releaseDate != null && !releaseDate.isEmpty()) {
            String year = releaseDate.substring(0, 4);
            binding.yearTextView.setText(getString(R.string.tahun) + ": " + year);
        }

        // Load cover image
        String artworkUrl = track.getArtworkUrl100();
        if (artworkUrl != null && !artworkUrl.isEmpty()) {
            Glide.with(this)
                    .load(artworkUrl)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .into(binding.albumArtImageView);
        } else {
            android.util.Log.e("PlayerFragment", "Artwork URL is null or empty!");
            binding.albumArtImageView.setImageResource(R.mipmap.ic_launcher_round);
        }

        // Set up "View in Apple Music" button
        binding.openInMusicButton.setOnClickListener(v -> {
            String url = track.getTrackViewUrl();
            if (url != null && !url.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), getString(R.string.link_tidak_tersedia), Toast.LENGTH_SHORT).show();
            }
        });

        binding.detailRatingBar.setRating(track.getRating());
        binding.detailRatingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                saveNewRating(rating);
            }
        });
    }

    private void saveNewRating(float rating) {
        if (currentTrack == null) {
            android.util.Log.e("PlayerFragment", "Cannot save rating: currentTrack is null");
            return;
        }

        android.util.Log.d("PlayerFragment", "Saving new rating: " + rating + " for track: " + currentTrack.getTrackName());
        currentTrack.setRating(rating);
        ContentValues values = new ContentValues();
        values.put(TrackColumns.RATING, rating);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Use helper's update method
                int updatedRows = ratedTrackHelper.update(String.valueOf(currentTrack.getTrackId()), values);
                android.util.Log.d("PlayerFragment", "Updated " + updatedRows + " rows with new rating");
                
                if (updatedRows > 0) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), getString(R.string.rating_diperbarui), Toast.LENGTH_SHORT).show();
                            // Refresh the UI to show the new rating
                            binding.detailRatingBar.setRating(rating);
                        });
                    }
                } else {
                    android.util.Log.e("PlayerFragment", "Failed to update rating. No rows were updated.");
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), getString(R.string.gagal_memperbarui_rating), Toast.LENGTH_SHORT).show();
                            // Revert the rating bar to the previous value
                            binding.detailRatingBar.setRating(currentTrack.getRating());
                        });
                    }
                }
            } catch (Exception e) {
                android.util.Log.e("PlayerFragment", "Error saving rating: " + e.getMessage());
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), getString(R.string.terjadi_kesalahan), Toast.LENGTH_SHORT).show();
                        // Revert the rating bar to the previous value
                        binding.detailRatingBar.setRating(currentTrack.getRating());
                    });
                }
            }
        });
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