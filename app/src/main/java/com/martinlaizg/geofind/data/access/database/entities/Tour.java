package com.martinlaizg.geofind.data.access.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.martinlaizg.geofind.data.enums.PlayLevel;
import com.martinlaizg.geofind.utils.DateUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "tours", foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id",
                                                       childColumns = "creator_id",
                                                       onDelete = CASCADE, onUpdate = CASCADE),
        indices = @Index("creator_id"))
public class Tour {

	@PrimaryKey
	private final int id;
	private String name;
	private String description;
	private String image;
	private PlayLevel min_level;
	private Date created_at;
	private Date updated_at;
	private Integer creator_id;
	private Date updated;

	@Ignore
	private List<Place> places;
	@Ignore
	private User creator;

	public Tour(Integer id, String name, String description, PlayLevel min_level, Date created_at,
			Date updated_at, Integer creator_id, Date updated) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.image = null;
		this.min_level = min_level;
		this.created_at = created_at;
		this.updated_at = updated_at;
		this.creator_id = creator_id;
		this.updated = updated;
	}

	@Ignore
	public Tour() {
		id = 0;
		name = "";
		description = "";
		min_level = PlayLevel.MAP;
		places = new ArrayList<>();
	}

	public int getId() {
		return id;
	}

	public Integer getCreator_id() {
		return creator_id;
	}

	public void setCreator_id(Integer creator_id) {
		this.creator_id = creator_id;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public Date getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public boolean isValid() {
		for(Place p : getPlaces()) {
			if(!p.isValid()) return false;
		}
		return getName() != null && !getName().isEmpty() && //
				getDescription() != null && !getDescription().isEmpty() && //
				getMin_level() != null;
	}

	public List<Place> getPlaces() {
		return places;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	//-------------------------

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public PlayLevel getMin_level() {
		return min_level;
	}

	public void setMin_level(PlayLevel min_level) {
		this.min_level = min_level;
	}

	public void setPlaces(List<Place> places) {
		this.places = places;
	}

	public boolean isOutOfDate() {
		return DateUtils.isDateExpire(updated);
	}

	public Date getUpdated() {
		return new Date(Calendar.getInstance().getTime().getTime());
	}

	public void setUpdated(Date updated) {
		if(updated == null) {
			this.updated = new Date(Calendar.getInstance().getTime().getTime());
		}
		this.updated = updated;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
