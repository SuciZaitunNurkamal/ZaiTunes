package com.project.finalmobile.zaitunes.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.project.finalmobile.databinding.FragmentPlayerBinding;
import com.project.finalmobile.zaitunes.local.AppDatabase;
import com.project.finalmobile.zaitunes.local.RatedTrack;
import java.util.concurrent.Executors;
import androidx.navigation.Navigation;

public class PlayerFragment extends Fragment {

    private FragmentPlayerBinding binding;
    private AppDatabase db;
    private long trackId;
    private RatedTrack currentTrack;

    public PlayerFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ambil argumen secara manual menggunakan kunci "trackId"
        if (getArguments() != null) {
            this.trackId = getArguments().getLong("trackId");
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
        db = AppDatabase.getDatabase(requireContext());
        loadTrackDetails();
        binding.backButton.setOnClickListener(v -> {
            // Perintah untuk kembali ke fragment sebelumnya
            Navigation.findNavController(v).popBackStack();
        });
    }

    private void loadTrackDetails() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Ambil detail lagu dari database menggunakan ID
            currentTrack = db.ratedTrackDao().getTrackById(trackId);

            if (currentTrack != null && getActivity() != null) {
                // Tampilkan data ke UI di main thread
                getActivity().runOnUiThread(() -> {
                    updateUi(currentTrack);
                });
            }
        });
    }

    private void updateUi(RatedTrack track) {
        binding.trackNameTextView.setText(track.getTrackName());
        binding.artistNameTextView.setText(track.getArtistName());
        binding.detailRatingBar.setRating(track.getRating());
        binding.albumTextView.setText("Album: " + track.getCollectionName());
        binding.genreTextView.setText("Genre: " + track.getPrimaryGenreName());

        String releaseDate = track.getReleaseDate();
        if (releaseDate != null && !releaseDate.isEmpty()) {
            String year = releaseDate.substring(0, 4);
            binding.yearTextView.setText("Tahun: " + year);
        }

        Glide.with(this).load(track.getArtworkUrl100()).into(binding.albumArtImageView);

        // Atur listener untuk rating bar di halaman ini
        binding.detailRatingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                saveNewRating(rating);
            }
        });
    }

    private void saveNewRating(float rating) {
        currentTrack.setRating(rating);
        Executors.newSingleThreadExecutor().execute(() -> {
            db.ratedTrackDao().insert(currentTrack);
            // Beri feedback di main thread
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Rating updated!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}