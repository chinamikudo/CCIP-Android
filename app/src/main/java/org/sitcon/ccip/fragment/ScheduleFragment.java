package org.sitcon.ccip.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.sitcon.ccip.R;
import org.sitcon.ccip.adapter.ScheduleAdapter;
import org.sitcon.ccip.model.Submission;
import org.sitcon.ccip.network.SITCONClient;
import org.sitcon.ccip.util.PreferenceUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleFragment extends TrackFragment {

    private Activity mActivity;
    RecyclerView scheduleView;
    SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        scheduleView = (RecyclerView) view.findViewById(R.id.schedule);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        mActivity = getActivity();
        scheduleView.setLayoutManager(new LinearLayoutManager(mActivity));
        scheduleView.setItemAnimator(new DefaultItemAnimator());

        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        Call<List<Submission>> submissionCall = SITCONClient.get().submission();
        submissionCall.enqueue(new Callback<List<Submission>>() {
            @Override
            public void onResponse(Call<List<Submission>> call, Response<List<Submission>> response) {
                if (response.isSuccessful()) {
                    swipeRefreshLayout.setRefreshing(false);

                    List<Submission> submissions = response.body();
                    PreferenceUtil.savePrograms(mActivity, submissions);

                    setScheduleAdapter(submissions);
                } else {
                    loadOfflineScedule();
                }
            }

            @Override
            public void onFailure(Call<List<Submission>> call, Throwable t) {
                loadOfflineScedule();
            }
        });

        return view;
    }

    public void loadOfflineScedule() {
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(mActivity, R.string.offline, Toast.LENGTH_LONG).show();
        List<Submission> submissions = PreferenceUtil.loadPrograms(mActivity);
        if (submissions != null) {
            setScheduleAdapter(submissions);
        }
    }

    public void setScheduleAdapter(List<Submission> submissions) {
        HashMap<String, List<Submission>> map = new HashMap();
        for (Submission submission : submissions) {
            if (submission.getStart() == null) continue;

            if (map.containsKey(submission.getStart())) {
                List<Submission> tmp = map.get(submission.getStart());
                tmp.add(submission);
                Collections.sort(tmp, new Comparator<Submission>() {
                    @Override
                    public int compare(Submission s1, Submission s2) {
                        return s1.getRoom().compareTo(s2.getRoom());
                    }
                });
                map.put(submission.getStart(), tmp);
            } else {
                List<Submission> list = new ArrayList();
                list.add(submission);
                map.put(submission.getStart(), list);
            }
        }

        SortedSet<String> keys = new TreeSet(map.keySet());
        List<List<Submission>> submissionSlotList = new ArrayList();
        for (String key : keys) {
            submissionSlotList.add(map.get(key));
            scheduleView.setAdapter(new ScheduleAdapter(mActivity, submissionSlotList));
        }
    }

}
