package org.joinmastodon.android.fragments.discover;

import android.net.Uri;
import android.os.Bundle;

import org.joinmastodon.android.api.requests.timelines.GetPublicTimeline;
import org.joinmastodon.android.api.session.AccountSessionManager;
import org.joinmastodon.android.fragments.StatusListFragment;
import org.joinmastodon.android.model.FilterContext;
import org.joinmastodon.android.model.Status;
import org.joinmastodon.android.ui.utils.DiscoverInfoBannerHelper;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import me.grishka.appkit.api.SimpleCallback;
import me.grishka.appkit.utils.MergeRecyclerAdapter;

public class FederatedTimelineFragment extends StatusListFragment{
	private DiscoverInfoBannerHelper bannerHelper;

	private String maxID;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		bannerHelper=new DiscoverInfoBannerHelper(DiscoverInfoBannerHelper.BannerType.FEDERATED_TIMELINE, accountID);
	}

	@Override
	protected void doLoadData(int offset, int count){
		currentRequest=new GetPublicTimeline(false, false, refreshing ? null : maxID, count, getLocalPrefs().timelineReplyVisibility)
				.setCallback(new SimpleCallback<>(this){
					@Override
					public void onSuccess(List<Status> result){
						if(!result.isEmpty())
							maxID=result.get(result.size()-1).id;
						boolean empty=result.isEmpty();
						AccountSessionManager.get(accountID).filterStatuses(result, FilterContext.PUBLIC);
						onDataLoaded(result, !empty);
						bannerHelper.onBannerBecameVisible();
					}
				})
				.exec(accountID);
	}

	@Override
	protected RecyclerView.Adapter<?> getAdapter(){
		MergeRecyclerAdapter adapter=new MergeRecyclerAdapter();
		bannerHelper.maybeAddBanner(list, adapter);
		adapter.addAdapter(super.getAdapter());
		return adapter;
	}

	@Override
	protected FilterContext getFilterContext() {
		return FilterContext.PUBLIC;
	}

	@Override
	public Uri getWebUri(Uri.Builder base) {
		return base.path(isInstanceAkkoma() ? "/main/all" : "/public").build();
	}
}
