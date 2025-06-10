package com.project.finalmobile.zaitunes.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.project.finalmobile.R;
import com.project.finalmobile.databinding.ItemTrackBinding;
import com.project.finalmobile.zaitunes.model.ResultsItem;
import java.util.List;

// Adapter untuk menampilkan daftar lagu di RecyclerView
public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {

    private List<ResultsItem> trackList;
    private OnTrackClickListener listener;

    public interface OnTrackClickListener {
        void onTrackClick(ResultsItem track);
    }

    // Constructor adapter
    public TrackAdapter(List<ResultsItem> trackList, OnTrackClickListener listener) {
        this.trackList = trackList;
        this.listener = listener;
    }

    // Update data adapter
    public void updateData(List<ResultsItem> newTrackList) {
        this.trackList = newTrackList;
        notifyDataSetChanged();
    }

    // Buat ViewHolder baru
    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTrackBinding binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TrackViewHolder(binding);
    }

    // Bind data ke ViewHolder
    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        ResultsItem currentTrack = trackList.get(position);
        holder.bind(currentTrack, listener);
    }

    // Jumlah item dalam adapter
    @Override
    public int getItemCount() {
        return trackList.size();
    }

    // ViewHolder untuk item lagu
    static class TrackViewHolder extends RecyclerView.ViewHolder {
        private final ItemTrackBinding binding;

        public TrackViewHolder(ItemTrackBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        // Bind data lagu ke view
        public void bind(ResultsItem track, OnTrackClickListener listener) {
            binding.titleTrack.setText(track.getTrackName());
            binding.artistName.setText(track.getArtistName());
            binding.albumName.setText(track.getCollectionName());
            binding.ratingBar.setVisibility(View.GONE);
            binding.deleteIcon.setVisibility(View.GONE);

            // Load gambar sampul dengan Glide
            Glide.with(itemView.getContext())
                .load(track.getArtworkUrl100())
                .placeholder(R.mipmap.ic_launcher_round)
                .into(binding.albumCover);

            // Setup click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTrackClick(track);
                }
            });
        }
    }
}