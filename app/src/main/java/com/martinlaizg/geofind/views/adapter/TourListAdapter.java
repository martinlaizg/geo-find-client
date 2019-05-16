package com.martinlaizg.geofind.views.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.martinlaizg.geofind.R;
import com.martinlaizg.geofind.data.access.database.entities.relations.TourCreatorPlaces;
import com.martinlaizg.geofind.views.fragment.single.TourFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TourListAdapter
		extends RecyclerView.Adapter<TourListAdapter.ToursViewHolder> {

	private List<TourCreatorPlaces> tours = new ArrayList<>();

	@NonNull
	@Override
	public ToursViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		View view = LayoutInflater.from(viewGroup.getContext())
				.inflate(R.layout.fragment_tour_item, viewGroup, false);
		return new ToursViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ToursViewHolder holder, final int i) {
		final TourCreatorPlaces tour = tours.get(i);
		holder.tourName.setText(tour.getTour().getName());
		holder.tourCreator.setText(tour.getUsername());
		holder.tourDescription.setText(tour.getTour().getDescription());

		Bundle b = new Bundle();
		b.putInt(TourFragment.TOUR_ID, tours.get(i).getTour().getId());
		holder.materialCardView
				.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.toTour, b));
	}

	@Override
	public int getItemCount() {
		return tours.size();
	}

	public void setTours(List<TourCreatorPlaces> tours) {
		this.tours = tours;
		notifyDataSetChanged();
	}

	class ToursViewHolder
			extends RecyclerView.ViewHolder {

		@BindView(R.id.tour_name)
		TextView tourName;
		@BindView(R.id.tour_creator)
		TextView tourCreator;
		@BindView(R.id.tour_description)
		TextView tourDescription;
		@BindView(R.id.tour_card)
		MaterialCardView materialCardView;

		ToursViewHolder(@NonNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}

}