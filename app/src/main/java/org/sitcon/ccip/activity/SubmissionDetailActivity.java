package org.sitcon.ccip.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.internal.bind.util.ISO8601Utils;
import com.squareup.picasso.Picasso;

import org.sitcon.ccip.R;
import org.sitcon.ccip.model.Submission;
import org.sitcon.ccip.util.JsonUtil;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SubmissionDetailActivity extends TrackActivity {

    public static final String INTENT_EXTRA_PROGRAM = "program";
    private static final SimpleDateFormat SDF_DATETIME = new SimpleDateFormat("MM/dd HH:mm");
    private static final SimpleDateFormat SDF_TIME = new SimpleDateFormat("HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission_detail);

        final Submission submission = JsonUtil.fromJson(getIntent().getStringExtra(INTENT_EXTRA_PROGRAM), Submission.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(submission.getRoom());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView speakername, subject, time, type, lang, speakerInfo, programAbstract;
        ImageView appBarImage;
        speakername = (TextView) findViewById(R.id.speakername);
        subject = (TextView) findViewById(R.id.subject);
        time = (TextView) findViewById(R.id.time);
        type = (TextView) findViewById(R.id.type);
        lang = (TextView) findViewById(R.id.lang);
        speakerInfo = (TextView) findViewById(R.id.speakerinfo);
        programAbstract = (TextView) findViewById(R.id.program_abstract);
        appBarImage = (ImageView) findViewById(R.id.app_bar_image);

        Picasso.with(this).load("http://sitcon.org/2017/" + submission.getSpeaker().getAvatar()).into(appBarImage);

        speakername.setText(submission.getSpeaker().getName());
        subject.setText(submission.getSubject());

        try {
            StringBuffer timeString = new StringBuffer();
            Date startDate = ISO8601Utils.parse(submission.getStart(), new ParsePosition(0));
            timeString.append(SDF_DATETIME.format(startDate));
            timeString.append(" ~ ");
            Date endDate = ISO8601Utils.parse(submission.getEnd(), new ParsePosition(0));
            timeString.append(SDF_TIME.format(endDate));

            timeString.append(", " + ((endDate.getTime() - startDate.getTime()) / 1000 / 60) + getResources().getString(R.string.min));

            time.setText(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        type.setText(submission.getType());

        speakerInfo.setText(submission.getSpeaker().getBio());
        programAbstract.setText(submission.getSummary());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
