package edu.neu.madcourse.skilift.adapters;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import edu.neu.madcourse.skilift.R;
import edu.neu.madcourse.skilift.interfaces.IRideInfoClickListener;
import edu.neu.madcourse.skilift.models.RideInfo;
import edu.neu.madcourse.skilift.viewholders.RideInfoViewHolder;

public class RideInfoRVAdapter extends RecyclerView.Adapter<RideInfoViewHolder> {
    private final ArrayList<RideInfo> rideInfoList;
    private final IRideInfoClickListener rideInfoClickListener;
    private final Context context;

    public RideInfoRVAdapter(ArrayList<RideInfo> rideInfoList,
                             IRideInfoClickListener rideInfoClickListener, Context context) {
        this.rideInfoList = rideInfoList;
        this.rideInfoClickListener = rideInfoClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public RideInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ride_info_card,
                parent, false);
        return new RideInfoViewHolder(v, rideInfoClickListener);
    }

    // TODO: Get user profile data to set profile picture src, user rating
    // TODO: Look into why dates are 1970
    @Override
    public void onBindViewHolder(@NonNull RideInfoViewHolder holder, int position) {
        RideInfo rideInfo = rideInfoList.get(position);
        holder.username.setText(rideInfo.getUsername());
        holder.userRating.setRating(5);
        holder.departureLocation.setText(formatString(R.string.departureLocation,
                rideInfo.getDepartureLocation()));
        holder.destination.setText(formatString(
                R.string.destination, rideInfo.getDestination()));
        DateFormat dateFormat = new SimpleDateFormat("M/d/y 'at' h:mm a", Locale.US);
        holder.departureDate.setText(formatString(R.string.departureDate,
                dateFormat.format(new Date(rideInfo.getPickupDate()))));
        holder.returnDate.setText(formatString(R.string.returnDate,
                dateFormat.format(new Date(rideInfo.getReturnDate()))));
        // Set the profile picture
        setProfilePicture(holder.profilePicture, rideInfo.getUsername());
    }

    @Override
    public int getItemCount() {
        return rideInfoList.size();
    }

    private Spanned formatString(int resId, String text) {
        return Html.fromHtml(context.getString(resId, text), FROM_HTML_MODE_LEGACY);
    }

    private void setProfilePicture(ImageView profilePictureImageView, String username) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference profilePictureRef = storage.getReference().child("profile_pictures").child(username+".jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        profilePictureRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Set profile picture ImageView to downloaded data
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profilePictureImageView.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Could not get image, so set profile pic to default
                profilePictureImageView.setImageResource(R.drawable.default_user_img);
            }
        });
    }
}
