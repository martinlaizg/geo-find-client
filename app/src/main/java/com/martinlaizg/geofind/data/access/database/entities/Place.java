package com.martinlaizg.geofind.data.access.database.entities;

import android.location.Location;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;
import com.martinlaizg.geofind.utils.DateUtils;

import java.sql.Date;
import java.util.Calendar;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "places", foreignKeys = @ForeignKey(entity = Tour.class, parentColumns = "id",
                                                        childColumns = "tour_id",
                                                        onDelete = CASCADE),
        indices = @Index("tour_id"))
public class Place {

	@PrimaryKey
	private final int id;
	private String name;
	private Double lat;
	private Double lon;
	private Integer tour_id;
	private String description;
	private Integer order;
	private Date created_at;
	private Date updated_at;
	private Date updated;

	// Question
	private String question;
	private String answer;
	private String answer2;
	private String answer3;
	private String image;

	public Place(Integer id, String name, Double lat, Double lon, Integer tour_id,
			String description, Integer order, Date created_at, Date updated_at, Date updated) {
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.tour_id = tour_id;
		this.description = description;
		this.order = order;
		this.created_at = created_at;
		this.updated_at = updated_at;
		this.updated = updated;
	}

	@Ignore
	public Place() {
		id = 0;
		this.name = "";
		this.description = "";
	}

	public int getId() {
		return id;
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

	public Integer getTour_id() {
		return tour_id;
	}

	public void setTour_id(Integer tour_id) {
		this.tour_id = tour_id;
	}

	public LatLng getPosition() {
		if(lat == null || lon == null) return null;
		return new LatLng(lat, lon);
	}

	public void setPosition(LatLng position) {
		setLat(position.latitude);
		setLon(position.longitude);
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	boolean isValid() {
		return getName() != null && !getName().isEmpty() && getDescription() != null &&
				!getDescription().isEmpty() && getLat() != null && getLon() != null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public Double getLat() {
		return lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public boolean isOutOfDate() {
		return DateUtils.isDateExpire(updated);
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getAnswer2() {
		return answer2;
	}

	public void setAnswer2(String answer2) {
		this.answer2 = answer2;
	}

	public String getAnswer3() {
		return answer3;
	}

	public void setAnswer3(String answer3) {
		this.answer3 = answer3;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Location getLocation() {
		if(lat == null || lon == null) return null;
		Location l = new Location("");
		l.setLatitude(this.lat);
		l.setLongitude(this.lon);
		return l;
	}

	public void setLocation(Location location) {
		if(location == null) return;
		lat = location.getLatitude();
		lon = location.getLongitude();
	}

	public void setCompleteQuestion(String question, String correctAnswer, String secondAnswer,
			String thirdAnswer) {
		this.question = question;
		this.answer = correctAnswer;
		this.answer2 = secondAnswer;
		this.answer3 = thirdAnswer;
	}
}
