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

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {

    private List<ResultsItem> trackList;
    private OnTrackClickListener listener;

    // Interface HANYA untuk klik pada item
    public interface OnTrackClickListener {
        void onTrackClick(ResultsItem track);
    }

    public TrackAdapter(List<ResultsItem> trackList, OnTrackClickListener listener) {
        this.trackList = trackList;
        this.listener = listener;
    }

    public void updateData(List<ResultsItem> newTrackList) {
        this.trackList = newTrackList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTrackBinding binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TrackViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        ResultsItem currentTrack = trackList.get(position);
        holder.bind(currentTrack, listener);
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    static class TrackViewHolder extends RecyclerView.ViewHolder {
        private ItemTrackBinding binding;

        public TrackViewHolder(ItemTrackBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ResultsItem track, OnTrackClickListener listener) {
            binding.titleTrack.setText(track.getTrackName());
            binding.artistName.setText(track.getArtistName());
            binding.albumName.setText(track.getCollectionName());

            // Sembunyikan RatingBar dan Tombol Play di daftar pencarian
            binding.ratingBar.setVisibility(View.GONE);
            binding.deleteIcon.setVisibility(View.GONE);

            Glide.with(itemView.getContext())
                    .load(track.getArtworkUrl100())
                    .placeholder(R.mipmap.ic_launcher_round)
                    .into(binding.albumCover);

            // Set listener untuk seluruh item
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTrackClick(track);
                }
            });
        }
    }
}