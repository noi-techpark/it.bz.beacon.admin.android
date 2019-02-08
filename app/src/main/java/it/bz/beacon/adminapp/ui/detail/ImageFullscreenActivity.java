package it.bz.beacon.adminapp.ui.detail;

import android.content.Context;
import android.content.ContextWrapper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.bz.beacon.adminapp.R;

public class ImageFullscreenActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE_FILENAME = "EXTRA_IMAGE_FILENAME";


    @BindView(R.id.image)
    protected ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_fullscreen);


        ButterKnife.bind(this);

        if (getIntent() != null) {
            String fileName = getIntent().getStringExtra(EXTRA_IMAGE_FILENAME);

            ContextWrapper contextWrapper = new ContextWrapper(this);
            File directory = contextWrapper.getDir(getString(R.string.image_folder), Context.MODE_PRIVATE);
            final File file = new File(directory, fileName);

            if (file.exists()) {
                Picasso.with(this)
                        .load(file)
                        .placeholder(R.drawable.placeholder)
                        .into(img);
            }
        }

    }
}
